package de.kahoona.xtv.irc

import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.dcc.ReceiveFileTransfer
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.Message

/**
 * Created by Benni on 08.04.14.
 */
class XTVReceiveFileTransfer extends ReceiveFileTransfer{

  Vertx  vertx
  String downloadId


  XTVReceiveFileTransfer(Configuration<PircBotX> configuration, Socket socket, User user, File file, long startPosition) {
    super(configuration, socket, user, file, startPosition)
  }

  @Override
  protected void onAfterSend () {
    vertx.eventBus.send ('xtv.updateDownload', [id: downloadId, bytesTransfered: this.bytesTransfered])
  }
}
