package de.kahoona.xtv

import de.kahoona.xtv.irc.IrcBot
import org.vertx.groovy.platform.Verticle

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcConnector extends Verticle {

  def start () {
    IrcBot bot = new IrcBot(vertx, 'kahhonaPirc', 'irc.abjects.net', ['#mg-chat', '#moviegods'])
    bot.connect ()

    println "IrcConnector started."
  }

}
