package de.kahoona.xtv.irc

import de.kahoona.xtv.Download
import org.pircbotx.Channel
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.UtilSSLSocketFactory
import org.pircbotx.cap.TLSCapHandler
import org.pircbotx.dcc.ReceiveChat
import org.pircbotx.dcc.ReceiveFileTransfer
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.ChannelInfoEvent
import org.pircbotx.hooks.events.IncomingChatRequestEvent
import org.pircbotx.hooks.events.IncomingFileTransferEvent
import org.pircbotx.hooks.events.JoinEvent
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.events.NoticeEvent
import org.pircbotx.hooks.events.UserListEvent
import org.pircbotx.hooks.types.GenericMessageEvent
import org.pircbotx.output.OutputChannel
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.Message

import javax.swing.text.html.ObjectView

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcBot extends ListenerAdapter implements Runnable{

  String name
  String server
  List<String> channels
  List<String> consoleLog = []
  int consoleIndex = 0

  private Vertx    vertx
  private PircBotX bot
  String downloadDir

  public IrcBot (Vertx vertx, name, server, List<String> channels, String downloadDir) {
    this.vertx = vertx
    this.name = name
    this.server = server
    this.channels = channels
    this.downloadDir = downloadDir
  }

  boolean isConnected () {
    bot?.isConnected ()
  }

  public connect () {
    Configuration.Builder builder = new Configuration.Builder ()
      .setName (this.name) //Set the nick of the bot. CHANGE IN YOUR CODE
      .setAutoNickChange (true) //Automatically change nick when the current one is in use
      .setCapEnabled (true) //Enable CAP features
      .addCapHandler (new TLSCapHandler (new UtilSSLSocketFactory ().trustAllCertificates (), true))
      .addListener (this) //This class is a listener, so add it to the bots known listeners
      .setServerHostname (this.server)
      .setDccResumeAcceptTimeout(30000)

    channels.each {
      builder.addAutoJoinChannel (it)
    }

    builder.setBotFactory (new BotFactory())

    Configuration configuration = builder.buildConfiguration ()
    this.bot = new PircBotX (configuration)

    Thread t = new Thread(this)
    t.start()
  }

  public disconnect () {
    bot.sendIRC ().quitServer ()
  }

  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    Packet packet = PacketParser.getPacket (event.channel.name, event.user.nick, server, event.message)
    if (packet) {
      vertx.eventBus.send ('xtv.savePacket', [data: packet.toMap()]){Message response ->
        if (response.body().status != 'ok') {
          logToConsole ("ERROR: save Packet: " + response.body().message)
        }
      }
    }

  }

  @Override
  public void onNotice(NoticeEvent event) throws Exception {
    logToConsole ("irc: $event.message")
  }

  @Override
  public void onIncomingFileTransfer(IncomingFileTransferEvent event) throws Exception {
    File file = new File (this.downloadDir, event.getSafeFilename ())

    boolean resume = false
    long startPosition = 0
    if (!file.exists()) {
      // new Download
      file.getParentFile ().mkdirs ()
      file.createNewFile()
    } else {
      // resume download
      resume = true
      startPosition = file.length()
    }

    vertx.eventBus.send ('xtv.findDownloadById', [id: event.getSafeFilename ()]) { Message result ->
      Download download = result.body().result
      download.size = event.filesize

      XTVReceiveFileTransfer transfer
      if (!resume) {
        logToConsole "receiving file: ${event.getSafeFilename ()}"
        transfer = event.accept (file) as XTVReceiveFileTransfer
      } else {
        logToConsole "resuming file: ${event.getSafeFilename ()}"
        transfer = event.acceptResume (file, startPosition) as XTVReceiveFileTransfer
      }

      transfer.vertx = vertx
      transfer.download = download

      transfer.transfer()
    }
  }


  void startDownload (Download download) {
    bot.sendIRC().message(download.bot, download.sendMessage)
  }

  void stopDownload (Download download) {
    logToConsole "stopping file: ${download.file}"
    bot.sendIRC().message(download.bot, download.cancelMessage)
  }

  private logToConsole (String msg) {
    if (consoleIndex > 500) {
      consoleIndex = 0
    }
    consoleLog[consoleIndex] = msg
    consoleIndex++
  }

  @Override
  void run() {
    bot.startBot ()
  }
}
