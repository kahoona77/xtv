package de.kahoona.xtv

import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

/**
 * Created by Benni on 16.03.14.
 */
class DataService extends Verticle {

  def start () {

    //load servers
    vertx.eventBus.registerHandler ("xtv.loadServers") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'find', collection: 'servers', matcher: [:]]) { Message result ->
        message.reply (result.body ())
      }
    }

    //load server
    vertx.eventBus.registerHandler ("xtv.loadServer") { Message message ->
      def server = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'findone', collection: 'servers', matcher: ['_id': server._id]]) { Message result ->
        message.reply (result.body ())
      }
    }

    // save server
    vertx.eventBus.registerHandler ("xtv.saveServer") { Message message ->
      def server = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'servers', document: server]) { Message result ->
        message.reply (result.body ())
      }
    }

    // add channel
    vertx.eventBus.registerHandler ("xtv.addChannel") { Message message ->
      def data = message.body ().data
      vertx.eventBus.send ('xtv.loadServer', [data: ['_id': data.serverId]]) { Message result ->
        def server = result.body ().result
        server.channels << data.channel
        vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'servers', document: server]) { Message saveResult ->
          message.reply (saveResult.body ())
        }
      }
    }

    // delete channel
    vertx.eventBus.registerHandler ("xtv.deleteChannel") { Message message ->
      def data = message.body ().data
      vertx.eventBus.send ('xtv.loadServer', [data: ['_id': data.serverId]]) { Message result ->
        def server = result.body ().result
        def channelToRemove = server.channels.find {it._id == data.channelId}
        server.channels = server.channels - channelToRemove
        vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'servers', document: server]) { Message saveResult ->
          message.reply (saveResult.body ())
        }
      }
    }

    //delete server
    vertx.eventBus.registerHandler ("xtv.deleteServer") { Message message ->
      def server = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'delete', collection: 'servers', matcher: ['_id': server._id]]) { Message result ->
        message.reply (result.body ())
      }
    }

    //load settings
    vertx.eventBus.registerHandler ("xtv.loadSettings") { Message message ->
      vertx.eventBus.send ('xtv.mongo', [action: 'findone', collection: 'settings', matcher: [:]]) { Message result ->
        message.reply (result.body ())
      }
    }

    // save settings
    vertx.eventBus.registerHandler ("xtv.saveSettings") { Message message ->
      def settings = message.body ().data
      vertx.eventBus.send ('xtv.mongo', [action: 'save', collection: 'settings', document: settings]) { Message result ->
        vertx.eventBus.send ('xtv.reloadSettings', null)
        message.reply (result.body ())
      }
    }

  }
}
