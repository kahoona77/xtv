package de.kahoona.xtv.services

import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.Packet

/**
 * Created by Benni on 21.04.2014.
 */
class PacketsService {

  MongoDB db

  public PacketsService () {
    this.db = MongoDB.newInstance()
  }

  public long getPacketCount () {
    return  db.countAll ('packets')
  }

  public List<Packet> findPackets (Map query) {
    def result = db.findWithQuery ('packets', query, 50)
    return result.collect{Packet.fromData (it)}
  }


  public def savePacket(Packet packet) {
    return this.db.save('packets', packet.toMap())
  }

  public void cleanPackets () {
    Date yesterday = new Date () - 1
    def result = this.db.db.packets.remove ([date: ['$lt': yesterday.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")]])
    println "cleaned ${result.n} packets."
  }
}
