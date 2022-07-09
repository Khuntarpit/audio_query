
library audio_query;

import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'audio_query_platform_interface.dart';

part 'src/album_info.dart';
part 'src/album_sort_type.dart';
part 'src/artist_info.dart';
part 'src/artist_sort_type.dart';
part 'src/data_model.dart';
part 'src/flutter_audio_query.dart';
part 'src/genre_info.dart';
part 'src/genre_sort_type.dart';
part 'src/playlist_info.dart';
part 'src/playlist_sort_type.dart';
part 'src/song_info.dart';
part 'src/song_sort_type.dart';


class AudioQuery {
  Future<String?> getPlatformVersion() {
    return AudioQueryPlatform.instance.getPlatformVersion();
  }
}
