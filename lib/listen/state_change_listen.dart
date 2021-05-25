// 监听事件
import 'dart:async';

import 'package:flutter/services.dart';

class ScannerGunListing {
  static const EventChannel _eventChannel =
      const EventChannel('itbug/shop/scangun');
  static late StreamSubscription _subscription;

  // 开始监听返回数据
  static void listener(Function eventHandle, {Function? error}) {
    _subscription = _eventChannel.receiveBroadcastStream().listen(eventHandle as void Function(dynamic)?,
        onError: error ??
            (Object error) {
              print(error.toString());
            });
  }

  // 关闭监听
  static void cancal() {
    _subscription.cancel();
  }
}
