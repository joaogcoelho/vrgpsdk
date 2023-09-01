abstract class GPrinterRepository {
  Future<bool> checkConnection({
    required String host,
    required int port,
  });

  Future<dynamic> connectToPrinter({
    required String host,
    required int port,
  });

  Future<dynamic> printData();
}
