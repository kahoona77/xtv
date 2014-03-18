package de.kahoona.xtv.irc

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class PacketParserTest extends GroovyTestCase {

  void testGetPacket () {
    PacketParser packetParser = new PacketParser ()

    Packet packet = packetParser.getPacket ('test', '#89    0x [235M] Stand.Up.For.The.Week.S05E06.HDTV.x264-TLA.mp4')
    assertNotNull (packet)
    assertEquals ('#89', packet.id)
    assertEquals ('235M', packet.size)
    assertEquals ('Stand.Up.For.The.Week.S05E06.HDTV.x264-TLA.mp4', packet.name)
    assertEquals ('test', packet.bot)


    packet = packetParser.getPacket ('test', '#167  0x [ 10G] Cliff.Richard-Still.Reelin.And.A.Rockin.Live.At.Sydney.Opera.House.2013.BluRay.DTS-HD.x264-LEGi0N.tar')
    assertNotNull (packet)
    assertEquals ('#167', packet.id)
    assertEquals ('10G', packet.size)
    assertEquals ('Cliff.Richard-Still.Reelin.And.A.Rockin.Live.At.Sydney.Opera.House.2013.BluRay.DTS-HD.x264-LEGi0N.tar', packet.name)
    assertEquals ('test', packet.bot)
  }
}
