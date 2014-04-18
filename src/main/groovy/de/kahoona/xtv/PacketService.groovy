package de.kahoona.xtv

import de.kahoona.xtv.irc.Packet
import org.apache.commons.lang3.StringUtils
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

/**
 * Created by Benni on 16.03.14.
 */
class PacketService extends Verticle {

  def start () {


    //find all packets
    vertx.eventBus.registerHandler ("xtv.findAllPackets") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'find', collection: 'packets', matcher: [:]]) { Message result ->
        message.reply (result.body ())
      }
    }

    //count all packets
    vertx.eventBus.registerHandler ("xtv.countPackets") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'count', collection: 'packets', matcher: [:]]) { Message result ->
        message.reply (result.body ())
      }
    }

    //find packets
    vertx.eventBus.registerHandler ("xtv.findPackets") { Message message ->
      String query = createRegexQuery (message.body ().query)
      vertx.eventBus.send ('xtv.mongo', [action: 'find', collection: 'packets', limit: 50, matcher: [name: ['$regex': query, '$options': 'i' ]]]) { Message result ->
        message.reply (result.body ())
      }
    }

    // save packet
    vertx.eventBus.registerHandler ("xtv.savePacket") { Message message ->
      def packet = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'packets', document: packet]) { Message result ->
        message.reply (result.body ())
      }
    }
    // delete old packets
    vertx.eventBus.registerHandler ("xtv.cleanPackets") { Message message ->
      Date yesterday = new Date () - 1
      vertx.eventBus.send ('xtv.mongo', [action: 'delete', collection: 'packets', matcher: [date: ['$lt': yesterday.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")]]]) { Message result ->
        println "cleaned ${result.body ().number} packets."
      }
    }
  }

  private String createRegexQuery (String query) {
     String[] parts = StringUtils.split (query, ' ')
     return  parts.join ('.*')
  }
}
