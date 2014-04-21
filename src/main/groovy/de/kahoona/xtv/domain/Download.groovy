package de.kahoona.xtv.domain

import org.apache.commons.lang3.StringUtils


class Download {

  String id
  String status
  String file
  String packetId
  String server
  String bot
  long   bytesReceived
  long   size
  double speed
  long   remaining

  static def fromPacket (def packet) {
    return new Download (id: packet.name, status: 'WAITING', file: packet.name, packetId: packet.packetId, bot: packet.bot, server: packet.server)
  }



  String getSendMessage () {
    return "xdcc send ${getCleanPacketId()}"
  }

  @SuppressWarnings ("GrMethodMayBeStatic")
  String getCancelMessage () {
    return "xdcc cancel"
  }

  String getCleanPacketId () {
    return StringUtils.replace (packetId, '#','')
  }

  Map toMap () {
    return [
      'id'           :  id           ,
      'status'        : status        ,
      'file'          : file          ,
      'packetId'      : packetId      ,
      'bot'           : bot           ,
      'bytesReceived' : bytesReceived ,
      'size'          : size          ,
      'speed'         : speed         ,
      'remaining'     : remaining     ,
      'server'        : server        ,
    ]
  }

  static Download fromJsonMap(Map data) {
    return new Download ([
        'id'            : data.id           ,
        'status'        : data.status        ,
        'file'          : data.file          ,
        'packetId'      : data.packetId      ,
        'bot'           : data.bot           ,
        'bytesReceived' : data.bytesReceived ,
        'size'          : data.size          ,
        'speed'         : data.speed         ,
        'remaining'     : data.remaining     ,
        'server'        : data.server        ,
    ])
  }
}
