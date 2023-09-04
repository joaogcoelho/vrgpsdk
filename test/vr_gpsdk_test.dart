import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:vrgpsdk/vr_gpsdk.dart';

void main() {
  late PrinterDevice printerDevice;

  MethodChannel channel = const MethodChannel('VR_GPSDK');

  handlerMethodCall(MethodCall methodCall) {
    switch (methodCall.method) {
      case "CHECK_CONNECTION_PRINTER":
        return Future.value(true);
      case "PRINT_DATA":
        return Future.value(true);
      default:
        return Future.value(null);
    }
  }

  handlerMethodCallError(MethodCall methodCall) {
    switch (methodCall.method) {
      case "CHECK_CONNECTION_PRINTER":
        return Future.value(Exception("Ocorreu um erro!"));
      case "PRINT_DATA":
        return Future.value(Exception("Ocorreu um erro!"));
      default:
        return Future.value(null);
    }
  }

  setUpHandlerMethodCall(Future<Object?>? Function(MethodCall)? handler) {
    final binaryInstance = TestDefaultBinaryMessengerBinding.instance!;
    binaryInstance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      handler,
    );
  }

  setUp(() {
    TestWidgetsFlutterBinding.ensureInitialized();
    setUpHandlerMethodCall(handlerMethodCall);

    printerDevice = PrinterDeviceImpl();
  });

  group('VrGPSdk', () {
    test('''
      Dado chamada a método checkConnection,
      Quando methodChannel ligado a 'VR_GPSDK' retornar valor
      Então ser possível receber um boolean indicando sucesso ou não
    ''', () async {
      bool response = await printerDevice.checkConnection(
        host: '192.168.1.199',
        port: 9100,
      );

      expect(response, isTrue);
    });

    test('''
      Dado chamada a método checkConnection,
      Quando methodChannel ligado a 'VR_GPSDK' ocorrer algum erro
      Então ser possível receber um boolean false
    ''', () async {
      setUpHandlerMethodCall(handlerMethodCallError);

      bool response = await printerDevice.checkConnection(
        host: '192.168.1.199',
        port: 9100,
      );

      expect(response, isFalse);
    });

    test('''
      Dado chamada a método printData,
      Quando methodChannel ligado a 'VR_GPSDK' retornar valor
      Então ser possível receber um boolean indicando sucesso ou não
    ''', () async {
      bool response = await printerDevice.printData(
        host: '192.168.1.199',
        port: 9100,
        data: "TESTE",
      );

      expect(response, isTrue);
    });

    test('''
      Dado chamada a método printData,
      Quando methodChannel ligado a 'VR_GPSDK' ocorrer algum erro
      Então ser possível receber um boolean false
    ''', () async {
      setUpHandlerMethodCall(handlerMethodCallError);

      bool response = await printerDevice.printData(
        host: '192.168.1.199',
        port: 9100,
        data: "TESTE",
      );

      expect(response, isFalse);
    });
  });
}
