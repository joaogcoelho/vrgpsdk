import 'package:flutter/services.dart';
import 'package:vrgpsdk/gprinter_repository.dart';

class GPrinterRepositoryImpl implements GPrinterRepository {
  final String methodChannelName = "VR_GPSDK";

  final String methodConnectToPrinter = "CONNECT_TO_PRINTER";
  final String methodPrintData = "PRINT_DATA";

  late MethodChannel _methodChannel;

  GPrinterRepositoryImpl() {
    _methodChannel = MethodChannel(methodChannelName);
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
