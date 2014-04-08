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
    vertx.eventBus.registerHandler ("xtv.downloads") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'find', collection: 'downloads', matcher: [:]]) { Message result ->
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

    // update download
    vertx.eventBus.registerHandler ("xtv.updateDownload") { Message message ->
      def id = message.body ().id
      def bytesTransfered = message.body ().bytesTransfered
      vertx.eventBus.send ('xtv.mongo', [action: 'findone', collection: 'downloads', matcher: ['_id': id]]) { Message result ->
        def download = result.body().result
        download.loaded = bytesTransfered
        vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'downloads', document: download])
      }
    }
  }
}
