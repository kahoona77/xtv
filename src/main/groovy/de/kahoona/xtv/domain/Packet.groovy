package de.kahoona.xtv.domain

import java.text.ParseException

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class Packet {

  String packetId
  String size
  String name
  String bot
  String channel
  String server
  Date date

  Map toMap() {
    return [
        '_id'     : "${channel}:${bot}:${packetId}".toString(),
        'channel' : channel,
        'server'  : server,
        'bot'     : bot,
        'packetId': packetId,
        'name'    : name,
        'size'    : size,
        'date'    : date.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    ]
  }

  static Packet fromData(def data) {
    return new Packet(
        packetId: data.packetId,
        size: data.size,
        name: data.name,
        bot: data.bot,
        channel: data.channel,
        server: data.server,
        date: parseDate(data.date),
    )
  }

  private static Date parseDate(def date) {
    if (date instanceof String)
      Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", date as String)
    else if (date instanceof Long){
      return new Date(date as long)
    }
    return null
  }

}
