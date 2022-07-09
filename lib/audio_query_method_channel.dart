import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'audio_query_platform_interface.dart';

/// An implementation of [AudioQueryPlatform] that uses method channels.
class MethodChannelAudioQuery extends AudioQueryPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('audio_query');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
