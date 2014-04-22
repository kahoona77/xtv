package de.kahoona.xtv

import com.sun.net.httpserver.HttpServer
import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.server.DataHandler
import de.kahoona.xtv.server.DownloadHandler
import de.kahoona.xtv.server.IrcHandler
import de.kahoona.xtv.services.IrcConnector
import de.kahoona.xtv.server.PacketHandler
import de.kahoona.xtv.server.StaticFileHandler
import de.kahoona.xtv.services.DownloadsService
import de.kahoona.xtv.services.PacketsService

import java.util.concurrent.Executors

/**
 * Created by Benni on 19.04.2014.
 */
class XTV {

  static void main(String[] args) {

    MongoDB db = new MongoDB()
    XtvSettings settings = loadSettings(db)
    DownloadsService downloadsService = new DownloadsService(settings)
    PacketsService packetsService = new PacketsService()
    IrcConnector connector = new IrcConnector(settings, packetsService, downloadsService)
    downloadsService.connector = connector


    InetSocketAddress addr = new InetSocketAddress(settings.port)
    HttpServer httpServer = HttpServer.create(addr, 0)
    httpServer.with {
      createContext('/data',      new DataHandler(downloadsService))
      createContext('/irc',       new IrcHandler (connector))
      createContext('/packets',   new PacketHandler(packetsService))
      createContext('/downloads', new DownloadHandler(downloadsService))
      createContext('/',          new StaticFileHandler())
      setExecutor(Executors.newCachedThreadPool())
      start()
    }
  }

  private static XtvSettings loadSettings(MongoDB db) {
    def data = db.findFirst('settings')
    XtvSettings settings =  XtvSettings.fromData (data)

    String homeDir = System.getProperty("user.home")

    File settingsFile = new File ("$homeDir/.xtv", 'conf.properties')
    if (!settingsFile.exists()) {
      settingsFile.getParentFile ().mkdirs ()
      settingsFile.createNewFile()
      settingsFile.write('xtv.port=8090\n')
    }

    def props = new Properties()
    settingsFile.withInputStream {
      stream -> props.load(stream)
    }

    settings.port = new Integer(props['xtv.port'] as String)

    println "------- XTV-Settings --------"
    println "  Port:          ${settings.port}"
    println "  Nick:          ${settings.nick}"
    println "  Temp-Dir:      ${settings.tempDir}"
    println "  Download-Dir:  ${settings.downloadDir}"
    println "-----------------------------"

    return settings
  }
}
