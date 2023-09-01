import 'package:flutter/services.dart';
import 'package:vrgpsdk/domain/repositories/gprinter_repository.dart';

class GPrinterRepositoryImpl implements GPrinterRepository {
  static const String vrGPSdkChannelName = "VR_GPSDK";

  final String methodConnectToPrinter = "CONNECT_TO_PRINTER";
  final String methodPrintData = "PRINT_DATA";

  late MethodChannel _methodChannel;

  GPrinterRepositoryImpl() {
    _methodChannel = const MethodChannel(vrGPSdkChannelName);
  }

  @override
  Future<dynamic> connectToPrinter({
    required String host,
    required int port,
  }) async {
    _methodChannel.invokeMethod(methodConnectToPrinter, {
      "host": host,
      "port": port,
    });
  }

  @override
  Future<dynamic> printData() async {
    _methodChannel.invokeMethod(methodPrintData);
  }
}
