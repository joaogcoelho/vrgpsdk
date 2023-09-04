abstract class PrinterDevice {
  Future<bool> checkConnection({
    required String host,
    required int port,
  });

  Future<bool> printData({
    required String host,
    required int port,
    required String data,
  });
}
