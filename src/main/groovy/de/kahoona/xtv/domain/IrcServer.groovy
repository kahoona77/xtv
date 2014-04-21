package de.kahoona.xtv.domain

/**
 * Created by Benni on 21.04.2014.
 */
class IrcServer {

    String        _id
    String        name
    String        status
    List<Channel> channels

  Map toMap () {
    return [
        _id      : this._id,
        name     : this.name,
        status   : this.status,
        channels : this.channels.collect{[_id: it._id, name: it.name]}
    ]
  }

    static IrcServer fromData(def data) {
        IrcServer server = new IrcServer(_id: data._id, name: data.name, status: data.status, channels: [])
        data.channels?.each {
          server.channels << new Channel(_id: data._id, name: it.name)
        }
        return server
    }
}
