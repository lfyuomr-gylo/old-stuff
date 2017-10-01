import 'dart:io';

import 'WaitingRoom.dart';

void main(List<String> args) {
  WaitingRoom waitingRoom = new WaitingRoom();
  HttpServer.bind('localhost', 8814).then((HttpServer server) /* => */ {
    server.listen( (HttpRequest request) {
      if ('/connect' == request.uri.path) {
        WebSocketTransformer.upgrade(request).then(
                (WebSocket socket) => waitingRoom.onNewUser(socket)
        );
      } else {
        final String requestPath = request.uri.path;
        final String path = '../public/' +
            (requestPath == '/' ? 'index.html' : requestPath);

        try {
          ContentType contentType = null;
          if (path.endsWith('.html')) {
            contentType = ContentType.HTML;
          } else if (path.endsWith('.js')) {
            contentType = ContentType.parse('text/javascript');
          }
          request.response.headers.contentType = contentType;

          final File fileToReturn =
              new File.fromUri(Platform.script.resolve(path));

          request.response.headers.contentLength = fileToReturn.lengthSync();
          fileToReturn.openRead().pipe(request.response);
        } catch(ignored) {}
      }
    });
  });
}