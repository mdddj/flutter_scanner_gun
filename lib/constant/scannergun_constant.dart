class ScannerGunConstant {
  /// 发现周围蓝牙结束后将触发
  static const String DISCOVERY_FINISHED = "discoveryFinished";

  /// 扫描到的周边蓝牙设备
  static const String SCANED_DEVICE = "newDevice";

  /// 连接成功
  static const String CONNECT_SUCCESS = "onConnected";

  /// 连接断开事件
  static const String CONNECT_DIS = "onClose";

  /// 主动连接失败事件
  static const String CONNECT_FAIL = "onConnectFailed";

  /// 数据
  static const String DATA = "code";
}
