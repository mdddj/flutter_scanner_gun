import 'dart:convert';

import 'package:scanner_gun/models/bt_device_model.dart';

class ScannerGunUtil {
  /// 获取插件返回的数据类型 type
  static String? getDataType(Object event) {
    try {
      Map map = json.decode(event.toString());
      return map["dataType"];
    } catch (e, s) {
      print(e);
      print(s);
      return "";
    }
  }

  /// 获取插件返回的蓝牙设备信息obj
  static BtDeviceModel? getDevice(Object event) {
    try {
      Map map = json.decode(event.toString());
      return BtDeviceModel.fromJson(map["data"]);
    } catch (e) {
      return null;
    }
  }

  /// 获取插件返回的蓝牙设备信息obj
  static String? getData(Object event) {
    try {
      Map map = json.decode(event.toString());
      return map["data"];
    } catch (e) {
      return "";
    }
  }
}
