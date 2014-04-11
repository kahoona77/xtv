package de.kahoona.xtv.irc

/**
 * Created by benjamin.ernst on 18.03.14.
 */
class PacketParserTest extends GroovyTestCase {

  void testGetPacket () {
    PacketParser packetParser = new PacketParser ()

    Packet packet = packetParser.getPacket ('mg', 'test', 'server', '#89    0x [235M] Stand.Up.For.The.Week.S05E06.HDTV.x264-TLA.mp4')
    assertNotNull (packet)
    assertEquals ('#89', packet.packetId)
    assertEquals ('235M', packet.size)
    assertEquals ('Stand.Up.For.The.Week.S05E06.HDTV.x264-TLA.mp4', packet.name)
    assertEquals ('test', packet.bot)
    assertEquals ('mg', packet.channel)

    packet = packetParser.getPacket ('mg', 'test', 'server', '#167  0x [ 10G] Cliff.Richard-Still.Reelin.And.A.Rockin.Live.At.Sydney.Opera.House.2013.BluRay.DTS-HD.x264-LEGi0N.tar')
    assertNotNull (packet)
    assertEquals ('#167', packet.packetId)
    assertEquals ('10G', packet.size)
    assertEquals ('Cliff.Richard-Still.Reelin.And.A.Rockin.Live.At.Sydney.Opera.House.2013.BluRay.DTS-HD.x264-LEGi0N.tar', packet.name)
    assertEquals ('test', packet.bot)
    assertEquals ('mg', packet.channel)

    packet = packetParser.getPacket ('mg', 'test', 'server', '#88 \u0002   2x [2.0G] WWE.Hell.In.A.Cell.2012.BDRip.x264-FiCO.mkv')
    assertNotNull (packet)
    assertEquals ('#88', packet.packetId)
    assertEquals ('2.0G', packet.size)
    assertEquals ('WWE.Hell.In.A.Cell.2012.BDRip.x264-FiCO.mkv', packet.name)
    assertEquals ('test', packet.bot)
    assertEquals ('mg', packet.channel)
  }
}
