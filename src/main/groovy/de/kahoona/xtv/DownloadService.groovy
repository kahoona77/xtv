package de.kahoona.xtv

import de.kahoona.xtv.irc.Packet
import org.apache.commons.lang3.StringUtils
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

/**
 * Created by Benni on 16.03.14.
 */
class DownloadService extends Verticle {

  def start () {


    //find all downloads
    vertx.eventBus.registerHandler ("xtv.listDownloads") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'find', collection: 'downloads', matcher: [:]]) { Message result ->
        message.reply (result.body ())
      }
    }

    //find by id
    vertx.eventBus.registerHandler ("xtv.findDownloadById") { Message message ->
      def id = message.body ().id
      vertx.eventBus.send ('xtv.mongo', [action: 'findone', collection: 'downloads', matcher: ['_id': id]]) { Message result ->
        message.reply (result.body ())
      }
    }

    // save download
    vertx.eventBus.registerHandler ("xtv.saveDownload") { Message message ->
      def download = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download]) { Message result ->
        message.reply (result.body ())
      }
    }

    // delete download
    vertx.eventBus.registerHandler ("xtv.deleteDownload") { Message message ->
      def download = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'delete', collection: 'downloads', matcher: ['_id': download._id]]) { Message result ->
        message.reply (result.body ())
      }
    }

    // start download
    vertx.eventBus.registerHandler ("xtv.downloadPacket") { Message message ->
      def packet = message.body ().data
      Download download

      //find or create new download
      vertx.eventBus.send ('xtv.findDownloadById', [id: packet.name]) { Message result ->
        if (result.body ().status == 'ok' && result.body().result) {
          download = result.body().result
        }  else {
          download = Download.fromPacket (packet)
        }

        // save download
        vertx.eventBus.send ('xtv.saveDownload', [data: download.toMap()]) { Message saveResult ->
          // start download
          vertx.eventBus.send ('xtv.startDownload', [data: download.toMap()])  {Message startResult ->
            message.reply (startResult.body ())
          }
        }
      }
    }

    // resume download
    vertx.eventBus.registerHandler ("xtv.resumeDownload") { Message message ->
      def data = message.body ().data
      Download download = Download.fromJsonMap(data)
      vertx.eventBus.send ('xtv.startDownload', [data: download.toMap()])
      message.reply ([status: 'ok'])
    }

  }
}
