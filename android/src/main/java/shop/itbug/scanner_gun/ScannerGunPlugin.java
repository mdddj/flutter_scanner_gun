package shop.itbug.scanner_gun;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import shop.itbug.scanner_gun.entry.BtDeviceModel;
import shop.itbug.scanner_gun.entry.ReadDataModel;
import shop.itbug.scanner_gun.util.BtBase;
import shop.itbug.scanner_gun.util.BtClient;
import shop.itbug.scanner_gun.util.BtReceiver;

public class ScannerGunPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, BtReceiver.Listener, BtBase.Listener {

    private final String TAG = "ScannerGunPlugin_TAG";
    private MethodChannel channel;
    private EventChannel eventChannel;
    private Context context;
    private BtReceiver mBtReceiver;
    private final BtClient mClient = new BtClient(this);
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final List<BluetoothDevice> deviceLists = new ArrayList<>(); // 设备列表
    public static EventChannel.EventSink sink;// 传输数据通道
    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());



    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.d(TAG, "插件启动");
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "scanner_gun");
        channel.setMethodCallHandler(this);
        this.context = flutterPluginBinding.getApplicationContext();
        String CHANNEL_NAME = "itbug/shop/scangun";
        this.eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                ScannerGunPlugin.sink = events;
            }

            @Override
            public void onCancel(Object arguments) {

            }
        });
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                adapter.enable();
            }
        }

    }

    /**
     * 2020年10月23日13:12:09 梁典典
     * <p>
     * 获取手机已经绑定的设备列表
     *
     * @return 蓝牙设备列表
     */
    private List<BtDeviceModel> getBindDervis() {
        List<BtDeviceModel> ds = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() != 0) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName() != null ? device.getName() : "未知设备";
                ds.add(new BtDeviceModel(name, device.getAddress(), -1));
            }
        }
        return ds;
    }


    /**
     * 2020年10月23日13:08:31 梁典典
     * <p>
     * 开始注册蓝牙监听广播
     * 并开始扫描设备
     */
    private void registerBluetoothBroadcastAndStartScanDevices() {
        mBtReceiver = new BtReceiver(this.context, this);
        this.mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "scan":
                if(this.mBtReceiver!=null){
                    this.context.unregisterReceiver(mBtReceiver);
                    this.mBtReceiver = null;
                }
                Log.d(TAG,"开始扫描蓝牙设备");
                this.deviceLists.clear();
                List<BtDeviceModel> bindDervis1 = getBindDervis();
                for (BtDeviceModel item : bindDervis1){
                    ScannerGunPlugin.sink.success(JSONObject.toJSONString(new EventResultData<>("newDevice", item)));
                }
                registerBluetoothBroadcastAndStartScanDevices();
                break;
            case "connect":
                // 连接设备
                if (this.mBluetoothAdapter.isDiscovering())
                    this.mBluetoothAdapter.cancelDiscovery();
                String address = call.argument("address");
                BluetoothDevice device = getDeviceByAddressInList(address);
                if (device == null) {
                    Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
                    Log.d(TAG,"已绑定列表设备数量:"+bondedDevices.size());
                    if(bondedDevices.size()!=0){
                        for (BluetoothDevice bluetoothDevice : bondedDevices) {
                            if (bluetoothDevice.getAddress().equals(address)) {
                                Log.d(TAG, "在绑定列表中找到了设备");
                                device = bluetoothDevice;
                                break;
                            }
                        }
                    }
                }else{
                    List<BtDeviceModel> bindDervis = getBindDervis();
                    Log.d(TAG,"已绑定列表设备数量:"+bindDervis.size());
                }
                if (device != null) {
                    Log.d(TAG, "正在连接" + device.getName());
                    if (mClient.isConnected(device)) {
                        Log.d(TAG, "已经连接了....");
                        sendScanCodeToFlutterApp("onConnected", "连接成功");
                        return;
                    }
                    Log.d(TAG, "未连接,正在连接....");
                    mClient.connect(device);
                }
                break;
            case "bindedDrivers":
                // 获取已经绑定的设备
                List<BtDeviceModel> bindDervis = getBindDervis();
                result.success(JSONObject.toJSONString(bindDervis));
                break;
            case "cancelDiscovery":
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                break;
            case "colseConnect":
                // 关闭socket连接
                mClient.close();
                result.success(true);
                break;
            case "devices":
                // 获取设备列表
                result.success(JSONObject.toJSONString(this.deviceLists));
                break;
            case "isConnect":
                // 是否已经连接了
                String address1 = call.argument("address");
                Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
                BluetoothDevice device1 = null;
                for (BluetoothDevice bluetoothDevice : bondedDevices){
                    if(bluetoothDevice.getAddress().equals(address1)){
                        device1 = bluetoothDevice;
                        break;
                    }
                }
                if(device1!=null){
                    if (mClient.isConnected(device1)) {
                        result.success(true);
                        return;
                    }
                }
                result.success(false);
                break;
            default:
                result.notImplemented();
                break;
        }

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        Log.d(TAG, "插件 注销...");
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        mClient.unListener();
    }

    /**
     * 发送动态数据到flutter应用
     *
     * @param dataType 数据类型
     * @param code     返回的数据json字符串
     */
    public void sendScanCodeToFlutterApp(final String dataType, final String code) {
        if (ScannerGunPlugin.sink != null)

            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    ScannerGunPlugin.sink.success(JSONObject.toJSONString(new EventResultData<>(dataType, code)));
                }
            });

    }

    /**
     * 在当前列表中根据mac地址查找设备
     *
     * @param address 蓝牙设备的mac地址
     * @return dev
     */
    private BluetoothDevice getDeviceByAddressInList(String address) {
        BluetoothDevice resultDev = null;
        for (int i = 0; i < this.deviceLists.size(); i++) {
            BluetoothDevice item = deviceLists.get(i);
            if (item.getAddress().equals(address)) {
                resultDev = item;
                break;
            }
        }
        return resultDev;
    }


    // 查找到设备
    @Override
    public void foundDev(BluetoothDevice dev, int signalIntensity) {
        boolean isInList = getDeviceByAddressInList(dev.getAddress()) != null;// 是否存在于列表中
        if (!isInList) {
            this.deviceLists.add(dev);
            BtDeviceModel model = new BtDeviceModel(dev.getName() == null ? "未知设备" : dev.getName(), dev.getAddress(), signalIntensity);
            if (ScannerGunPlugin.sink != null) {
                ScannerGunPlugin.sink.success(JSONObject.toJSONString(new EventResultData<>("newDevice", model)));
            }
        }
    }

    // 查找结束
    @Override
    public void discoverFinish() {
        if (ScannerGunPlugin.sink != null) {
            ScannerGunPlugin.sink.success(JSONObject.toJSONString(new EventResultData<>("discoveryFinished", "扫描结束")));
        }
    }

    // 连接断开
    @Override
    public void connectDisconnection() {
        if (ScannerGunPlugin.sink != null) {
            ScannerGunPlugin.sink.success(JSONObject.toJSONString(new EventResultData<>("onClosed", "连接断开")));
        }
    }


    @Override
    public void socketNotify(int state, Object obj) {
        switch (state) {
            case BtBase.Listener.CONNECTED:
                sendScanCodeToFlutterApp("onConnected", "连接成功");
                break;
            case BtBase.Listener.DISCONNECTED:
                sendScanCodeToFlutterApp("onClosed", "连接断开");
                break;
            case BtBase.Listener.CODE:
                ReadDataModel readDataModel = (ReadDataModel) obj;
                byte[] buffer = readDataModel.getDatas();
                StringBuilder stringBuffer = new StringBuilder();
                for (int i = 0; i < readDataModel.getBytes(); i++) {
                    char c = (char) buffer[i];
                    stringBuffer.append(c);
                }
                final String scanData = stringBuffer.toString().replaceAll(" ", "");
                sendScanCodeToFlutterApp("code", scanData);
                break;
            case BtBase.Listener.CONNECTERROR:
                sendScanCodeToFlutterApp("onConnectFailed", "连接失败");
                break;
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Activity activity = binding.getActivity();
        binding.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
            @Override
            public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                Log.d(TAG, requestCode + "");
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
            for (String str : permissions) {
                if (ContextCompat.checkSelfPermission(activity, str) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, permissions, 1);
                    break;
                }
            }
        }

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        Log.d(TAG, "actity 注销...");
        if (this.mBtReceiver != null) {
            context.unregisterReceiver(mBtReceiver);
        }
    }
}
