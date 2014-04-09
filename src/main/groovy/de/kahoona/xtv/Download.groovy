package de.kahoona.xtv

import org.apache.commons.lang3.StringUtils


class Download {

  String _id
  String status
  String file
  String packetId
  String bot
  Long   bytesReceived
  Long   size
  double speed
  Long   remaining

  static def fromPacket (def packet) {
    return new Download (_id: packet.name, status: 'WAITING', file: packet.name, packetId: packet.packetId, bot: packet.bot)
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
      '_id'           : _id           ,
      'status'        : status        ,
      'file'          : file          ,
      'packetId'      : packetId      ,
      'bot'           : bot           ,
      'bytesReceived' : bytesReceived ,
      'size'          : size          ,
      'speed'         : speed         ,
      'remaining'     : remaining     ,
    ]
  }

  static Download fromJsonMap(Map data) {
    return new Download ([
        '_id'           : data._id           ,
        'status'        : data.status        ,
        'file'          : data.file          ,
        'packetId'      : data.packetId      ,
        'bot'           : data.bot           ,
        'bytesReceived' : data.bytesReceived ,
        'size'          : data.size          ,
        'speed'         : data.speed         ,
        'remaining'     : data.remaining     ,
    ])
  }
}
