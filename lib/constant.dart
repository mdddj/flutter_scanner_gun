/// 扫描枪连接状态
class BlueScanGunConnectState {
  /// 串口服务连接正常
  static final int SERIAL_PORT_SUCCESS = 0;

  /// socket 服务连接正常
  static final int SOCKET_SUCCESS = 1;

  /// 串口服务连接失败
  static final int SERIAL_PORT_ERROR = -100;

  /// socket 服务连接失败
  ///
  /// 当app 蓝牙连接扫码枪后连接失败将返回-101
  static final int SOCKET_ERROR = -101;

  /// 关闭串口服务将返回 (成功)
  static final int SERIAL_PORT_CLOSE_SUCCESS = 2;

  /// 关闭串口服务将返回 (失败)
  static final int SERIAL_PORT_CLOSE_ERROR = -102;

  /// 当扫码枪长时间没有和app 收发数据
  ///
  /// 将返回此状态码
  ///
  /// 可以做一些处理
  static final int SOCKET_CLOSEED = -103;

  /// 未知状态码
  static final int UNKOW = -1000;
}
