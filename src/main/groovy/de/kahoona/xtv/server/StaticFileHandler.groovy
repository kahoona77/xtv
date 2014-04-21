package de.kahoona.xtv.server

import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

/**
 * Created by Benni on 19.04.2014.
 */
class StaticFileHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange t) throws IOException {
    URI uri = t.getRequestURI();
    String path = uri.getPath();
    if (!path.startsWith('/web')) {
      path = '/web/index.html'
    }
    URL file = loadFile(path)

    if (!file) {
      // Object does not exist or is not a file: reject with 404 error.
      String response = "404 (Not Found)\n";
      t.sendResponseHeaders(404, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } else {
      // Object exists and is a file: accept with response code 200.
      String mime = "text/html";
      if (path.substring(path.length() - 3).equals(".js")) mime = "application/javascript";
      if (path.substring(path.length() - 3).equals("css")) mime = "text/css";

      Headers h = t.getResponseHeaders();
      h.set("Content-Type", mime);
      t.sendResponseHeaders(200, 0);

      OutputStream os = t.getResponseBody();
      InputStream is = file.openStream();
      final byte[] buffer = new byte[0x10000];
      int count = 0;
      while ((count = is.read(buffer)) >= 0) {
        os.write(buffer, 0, count);
      }
      is.close();
      os.close();
    }
  }

  private URL loadFile(String path) {
    this.getClass().getResource(path)
  }
}
