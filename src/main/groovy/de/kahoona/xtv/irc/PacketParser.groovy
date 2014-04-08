package de.kahoona.xtv.irc

import org.apache.commons.lang3.StringUtils

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class PacketParser {

  private static def myRegularExpression = /(#[0-9]+).*\[\s*([0-9|\.]+[BbGgiKMs]+)\]\s+(.+).*/

  public static Packet getPacket (String channel, String bot, String message) {

    message = StringUtils.trimToEmpty (message)

    def matcher = ( message =~ myRegularExpression )

    if (matcher.matches ()) {
      return new Packet(packetId: matcher[0][1], size: matcher[0][2], name: matcher[0][3], bot: bot, channel: channel, date: new Date())
    }
    return null
  }

}
