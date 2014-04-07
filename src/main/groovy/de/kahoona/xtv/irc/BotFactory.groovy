package de.kahoona.xtv.irc

import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.dcc.ReceiveFileTransfer

/**
 * Created by Benjamin.Ernst on 07.04.14.
 */
class BotFactory extends Configuration.BotFactory {

  @Override
  public ReceiveFileTransfer createReceiveFileTransfer (PircBotX bot, Socket socket, User user, File file, long startPosition) {
    return new ReceiveFileTransfer (bot.getConfiguration (), socket, user, file, startPosition) {
      protected void onAfterSend () {
        println "on after send"
      }
    };
  }
}