import 'WaitingRoom.dart';
import 'dart:io';
import 'dart:convert';
import 'dart:math';

typedef void EventHandler(dynamic event);

class Game {
  final WaitingRoom waitingRoom;
  final WebSocket firstPlayer;
  final WebSocket secondPlayer;

  // true if first players' cell, false if seconds' one, null if empty
  Map<Point<int>, bool> field = {};

  bool firstPlayerTurn = true;

  Game(this.waitingRoom, this.firstPlayer, this.secondPlayer) {
    firstPlayer.add(createStartMessage(1));
    secondPlayer.add(createStartMessage(2));

    firstPlayer.listen(createEventHandler(true, firstPlayer, secondPlayer));
    secondPlayer.listen(createEventHandler(false, secondPlayer, firstPlayer));
  }

  EventHandler createEventHandler(
      bool forFirstPlayer, WebSocket thisPlayer, WebSocket otherPlayer) {
    return (messageJson) {
      print('event handler for ' +
          (forFirstPlayer ? 'first player' : 'second player')
          + ' invoked with message: ' + messageJson);
      if (forFirstPlayer == firstPlayerTurn) {
        final Map message = JSON.decode(messageJson);
        final int line = message['line'];
        final int column = message['column'];
        final cellPoint = new Point<int>(line, column);

        if (field[cellPoint] == null) {
          firstPlayerTurn = !firstPlayerTurn;
          field[cellPoint] = forFirstPlayer;

          thisPlayer.add(createSetCellMessage(line, column, true));
          otherPlayer.add(createSetCellMessage(line, column, false));

          checkIfGameIsOver(line, column, forFirstPlayer);
        } else {
          print('ignore since cell is not empty');
        }
      } else {
        print('ignore since it is attempt to make turn out of order'
            '\nfirstPlayerTurn: $firstPlayerTurn, forFirstPlayer: $forFirstPlayer');
      }
    };
  }

  void checkIfGameIsOver(int line, int column, bool player) {
    // vertical check
    int count = 1;
    bool firstDirOk = true;
    bool secondDirOk = true;
    for (int i = 1; i < 5; i++) {
      if (firstDirOk) {
        if (field[new Point<int>(line + i, column)] == player) {
          count++;
        } else {
          firstDirOk = false;
        }
      }
      if (secondDirOk) {
        if (field[new Point<int>(line - i, column)] == player) {
          count++;
        } else {
          secondDirOk = false;
        }
      }
    }
    if (count >= 5) {
      onGameOver(player);
    }

    // horizontal check
    count = 1;
    firstDirOk = true;
    secondDirOk = true;
    for (int i = 1; i < 5; i++) {
      if (firstDirOk) {
        if (field[new Point<int>(line, column + i)] == player) {
          count++;
        } else {
          firstDirOk = false;
        }
      }
      if (secondDirOk) {
        if (field[new Point<int>(line, column - i)] == player) {
          count++;
        } else {
          secondDirOk = false;
        }
      }
    }
    if (count >= 5) {
      onGameOver(player);
    }

    // main diagonal check
    count = 1;
    firstDirOk = true;
    secondDirOk = true;
    for (int i = 1; i < 5; i++) {
      if (firstDirOk) {
        if (field[new Point<int>(line + i, column + i)] == player) {
          count++;
        } else {
          firstDirOk = false;
        }
      }
      if (secondDirOk) {
        if (field[new Point<int>(line - i, column - i)] == player) {
          count++;
        } else {
          secondDirOk = false;
        }
      }
    }
    if (count >= 5) {
      onGameOver(player);
    }

    // not main diagonal check
    count = 1;
    firstDirOk = true;
    secondDirOk = true;
    for (int i = 1; i < 5; i++) {
      if (firstDirOk) {
        if (field[new Point<int>(line + i, column - i)] == player) {
          count++;
        } else {
          firstDirOk = false;
        }
      }
      if (secondDirOk) {
        if (field[new Point<int>(line - i, column + i)] == player) {
          count++;
        } else {
          secondDirOk = false;
        }
      }
    }
    if (count >= 5) {
      onGameOver(player);
    }
  }

  void onGameOver(bool isFirstPlayerWinner) {
    firstPlayer.add(isFirstPlayerWinner ?
        createWinMessage() :
        createLossMessage());
    secondPlayer.add(isFirstPlayerWinner ?
        createLossMessage() :
        createWinMessage());

    firstPlayer.close();
    secondPlayer.close();

    waitingRoom.activeGames.remove(this);
  }
}

String createStartMessage(int turnOrder) {
  return JSON.encode({
    'type': 'start',
    'turnOrder': turnOrder
  });
}

String createWinMessage() {
  return JSON.encode({
    'type': 'win'
  });
}

String createLossMessage() {
  return JSON.encode({
    'type': 'loss'
  });
}

String createSetCellMessage(int line, int column, bool isMy) {
  return JSON.encode({
    'type': 'set_cell',
    'line': line,
    'column': column,
    'imOwner': isMy
  });
}