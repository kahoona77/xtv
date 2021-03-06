package de.kahoona.xtv.domain

/**
 * Created by Benni on 21.04.2014.
 */
class XtvSettings {

    String _id
    String nick
    String tempDir
    String downloadDir
    String postDownloadTrigger
    String logFile
    int    port
    long   maxDownStream


  Map toMap() {
    return [
        _id:                 this._id,
        nick:                this.nick,
        tempDir:             this.tempDir,
        downloadDir:         this.downloadDir,
        port:                this.port,
        maxDownStream:       this.maxDownStream,
        postDownloadTrigger: this.postDownloadTrigger,
        logFile:             this.logFile,
    ]
  }
    static XtvSettings fromData (def data) {
      return new XtvSettings(
          _id: data._id,
          nick: data.nick,
          tempDir: data.tempDir,
          downloadDir: data.downloadDir,
          maxDownStream: data.maxDownStream ? new Long (data.maxDownStream as String): 0,
          postDownloadTrigger: data.postDownloadTrigger,
          logFile: data.logFile,
      )
    }
}
