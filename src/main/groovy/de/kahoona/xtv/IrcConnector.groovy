package de.kahoona.xtv

import de.kahoona.xtv.irc.IrcBot
import de.kahoona.xtv.irc.Packet
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcConnector extends Verticle {

  def start () {

    IrcBot bot = new IrcBot(vertx, 'kahhonaPirc', 'irc.abjects.net', ['#mg-chat', '#moviegods'])
//    bot.connect ()

    vertx.eventBus.registerHandler ("xtv.startDownload") { Message message ->
      Download download = message.body ().data
      bot.startDownload (download)
      message.reply ([status: 'ok'])
    }

    println "IrcConnector started."
  }

}
