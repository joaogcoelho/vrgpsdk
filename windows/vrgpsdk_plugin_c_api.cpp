#include "include/vrgpsdk/vrgpsdk_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "vrgpsdk_plugin.h"

void VrgpsdkPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  vrgpsdk::VrgpsdkPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
