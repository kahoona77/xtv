package de.kahoona.xtv

import de.kahoona.xtv.irc.IrcBot
import de.kahoona.xtv.irc.Packet
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcConnector extends Verticle {

  Map<String, IrcBot> bots = [:]
  Map<String, String> settings = [:]

  def start () {
    initSettings ()

    vertx.eventBus.registerHandler ("xtv.startDownload") { Message message ->
      Download download = message.body ().data
      IrcBot bot = bots[download.server]
      if (!bot || !bot.isConnected ()) {
        message.reply ([status: 'err', message: "Server '${download.server}' not connected.".toString ()])
      }else {
        bot.startDownload (download)
        message.reply ([status: 'ok'])
      }
    }

    vertx.eventBus.registerHandler ("xtv.stopDownload") { Message message ->
      def data = message.body ().data
      Download download = Download.fromJsonMap (data)
      IrcBot bot = bots[download.server]
      bot.stopDownload (download)
      message.reply ([status: 'ok'])
    }

    vertx.eventBus.registerHandler ("xtv.downloadComplete") { Message message ->
      Download download = Download.fromJsonMap (message.body ().data)
      moveDownload (download, settings.tempDir, settings.downloadDir)
    }

    vertx.eventBus.registerHandler ("xtv.toggleConnection") { Message message ->
      def server = message.body ().data
      IrcBot bot = getAndUpdateServer(server, settings.nick)
      if (bot.isConnected ()) {
        bot.disconnect ()
        server.status = 'Not Connected'
      } else {
        bot.connect ()
        server.status = 'Connected'
      }
      vertx.eventBus.send ('xtv.saveServer', [data: server]) {Message response ->
        message.reply ([status: response.body ().status, result: server])
      }
    }

    println "IrcConnector started."
  }

  static void moveDownload (Download download, String fromDir, String toDir) {
    File file = new File (fromDir, download.file)
    if (file.exists ()) {
      try {
        Files.move (file.toPath (), new File (toDir, file.getName ()).toPath ())
      } catch (IOException e) {
        e.printStackTrace ()
      }
    }
  }

  private IrcBot getAndUpdateServer (def server, String nick) {
   String serverName = server.name
   List<String> channels = server.channels.collect { it.name }


   IrcBot bot = bots[server.name]
   if (!bot) {
     bot = new IrcBot (vertx, nick, serverName, channels, settings.tempDir)
     bots[bot.server] = bot
   } else {
     bot.channels = channels
     bot.name = nick
   }
   return bot
 }

  private initSettings() {
    vertx.eventBus.send ('xtv.loadSettings', null) { Message settingsResponse ->
      def settings = settingsResponse.body ().result
      this.settings = settings
    }
  }

}
