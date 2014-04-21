package de.kahoona.xtv.server

import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.domain.IrcServer
import de.kahoona.xtv.irc.IrcBot
import de.kahoona.xtv.domain.Packet
import de.kahoona.xtv.services.IrcConnector

import java.nio.file.Files

/**
 * Created by Benni on 19.04.2014.
 */
class IrcHandler extends JSONHandler {

  IrcConnector connector

  public IrcHandler(IrcConnector ircConnector) {
    this.connector = ircConnector
  }


  @Override
  void handle(HttpExchange exchange) throws IOException {
    try {
      URI uri = exchange.getRequestURI()
      switch (uri.path) {
        case "/irc/toggleConnection":
          toggleConnection(exchange)
          break
        case "/irc/getServerStatus":
          getServerStatus(exchange)
          break
        case "/irc/getServerConsole":
          getServerConsole(exchange)
          break
      }
    } catch (Exception e) {
      e.printStackTrace()
    }
  }

  private void toggleConnection(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    IrcServer server = IrcServer.fromData(json.data)
    server = connector.toggleConnection(server)
    jsonResponse([status: 'ok', result: server], exchange)
  }

  private void getServerStatus(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    IrcServer server = IrcServer.fromData(json.data)
    String status = connector.getServerStatus(server)
    jsonResponse([status: status], exchange)
  }

  private void getServerConsole(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    IrcServer server = IrcServer.fromData(json.data)
    List<String> consoleLog = connector.getServerConsole(server)
    String log = consoleLog.join('\n')
    jsonResponse([console: log], exchange)
  }
}
