import 'package:flutter/services.dart';

import 'models/bt_device_model.dart';

class ScannerGun {
  static const MethodChannel _channel = const MethodChannel('scanner_gun');

  /// 开始扫描周边蓝牙设备
  static Future<void> scan() async {
    await _channel.invokeMethod("scan");
  }

  /// 获取设备列表
  static Future<List<BtDeviceModel>> getDevices() async {
    String jsonStr = await _channel.invokeMethod("devices");
    if (jsonStr.isEmpty) return [];
    return permissionModelFromJson(jsonStr);
  }

  /// 连接蓝牙设备
  static Future<void> connect(BtDeviceModel device) async {
    Map map = Map();
    map["address"] = device.address;
    _channel.invokeMethod("connect", map);
  }

  /// 获取本地绑定的设备
  static Future<List<BtDeviceModel>> getBindDevices() async {
    String jsonStr = await _channel.invokeMethod("bindedDrivers");
    return permissionModelFromJson(jsonStr);
  }

  /// 关闭发现设备 ( 取消扫描 )
  static Future<void> cancelDiscovery() async {
    await _channel.invokeMethod("cancelDiscovery");
  }

  // 关闭socket连接
  static Future<void> closeSocket() async {
    await _channel.invokeMethod("colseConnect");
  }

  // 判断是否已经连接
  static Future<bool?> isConnect(BtDeviceModel deviceModel) async {
    Map map = Map();
    map["address"] = deviceModel.address;
    return await _channel.invokeMethod("isConnect", map);
  }
}
