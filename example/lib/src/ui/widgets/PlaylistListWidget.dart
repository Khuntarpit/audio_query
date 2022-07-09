import 'package:flutter/material.dart';
import 'package:audio_query/audio_query.dart';
import '../../bloc/ApplicationBloc.dart';
import '../../bloc/BlocProvider.dart';
import '../screens/PlaylistDetailScreen.dart';
import './ListItemWidget.dart';

class PlaylistListWidget extends StatelessWidget {
  final List<PlaylistInfo> dataList;
  final ApplicationBloc appBloc;
  PlaylistListWidget({required this.dataList, required this.appBloc});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
        itemCount: dataList.length,
        itemBuilder: (context, index) {
          PlaylistInfo playlistInfo = dataList[index];

          return ListItemWidget(
            title: Text(playlistInfo.name),
            subtitle: Text("Total songs: ${playlistInfo.memberIds.length}"),
            onTap: () {
              _showPlaylistDetailsScreen(playlistInfo, context);
            },
            leading: CircleAvatar(
              backgroundImage: AssetImage('assets/no_cover.png'),
            ),
            trailing: IconButton(
                icon: Icon(Icons.delete),
                onPressed: () {
                  FlutterAudioQuery.removePlaylist(playlist: playlistInfo);
                  appBloc.loadPlaylistData();
                }), imagePath: '',
          );
        });
  }

  void _showPlaylistDetailsScreen(
      final PlaylistInfo playlist, BuildContext context) {
    Navigator.push(context, MaterialPageRoute(builder: (context) {
      return BlocProvider(
        bloc: PlaylistDetailScreenBloc(playlist),
        child: PlaylistDetailScreen(),
      );
    }));
  }
}
