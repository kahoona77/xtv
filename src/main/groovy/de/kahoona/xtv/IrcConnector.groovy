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
    bot.connect ()

    vertx.eventBus.registerHandler ("xtv.downloadPacket") { Message message ->
      Map data = message.body ().data
      Packet packet = new Packet(packetId: data.packetId, channel: data.channel, bot: data.bot, name: data.name)
      bot.download (packet)
      message.reply ([status: 'ok'])
    }

    println "IrcConnector started."
  }

}
