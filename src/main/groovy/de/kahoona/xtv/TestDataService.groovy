package de.kahoona.xtv

import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle

/**
 * Created by Benni on 16.03.14.
 */
class TestDataService extends Verticle {

    def start() {
        vertx.eventBus.registerHandler("testdata.servers") {Message message ->
            def servers = [
                    [name: 'irc.abjects.net', port: 6667, chanles: ['#mg-chat', '#moviegods']]
            ]
            message.reply([success: true, data: servers])
        }
    }
}
