package br.com.vrsoft.vrgpsdk;

import androidx.annotation.NonNull;

import com.gprinter.command.EscCommand;

import java.util.Map;
import java.util.Vector;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** VrGPSdkPlugin */
public class VrGPSdkPlugin implements FlutterPlugin, MethodCallHandler {
  static final String METHOD_CHANNEL = "VR_GPSDK";

  private MethodChannel channel;

  private ThreadPool threadPool;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), METHOD_CHANNEL);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "CHECK_CONNECTION_PRINTER":
        handleConnectToPrinter(call, result);
        break;
      case "PRINT_DATA":
        handlePrintData(call, result);
        break;
      default:
        result.notImplemented();
    }
  }

  private void handleConnectToPrinter(MethodCall call, Result result) {
    try {
      Map<String, Object> arguments = (Map<String, Object>) call.arguments();

      String hostPrinter = (String) arguments.get("host");
      int portPrinter = (int) arguments.get("port");

      threadPool = ThreadPool.getInstantiation();
      threadPool.addTask(() -> {
        Printer printer = new Printer(hostPrinter, portPrinter);
        printer.openPort();

        boolean isConnected = printer.getConnState();
        result.success(isConnected);
      });
    } catch (Exception exception) {
      result.error("ERROR_IN_CONNECTION", "Não foi possivel realizar conexão com impressora!", exception);
    }
  }

  private void handlePrintData(MethodCall call, Result result) {
    try {
      Map<String, Object> arguments = (Map<String, Object>) call.arguments();

      String hostPrinter = (String) arguments.get("host");
      int portPrinter = (int) arguments.get("port");
      String dataToPrinter = (String) arguments.get("data");

      EscCommand esc = new EscCommand();
      esc.addInitializePrinter();

      esc.addPrintAndFeedLines((byte) 3);
      esc.addText(dataToPrinter);
      esc.addPrintAndLineFeed();

      Vector<Byte> datas = esc.getCommand();

      threadPool = ThreadPool.getInstantiation();
      threadPool.addTask(() -> {
        Printer printer = new Printer(hostPrinter, portPrinter);
        printer.openPort();

        boolean isConnected = printer.getConnState();

        System.out.println("TESTE PRINTER CONE - " + isConnected);

        if (isConnected) {
          printer.sendDataImmediately(datas);
        }
      });

      result.success(true);
    } catch (Exception exception) {
      result.error("ERROR_IN_CONNECTION", "Não foi possível realizar conexão com impressora!", exception);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    threadPool.stopThreadPool();
    channel.setMethodCallHandler(null);
  }
}
