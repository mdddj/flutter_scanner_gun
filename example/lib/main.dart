import 'package:flutter/material.dart';
import 'package:scanner_gun/models/bt_device_model.dart';
import 'package:scanner_gun/scan_gun_mixin.dart';
import 'package:scanner_gun/scanner_gun.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with ScanGunMixin<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('蓝牙示例'),
        ),
        body: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Column(
                children: [
                  RaisedButton(
                    child: Text("开始扫描"),
                    onPressed: () async {
                      await ScannerGun.scan();
                    },
                  ),
                  RaisedButton(
                    child: Text("关闭socket"),
                    onPressed: () async {
                      await ScannerGun.closeSocket();
                    },
                  ),
                ],
              ),
              if (blueDevices.isNotEmpty)
                Column(
                  children: blueDevices
                      .map((e) => InkWell(
                            onTap: () async {
                              await ScannerGun.connect(e);
                            },
                            child: ListTile(
                              title: Text(e.name ?? "空名字"),
                              subtitle: Text(e.address),
                            ),
                          ))
                      .toList(),
                ),
              if (blueDevices.isEmpty) Text("蓝牙列表为空")
            ],
          ),
        ),
      ),
    );
  }

  @override
  void scanCode(String code) {
    // TODO: implement scanCode
    print("flutter 收到数据了:$code");
  }

  @override
  void state(int state) {
    // TODO: implement state
    print("蓝牙连接状态:$state");
    if (state == -100) {
      // socket 连接断开 ,重连操作
    }
  }

  @override
  void deviceScaned(BtDeviceModel device) {
    print("flutter app :扫描到新设备啦~~~");
  }

  @override
  void onConnectClose() {
    print("flutter app :连接断开啦~~~~");
  }

  @override
  void onConnectSuccess() {
    print("flutter app :连接成功啦~~~~");
  }

  @override
  void onConnectFailed() {
    print("flutter app :连接失败啦~~~");
  }

  @override
  void eventData(Object event) {
    // TODO: implement eventData
  }

  @override
  void allData(Object event) {
    // TODO: implement allData
  }
}
