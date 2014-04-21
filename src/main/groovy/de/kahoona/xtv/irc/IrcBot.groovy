package de.kahoona.xtv.irc

import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.domain.IrcServer
import de.kahoona.xtv.domain.Packet
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.services.DownloadsService
import de.kahoona.xtv.services.PacketsService
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.UtilSSLSocketFactory
import org.pircbotx.cap.TLSCapHandler
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.IncomingFileTransferEvent
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.events.NoticeEvent

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class IrcBot extends ListenerAdapter implements Runnable {

  XtvSettings      settings
  IrcServer        server
  PacketsService   packetsService
  DownloadsService downloadsService

  List<String> consoleLog = []
  int consoleIndex = 0

  private PircBotX bot

  public IrcBot(XtvSettings settings, IrcServer server, PacketsService packetsService, DownloadsService downloadsService) {
    this.settings         = settings
    this.server           = server
    this.packetsService   = packetsService
    this.downloadsService = downloadsService
  }

  boolean isConnected() {
    bot?.isConnected()
  }

  public connect() {
    Configuration.Builder builder = new Configuration.Builder()
        .setName(this.settings.nick) //Set the nick of the bot. CHANGE IN YOUR CODE
        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        .setCapEnabled(true) //Enable CAP features
        .addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
        .addListener(this) //This class is a listener, so add it to the bots known listeners
        .setServerHostname(this.server.name)
        .setDccResumeAcceptTimeout(30000)

    this.server.channels.each {
      builder.addAutoJoinChannel(it.name)
    }

    builder.setBotFactory(new BotFactory())

    Configuration configuration = builder.buildConfiguration()
    this.bot = new PircBotX(configuration)

    Thread t = new Thread(this)
    t.start()
  }

  public disconnect() {
    bot.sendIRC().quitServer()
  }

  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    Packet packet = PacketParser.getPacket(event.channel.name, event.user.nick, server.name, event.message)
    if (packet) {
      packetsService.savePacket(packet)
    }

  }

  @Override
  public void onNotice(NoticeEvent event) throws Exception {
    logToConsole("irc: $event.message")
  }

  @Override
  public void onIncomingFileTransfer(IncomingFileTransferEvent event) throws Exception {
    logToConsole "incoming file-transfer: ${event.getSafeFilename()}"
    downloadsService.newTransfer (event)
  }


  void startDownload(Download download) {
    bot.sendIRC().message(download.bot, download.sendMessage)
  }

  void stopDownload(Download download) {
    logToConsole "stopping file: ${download.file}"
    bot.sendIRC().message(download.bot, download.cancelMessage)
  }

  private logToConsole(String msg) {
    if (consoleIndex > 500) {
      consoleIndex = 0
    }
    consoleLog[consoleIndex] = msg
    consoleIndex++
  }

  @Override
  void run() {
    bot.startBot()
  }
}
