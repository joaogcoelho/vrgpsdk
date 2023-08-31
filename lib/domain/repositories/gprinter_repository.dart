abstract class GPrinterRepository {
  Future<dynamic> connectToPrinter({
    required String host,
    required int port,
  });

  Future<dynamic> printData();
}
