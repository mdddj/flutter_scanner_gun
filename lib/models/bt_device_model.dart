// To parse this JSON data, do
//
//     final permissionModel = permissionModelFromJson(jsonString);

import 'dart:convert';

List<BtDeviceModel> permissionModelFromJson(String str) =>
    List<BtDeviceModel>.from(
        json.decode(str).map((x) => BtDeviceModel.fromJson(x)));

String permissionModelToJson(List<BtDeviceModel> data) =>
    json.encode(List<dynamic>.from(data.map((x) => x.toJson())));

class BtDeviceModel {
  BtDeviceModel({
    this.address,
    this.name,
    this.signalIntensity,
  });

  String? address;
  String? name;
  int? signalIntensity;

  factory BtDeviceModel.fromJson(Map<String, dynamic> json) => BtDeviceModel(
        address: json["address"],
        name: json["name"],
        signalIntensity: json["signalIntensity"],
      );

  Map<String, dynamic> toJson() => {
        "address": address,
        "name": name,
        "signalIntensity": signalIntensity,
      };
}
