import 'dart:async';
import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:scanner_gun/models/bt_device_model.dart';
import 'package:scanner_gun/scanner_gun.dart';

mixin ScanGunMixin<T extends StatefulWidget> on State<T> {
  static const EventChannel _eventChannel =
      const EventChannel('itbug/shop/scangun');
  late StreamSubscription _streamSubscription;

  List<BtDeviceModel> blueDevices = []; // 设备列表
  bool isDiscoverFinish = false; // 是否在搜索中

  @override
  void initState() {
    super.initState();
    _streamSubscription = _eventChannel
        .receiveBroadcastStream()
        .listen(_onEvent, onError: _onError);
  }

  void _onEvent(Object? event) {
    Map map = json.decode(event as String);
    allData(event);
    if (map["dataType"] == "newDevice") {
      //扫描到新设备
      BtDeviceModel device = BtDeviceModel.fromJson(map["data"]);
      newDevice(device);
      deviceScaned(device);
    } else if (map["dataType"] == "code") {
      scanCode(map["data"]);
    } else if (map["dataType"] == "onConnected") {
      onConnectSuccess();
    } else if (map["dataType"] == "onClosed") {
      onConnectClose();
    } else if (map["dataType"] == "onConnectFailed") {
      onConnectFailed();
    } else if (map["dataType"] == "discoveryFinished") {
      // 发现设备结束
      print("扫描设备完成");
      setState(() {
        isDiscoverFinish = true;
      });
    }
  }

  void _onError(Object error) {
    print('返回的错误');
  }

  /// 扫码枪获取到数据
  void scanCode(String? code);

  // 扫描到新设备
  void newDevice(BtDeviceModel device) {
    this.blueDevices.add(device);
    setState(() {
      blueDevices = blueDevices;
    });
  }

  /// 扫描到新设备时
  void deviceScaned(BtDeviceModel device);

  /// 蓝牙连接成功时
  void onConnectSuccess();

  /// 蓝牙连接断开是
  void onConnectClose();

  /// 用户主动连接,失败的
  void onConnectFailed();

  // 重新扫描
  Future<void> reScan() async {
    if (isDiscoverFinish) {
      this.setState(() {
        blueDevices = [];
      });
      await scan();
    }
  }

  void allData(Object? event);

  // 开始扫描
  Future<void> scan() async {
    setState(() {
      isDiscoverFinish = false;
    });
    await ScannerGun.scan();
  }

  /// 关闭流通道
  void cancelSubscription() {
    _streamSubscription.cancel();
  }

  @override
  void dispose() {
    super.dispose();
  }
}
