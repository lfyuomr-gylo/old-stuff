import 'Game.dart';
import 'dart:io';

class WaitingRoom {
  final Set<Game> activeGames = new Set<Game>();
  WebSocket waitingUser = null;

  void onNewUser(WebSocket user) {
    if (waitingUser == null) {
      waitingUser = user;
    } else {
      activeGames.add(new Game(this, waitingUser, user));
      waitingUser = null;
    }
  }
}