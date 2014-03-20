package de.kahoona.xtv.irc

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class Packet {

  String packetId
  String size
  String name
  String bot
  String channel
  Date   date

  Map toMap () {
    return [
     '_id':     "${channel}:${bot}:${packetId}",
     'channel':  channel,
     'bot':      bot,
     'packetId': packetId,
     'name':     name,
     'size':     size,
     'date':     date,
    ]
  }
}
