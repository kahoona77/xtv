package de.kahoona.xtv.services

import de.kahoona.xtv.data.StreamManager
import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.irc.IrcBot
import de.kahoona.xtv.irc.XTVReceiveFileTransfer
import org.apache.commons.lang3.StringUtils
import org.pircbotx.hooks.events.IncomingFileTransferEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Benni on 21.04.2014.
 */
class DownloadsService {

  private static Logger log = LoggerFactory.getLogger(DownloadsService.class)

  XtvSettings settings
  IrcConnector connector
  Map<String, XTVReceiveFileTransfer> downloads = [:]
  Map<String, Download> pendingDownloads = [:]

  private StreamManager streamManager

  public DownloadsService(XtvSettings settings) {
    this.streamManager = new StreamManager(500 * 1024 * 8) // 500 KBytes
    this.streamManager.enable()
    this.updateSettings(settings)
  }

  //find all downloads
  public List<Download> findAll() {
    List<Download> result = pendingDownloads.values().collect()
    result.addAll(downloads.values().collect { it.getDownload() })
    return result
  }

  def cancelDownload(Download download) {
    //first check running transfers
    XTVReceiveFileTransfer transfer = downloads[StringUtils.trim(download.id)]
    if (transfer) {
      if (transfer.isRunning ()) {
        this.stopDownload(download)
      }
      downloads.remove(StringUtils.trim(download.id))
    }

    //now check pending downloads
    pendingDownloads.remove(StringUtils.trim(download.id))
  }


  def startDownload(Download download) {
    IrcBot bot = connector.getBot(download.server)
    if (!bot || !bot.isConnected()) {
      return [status: 'err', message: "Server '${download.server}' not connected.".toString ()]
    } else {
      log.info ("Adding download to queue: ${download.file}")
      bot.startDownload(download)
      download.status = 'PENDING'
      pendingDownloads[StringUtils.trim(download.id)] = download
      return [status: 'ok']
    }
  }

  def stopDownload(Download download) {
    IrcBot bot = connector.getBot(download.server)
    bot.stopDownload (download)
  }

  def newTransfer(IncomingFileTransferEvent event) {
    Download download =  pendingDownloads.remove(event.getSafeFilename())
    if (!download) {
      log.error ("Error: No pending download for ${event.getSafeFilename()}")
      return //abort
    }

    download.size = event.filesize
    File file = new File(this.settings.tempDir, event.getSafeFilename())

    boolean resume = false
    long startPosition = 0
    if (!file.exists()) {
      // new Download
      file.getParentFile().mkdirs()
      file.createNewFile()
    } else {
      // resume download
      resume = true
      startPosition = file.length()
    }

    XTVReceiveFileTransfer transfer
    if (!resume) {
      transfer = event.accept(file) as XTVReceiveFileTransfer
    } else {
      transfer = event.acceptResume(file, startPosition) as XTVReceiveFileTransfer
    }

    transfer.settings      = settings
    transfer.streamManager = streamManager
    transfer.download      = download

    //add to downloads
    downloads[StringUtils.trim(download.id)] = transfer

    transfer.transfer()
  }

  void updateSettings(XtvSettings settings) {
    this.settings = settings

    if (settings.maxDownStream <= 0) {
      this.streamManager.disable()
    } else {
      this.streamManager.enable()
      this.streamManager.setDownstreamKbps(settings.maxDownStream * 8)
    }

  }
}
