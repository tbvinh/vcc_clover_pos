import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'vcc_clover_pos_method_channel.dart';

abstract class VccCloverPosPlatform extends PlatformInterface {
  /// Constructs a VccCloverPosPlatform.
  VccCloverPosPlatform() : super(token: _token);

  static final Object _token = Object();

  static VccCloverPosPlatform _instance = MethodChannelVccCloverPos();

  /// The default instance of [VccCloverPosPlatform] to use.
  ///
  /// Defaults to [MethodChannelVccCloverPos].
  static VccCloverPosPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [VccCloverPosPlatform] when
  /// they register themselves.
  static set instance(VccCloverPosPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> connectClover(String ip, int? port) {
    throw UnimplementedError('connectClover() has not been implemented.');
  }
}
