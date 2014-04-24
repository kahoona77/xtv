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
import org.apache.log4j.FileAppender
import org.apache.log4j.Layout
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors

/**
 * Created by Benni on 19.04.2014.
 */
class XTV {

  private static org.slf4j.Logger log = LoggerFactory.getLogger(XTV.class)

  static void main(String[] args) {

    MongoDB db = new MongoDB()
    XtvSettings settings = loadSettings(db)

    configureLogging (settings)

    DownloadsService downloadsService = new DownloadsService(settings)
    PacketsService packetsService = new PacketsService()
    IrcConnector connector = new IrcConnector(settings, packetsService, downloadsService)
    downloadsService.connector = connector


    InetSocketAddress addr = new InetSocketAddress(settings.port)
    HttpServer httpServer = HttpServer.create(addr, 0)
    httpServer.with {
      createContext('/data',      new DataHandler(settings, downloadsService))
      createContext('/irc',       new IrcHandler (connector))
      createContext('/packets',   new PacketHandler(packetsService))
      createContext('/downloads', new DownloadHandler(downloadsService))
      createContext('/',          new StaticFileHandler())
      setExecutor(Executors.newCachedThreadPool())
      start()
    }
  }

  static void configureLogging(XtvSettings settings) {
    FileAppender fileAppender = new FileAppender( new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n"), settings.logFile)
    Logger root = Logger.getRootLogger();
    root.setLevel(Level.DEBUG)
    root.addAppender(fileAppender);

    Logger pircBotxLogger = Logger.getLogger('org.pircbotx')
    pircBotxLogger.setLevel(Level.ERROR)

  }

  private static XtvSettings loadSettings(MongoDB db) {
    def data = db.findFirst('settings')
    XtvSettings settings =  XtvSettings.fromData (data)

    String homeDir = System.getProperty("user.home")

    File settingsFile = new File ("$homeDir/.xtv", 'conf.properties')
    if (!settingsFile.exists()) {
      settingsFile.getParentFile ().mkdirs ()
      settingsFile.createNewFile()
      settingsFile.write("xtv.port=8090\nxtv.logFile=$homeDir/.xtv/xtv.log\n")
    }

    def props = new Properties()
    settingsFile.withInputStream {
      stream -> props.load(stream)
    }

    settings.port    = new Integer(props['xtv.port'] as String)
    settings.logFile = props['xtv.logFile'] as String

    println "------- XTV-Settings --------"
    println "  Port:          ${settings.port}"
    println "  logFile:       ${settings.logFile}"
    println "  Nick:          ${settings.nick}"
    println "  Temp-Dir:      ${settings.tempDir}"
    println "  Download-Dir:  ${settings.downloadDir}"
    println "-----------------------------"

    return settings
  }
}
