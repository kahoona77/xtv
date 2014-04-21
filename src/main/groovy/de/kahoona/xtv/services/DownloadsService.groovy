package de.kahoona.xtv.services

import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.irc.IrcBot
import de.kahoona.xtv.irc.XTVReceiveFileTransfer
import org.pircbotx.hooks.events.IncomingFileTransferEvent

/**
 * Created by Benni on 21.04.2014.
 */
class DownloadsService {

  XtvSettings settings
  IrcConnector connector
  Map<String, XTVReceiveFileTransfer> downloads = [:]
  Map<String, Download> pendingDownloads = [:]

  public DownloadsService(XtvSettings settings) {
    this.settings = settings
  }

  //find all downloads
  public List<Download> findAll() {
    List<Download> result = pendingDownloads.values().collect()
    result.addAll(downloads.values().collect { it.getDownload() })
    return result
  }

  def cancelDownload(Download download) {
    //first check running transfers
    XTVReceiveFileTransfer transfer = downloads[download.id]
    if (transfer) {
      if (transfer.isRunning ()) {
        this.stopDownload(download)
      }
      downloads.remove(download.id)
    }

    //now check pending downloads
    pendingDownloads.remove(download.id)
  }


  def startDownload(Download download) {
    IrcBot bot = connector.getBot(download.server)
    if (!bot || !bot.isConnected()) {
      return [status: 'err', message: "Server '${download.server}' not connected.".toString ()]
    } else {
      bot.startDownload(download)
      download.status = 'PENDING'
      pendingDownloads[download.id] = download
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
      println "Error: No pending download for ${event.getSafeFilename()}"
      return //abort
    }

    download.size = event.filesize
    File file = new File(this.settings.downloadDir, event.getSafeFilename())

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

    transfer.settings = settings
    transfer.download = download

    //add to downloads
    downloads[download.id] = transfer

    transfer.transfer()
  }
}
