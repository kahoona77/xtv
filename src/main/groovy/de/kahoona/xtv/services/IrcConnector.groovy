package de.kahoona.xtv.services

import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.IrcServer
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.irc.IrcBot

/**
 * Created by Benni on 19.04.2014.
 */
class IrcConnector {

  MongoDB db
  XtvSettings settings
  PacketsService packetsService
  DownloadsService downloadsService
  Map<String, IrcBot> bots = [:]

  public IrcConnector(XtvSettings settings, PacketsService packetsService, DownloadsService downloadsService) {
    this.db = MongoDB.newInstance()
    this.settings = settings
    this.packetsService = packetsService
    this.downloadsService = downloadsService
  }

  public IrcServer toggleConnection(IrcServer server) {
    IrcBot bot = getAndUpdateServer(server)
    if (bot.isConnected()) {
      bot.disconnect()
      server.status = 'Not Connected'
    } else {
      bot.connect()
      server.status = 'Connected'
    }
    db.save('servers', server.toMap())
    return server
  }

  public String getServerStatus(IrcServer server) {
    IrcBot bot = getAndUpdateServer(server)
    if (bot.isConnected()) {
      server.status = 'Connected'
    } else {
      server.status = 'Not Connected'
    }
    db.save('servers', server.toMap ())
    return server.status
  }

  public List<String> getServerConsole(IrcServer server) {
    IrcBot bot = getAndUpdateServer(server)
    return bot.consoleLog
  }

  public IrcBot getBot(String serverName) {
    return bots[serverName]
  }


  private IrcBot getAndUpdateServer(IrcServer server) {
    IrcBot bot = bots[server.name]
    if (!bot) {
      bot = new IrcBot(settings, server, packetsService, downloadsService)
      bots[bot.server.name] = bot
    } else {
      bot.server = server
      bot.settings = settings
    }
    return bot
  }
}
