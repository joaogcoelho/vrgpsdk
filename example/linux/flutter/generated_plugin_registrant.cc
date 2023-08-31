//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <vrgpsdk/vrgpsdk_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) vrgpsdk_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "VrgpsdkPlugin");
  vrgpsdk_plugin_register_with_registrar(vrgpsdk_registrar);
}
