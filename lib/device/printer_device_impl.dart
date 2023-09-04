import 'package:flutter/services.dart';
import 'package:vrgpsdk/device/printer_device.dart';

class PrinterDeviceImpl implements PrinterDevice {
  static const String vrGPSdkChannelName = "VR_GPSDK";

  final String methodCheckConnectionPrinter = "CHECK_CONNECTION_PRINTER";
  final String methodPrintData = "PRINT_DATA";

  late MethodChannel _methodChannel;

  PrinterDeviceImpl() {
    _methodChannel = const MethodChannel(vrGPSdkChannelName);
  }

  @override
  Future<bool> printData({
    required String host,
    required int port,
    required String data,
  }) async {
    try {
      final response = await _methodChannel.invokeMethod(methodPrintData, {
        "host": host,
        "port": port,
        "data": data,
      });

      return (response is bool) ? response : false;
    } catch (_) {
      return false;
    }
  }

  @override
  Future<bool> checkConnection({
    required String host,
    required int port,
  }) async {
    try {
      final response = await _methodChannel.invokeMethod(
        methodCheckConnectionPrinter,
        {
          "host": host,
          "port": port,
        },
      );

      return (response is bool) ? response : false;
    } catch (_) {
      return false;
    }
  }
}
