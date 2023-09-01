package br.com.vrsoft.vrgpsdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.util.Map;
import java.util.Vector;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** VrGPSdkPlugin */
public class VrgpsdkPlugin implements FlutterPlugin, MethodCallHandler {
  static final String METHOD_CHANNEL = "VR_GPSDK";

  private MethodChannel channel;


  private static final int CONN_PRINTER = 0x12;
  private ThreadPool threadPool;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), METHOD_CHANNEL);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "CONNECT_TO_PRINTER":
        System.out.println("TESTE PRINTERRRRR");
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
      System.out.println(call.arguments);
      Map<String, Object> arguments = (Map<String, Object>) call.arguments();

      String hostPrinter = (String) arguments.get("host");
      int portPrinter = (int) arguments.get("port");

      new DeviceConnFactoryManager
              .Build()
              .setId(1)
              .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
              .setIp(hostPrinter)
              .setPort(portPrinter)
              .build();

      threadPool = ThreadPool.getInstantiation();
      threadPool.addTask(() -> {
        boolean isConnected = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[1].getConnState();
        result.success(isConnected);
      });
    } catch (Exception exception) {
      System.out.println("Erro na conexao: " + exception.getMessage());
      result.error("ERROR_IN_CONNECTION", "Não foi possivel realizar conexão com impressora!", exception);
    }
  }

  private void handlePrintData(MethodCall call, Result result) {
    try {
      System.out.println(call.arguments);
      Map<String, Object> arguments = (Map<String, Object>) call.arguments();

      EscCommand esc = new EscCommand();
      esc.addInitializePrinter();
      esc.addPrintAndFeedLines((byte) 3);
      // 设置打印居中
      esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
      // 设置为倍高倍宽
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
      // 打印文字
      esc.addText("Sample\n");
      esc.addPrintAndLineFeed();

      /* 打印文字 */
      // 取消倍高倍宽
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
      // 设置打印左对齐
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
      // 打印文字
      esc.addText("Print text\n");
      // 打印文字
      esc.addText("Welcome to use SMARNET printer!\n");

      /* 打印繁体中文 需要打印机支持繁体字库 */
      String message = "票據打印機\n";
      esc.addText(message, "GB2312");
      esc.addPrintAndLineFeed();

      /* 绝对位置 具体详细信息请查看GP58编程手册 */
      esc.addText("Print");
      esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
      esc.addSetAbsolutePrintPosition((short) 6);
      esc.addText("Print");
      esc.addSetAbsolutePrintPosition((short) 10);
      esc.addText("Print");
      esc.addPrintAndLineFeed();

      /* 打印图片 */
      // 打印文字
      esc.addText("Print bitmap!\n");

      /* 打印一维条码 */
      // 打印文字
      esc.addText("Print code128\n");
      esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
      // 设置条码可识别字符位置在条码下方
      // 设置条码高度为60点
      esc.addSetBarcodeHeight((byte) 60);
      // 设置条码单元宽度为1
      esc.addSetBarcodeWidth((byte) 1);
      // 打印Code128码
      esc.addCODE128(esc.genCodeB("SMARNET"));
      esc.addPrintAndLineFeed();

      /*
       * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
       */
      // 打印文字
      esc.addText("Print QRcode\n");
      // 设置纠错等级
      esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
      // 设置qrcode模块大小
      esc.addSelectSizeOfModuleForQRCode((byte) 3);
      // 设置qrcode内容
      esc.addStoreQRCodeData("www.smarnet.cc");
      esc.addPrintQRCode();// 打印QRCode
      esc.addPrintAndLineFeed();

      // 设置打印左对齐
      esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
      //打印文字
      esc.addText("Completed!\r\n");

      // 开钱箱
      esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
      esc.addPrintAndFeedLines((byte) 8);
      // 加入查询打印机状态，用于连续打印
      byte[] bytes = {29, 114, 1};
      Vector<Byte> datas = esc.getCommand();

      threadPool.addTask(() -> {
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[1].sendDataImmediately(datas);
      });

      result.success("A conexão com a impressora foi realizada com sucesso!");
    } catch (Exception exception) {
      System.out.println("Erro na conexao: " + exception.getMessage());
      result.error("ERROR_IN_CONNECTION", "Não foi possivel realizar conexão com impressora!", exception);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    threadPool.stopThreadPool();
    channel.setMethodCallHandler(null);
  }
}
