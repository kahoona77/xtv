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
                    [name: 'irc.abjects.net', port: 6667, channels: [[name:'#mg-chat'], [name:'#moviegods']]],
                    [name: 'irc.criten.net', port: 6667, channels: [[name:'#1warez']]],
                    [name: 'irc.dejatoons.net', port: 6667, channels: [[name:'#beast-xdcc']]],
            ]
            message.reply([success: true, data: servers])
        }
    }
}
