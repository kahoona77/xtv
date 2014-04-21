package de.kahoona.xtv.domain

/**
 * Created by Benni on 21.04.2014.
 */
class XtvSettings {

    String _id
    String nick
    String tempDir
    String downloadDir
    int    port

    static XtvSettings fromData (def data) {
      return new XtvSettings(_id: data._id, nick: data.nick, tempDir: data.tempDir, downloadDir: data.downloadDir)
    }
}
