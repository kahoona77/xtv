package de.kahoona.xtv.server

import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.IrcServer
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.services.DownloadsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Benni on 19.04.2014.
 */
class DataHandler extends JSONHandler {

  private static Logger log = LoggerFactory.getLogger(DataHandler.class)

  MongoDB db
  DownloadsService downloadsService
  XtvSettings settings

  public DataHandler(XtvSettings settings, DownloadsService downloadsService) {
    this.settings = settings
    this.db = MongoDB.newInstance()
    this.downloadsService = downloadsService
  }

  @Override
  void handle(HttpExchange exchange) throws IOException {
    try {
      URI uri = exchange.getRequestURI()
      switch (uri.path) {
        case "/data/loadServers":
          loadServers(exchange)
          break
        case "/data/saveServer":
          saveServer(exchange)
          break
        case "/data/deleteServer":
          deleteServer(exchange)
          break
        case "/data/addChannel":
          addChannel(exchange)
          break
        case "/data/deleteChannel":
          deleteChannel(exchange)
          break
        case "/data/loadSettings":
          loadSettings(exchange)
          break
        case "/data/saveSettings":
          saveSettings(exchange)
          break
        case "/data/loadLogFile":
          loadLogFile(exchange)
          break
        case "/data/clearLogFile":
          clearLogFile(exchange)
          break
      }
    } catch (Exception e) {
      log.error('Error while handling request.',e)
    }
  }


  void deleteServer(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)

    def result = db.remove('servers', json.data)
    jsonResponse([success: true, status: 'ok'], exchange)
  }

  void loadServers(HttpExchange exchange) {
    def servers = db.findAll('servers')

    def data = [success: true, status: 'ok', results: servers]
    jsonResponse(data, exchange)
  }

  void addChannel(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    def server = db.findById('servers', json.data.serverId)
    server.channels << json.data.channel

    db.save('servers', server)
    def data = [success: true, status: 'ok']
    jsonResponse(data, exchange)
  }

  void deleteChannel(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    def server = db.findById('servers', json.data.serverId)
    def channelToRemove = server.channels.find { it._id == json.data.channelId }
    server.channels = server.channels - channelToRemove

    db.save('servers', server)
    def data = [success: true, status: 'ok']
    jsonResponse(data, exchange)
  }

  void loadSettings(HttpExchange exchange) {
    def settings = db.findFirst('settings')

    def data = [success: true, status: 'ok', result: settings]
    jsonResponse(data, exchange)
  }

  void saveSettings(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    XtvSettings settings = XtvSettings.fromData(json.data)

    db.save('settings', settings.toMap())

    downloadsService.updateSettings (settings)

    def data = [success: true, status: 'ok']
    jsonResponse(data, exchange)
  }

  private void loadLogFile(HttpExchange exchange) {
    File logFile = new File (settings.logFile)
    String log = logFile.text
    jsonResponse([logFile: log], exchange)
  }

  private void clearLogFile(HttpExchange exchange) {
    File logFile = new File (settings.logFile)
    logFile.write('')
    jsonResponse([status: 'ok'], exchange)
  }
}
