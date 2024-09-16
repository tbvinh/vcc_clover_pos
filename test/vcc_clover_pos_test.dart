import 'package:flutter_test/flutter_test.dart';
import 'package:vcc_clover_pos/vcc_clover_pos.dart';
import 'package:vcc_clover_pos/vcc_clover_pos_platform_interface.dart';
import 'package:vcc_clover_pos/vcc_clover_pos_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockVccCloverPosPlatform
    with MockPlatformInterfaceMixin
    implements VccCloverPosPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final VccCloverPosPlatform initialPlatform = VccCloverPosPlatform.instance;

  test('$MethodChannelVccCloverPos is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelVccCloverPos>());
  });

  test('getPlatformVersion', () async {
    VccCloverPos vccCloverPosPlugin = VccCloverPos();
    MockVccCloverPosPlatform fakePlatform = MockVccCloverPosPlatform();
    VccCloverPosPlatform.instance = fakePlatform;

    expect(await vccCloverPosPlugin.getPlatformVersion(), '42');
  });
}
