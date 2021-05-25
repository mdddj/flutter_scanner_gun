package shop.itbug.scanner_gun.util;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
/**
 * 监听蓝牙广播-各种状态
 */
public class BtReceiver extends BroadcastReceiver {
    private static final String TAG = BtReceiver.class.getSimpleName();
    private final Listener mListener;

    public BtReceiver(Context cxt, Listener listener) {
        mListener = listener;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开关状态
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//蓝牙开始搜索
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索结束

        filter.addAction(BluetoothDevice.ACTION_FOUND);//蓝牙发现新设备(未配对的设备)
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);//在系统弹出配对框之前(确认/输入配对码)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备配对状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//最底层连接建立
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//最底层连接断开

        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED); //BluetoothAdapter连接状态
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //BluetoothHeadset连接状态
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED); //BluetoothA2dp连接状态
        cxt.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
//        Log.i(TAG, "=======================" + action);
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                mListener.discoverFinish();
                break;
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MAX_VALUE);
                mListener.foundDev(dev,rssi);
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST: //在系统弹出配对框之前，实现自动配对，取消系统配对框
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                Log.i(TAG, "BOND_STATE: " + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                mListener.connectDisconnection();
                break;

            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                break;
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                break;
        }
    }

    public interface Listener {

        // 发现新设备
        void foundDev(BluetoothDevice dev,int signalIntensity);

        // 扫描结束
        void discoverFinish();

        // 连接断开
        void connectDisconnection();
    }
}