package shop.itbug.scanner_gun.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import shop.itbug.scanner_gun.entry.ReadDataModel;

/**
 * 客户端和服务端的基类，用于管理socket长连接
 */
public class BtBase {
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int FLAG_MSG = 0;  //消息标记

    private BluetoothSocket mSocket;
    private DataOutputStream mOut;
    private Listener mListener;
    private boolean isRead;
    private boolean isSending;

    BtBase(Listener listener) {
        mListener = listener;
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    void loopRead(BluetoothSocket socket) {
        mSocket = socket;
        try {
            if (!mSocket.isConnected())
                mSocket.connect();
            mListener.socketNotify(Listener.CONNECTED, mSocket.getRemoteDevice());
            longConnect();
            mOut = new DataOutputStream(mSocket.getOutputStream());
            InputStream inputStream = this.mSocket.getInputStream();
            isRead = true;
            int bytes;
            while (isRead) { //死循环读取
                byte[] buffer = new byte[100];
                try {
                    bytes = inputStream.read(buffer);
                    ReadDataModel readDataModel = new ReadDataModel();
                    readDataModel.setBytes(bytes);
                    readDataModel.setDatas(buffer);
                    mListener.socketNotify(Listener.CODE, readDataModel);
                } catch (IOException e) {
                    close();
                }
            }
        } catch (Throwable e) {
            mListener.socketNotify(Listener.CONNECTERROR, "连接失败");
//            e.printStackTrace();
            close();
        }
    }

    /// 构建长连接,一直发送数据
    private void longConnect() {
        Util.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                while (isRead) {
                    try{
                        if (mSocket.isConnected())
                            sendMsg("0");
                    }catch (Exception e){
                        close();
                    }

                }
            }
        });
    }

    /**
     * 发送短消息
     */
    public void sendMsg(String msg) {
        if (checkSend()) return;
        isSending = true;
        try {
            mOut.writeInt(FLAG_MSG); //消息标记
            mOut.writeUTF(msg);
            mOut.flush();
        } catch (Throwable e) {
            close();
        }
        isSending = false;
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void unListener() {
        mListener = null;
    }

    /**
     * 关闭Socket连接
     */
    public void close() {
        if (mSocket != null) {
            try {
                isRead = false;
                mSocket.close();
                mListener.socketNotify(Listener.DISCONNECTED,"连接断开");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (mSocket != null && mSocket.isConnected());
        if (dev == null)
            return connected;
        return connected && mSocket.getRemoteDevice().getAddress().equals(dev.getAddress());
    }

    // ============================================通知UI===========================================================
    private boolean checkSend() {
        if (isSending) {
            Log.d("BtBase", "正在发送其它数据,请稍后再发...");
            return true;
        }
        return false;
    }


    public interface Listener {
        int DISCONNECTED = 0;
        int CONNECTED = 1;
        int CONNECTERROR = 2;
        int CODE = 3;

        void socketNotify(int state, Object obj);
    }
}
