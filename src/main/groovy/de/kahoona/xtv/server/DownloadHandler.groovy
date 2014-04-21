package de.kahoona.xtv.server

import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.domain.Download
import de.kahoona.xtv.services.DownloadsService

/**
 * Created by Benni on 19.04.2014.
 */
class DownloadHandler extends JSONHandler {

  DownloadsService downloadsService

  public DownloadHandler(DownloadsService service) {
    this.downloadsService = service
  }

  @Override
  void handle(HttpExchange exchange) throws IOException {
    try {
      URI uri = exchange.getRequestURI()
      switch (uri.path) {
        case "/downloads/listDownloads":
          listDownloads(exchange)
          break
        case "/downloads/cancelDownload":
          cancelDownload(exchange)
          break
        case "/downloads/downloadPacket":
          downloadPacket(exchange)
          break
        case "/downloads/resumeDownload":
          resumeDownload(exchange)
          break
        case "/downloads/stopDownload":
          stopDownload(exchange)
          break
      }
    } catch (Exception e) {
      e.printStackTrace()
    }
  }

  //find all downloads
  private void listDownloads(HttpExchange exchange) {
    jsonResponse([status: 'ok', results: downloadsService.findAll()], exchange)
  }

  // cancel download
  private void cancelDownload(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    Download download = Download.fromJsonMap(json.data as Map)
    downloadsService.cancelDownload(download)
    jsonResponse([status: 'ok'], exchange)
  }

  // start download
  private void downloadPacket(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    def packet = json.data

    Download download = Download.fromPacket(packet)
    def result = downloadsService.startDownload(download)

    jsonResponse(result, exchange)
  }

  // resume download
  private void resumeDownload(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    Download download = Download.fromJsonMap(json.data as Map)

    def result = downloadsService.startDownload(download)

    jsonResponse(result, exchange)
  }

  // stop download
  private void stopDownload(HttpExchange exchange) {
    Map json = parseJsonPost(exchange)
    Download download = Download.fromJsonMap(json.data as Map)

    downloadsService.stopDownload(download)

    jsonResponse([status: 'ok'], exchange)
  }
}
