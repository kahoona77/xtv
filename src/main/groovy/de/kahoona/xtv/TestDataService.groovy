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
                    [name: 'irc.abjects.net',   port: 6667, status: 'Connected',     channels: [[name:'#mg-chat', botsCount: 0, packetsCount: 0], [name:'#moviegods', botsCount: 154, packetsCount: 1365]]],
                    [name: 'irc.criten.net',    port: 6667, status: 'Not Connected', channels: [[name:'#1warez', botsCount: 69, packetsCount: 145]]],
                    [name: 'irc.dejatoons.net', port: 6667, status: 'Connected',     channels: [[name:'#beast-xdcc', botsCount: 98, packetsCount: 759]]],
            ]
            message.reply([success: true, data: servers])
        }
    }
}
