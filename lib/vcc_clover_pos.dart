
import 'vcc_clover_pos_platform_interface.dart';

class VccCloverPos {
  Future<String?> getPlatformVersion() {
    return VccCloverPosPlatform.instance.getPlatformVersion();
  }

  Future<String?> connectClover(String ip, int? port) {
    return VccCloverPosPlatform.instance.connectClover(ip, port);
  }
}
