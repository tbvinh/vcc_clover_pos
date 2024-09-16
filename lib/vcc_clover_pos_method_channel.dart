import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'vcc_clover_pos_platform_interface.dart';

/// An implementation of [VccCloverPosPlatform] that uses method channels.
class MethodChannelVccCloverPos extends VccCloverPosPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('vcc_clover_pos');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> connectClover(String ip, int? port) async {
    final value = await methodChannel.invokeMethod('connectClover', {
      'ip': ip,
      'port': port,
    });

    return value;
  }
}
