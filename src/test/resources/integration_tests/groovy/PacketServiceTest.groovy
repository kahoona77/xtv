/**
 * Simple integration test which shows tests deploying other verticles, using the Vert.x API etc
 */
import de.kahoona.xtv.irc.Packet
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.Message

import static org.vertx.testtools.VertxAssert.*

// And import static the VertxTests script
import org.vertx.groovy.testtools.VertxTests

// The test methods must being with "test"


def testSavePacket() {

  Vertx v = vertx as Vertx

  Packet packet = new Packet(packetId: '#31', channel: 'mg', bot: 'bot', name: 'test.mkv', size: '123M')
  v.eventBus.send ('xtv.savePacket', [data: packet]) { Message message ->
    assertEquals ('ok', message.body ().status)

    testComplete()
  }
}


VertxTests.initialize(this)


container.deployModule(System.getProperty("vertx.modulename"), { asyncResult ->
  // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
  assertTrue(asyncResult.succeeded)
  assertNotNull("deploymentID should not be null", asyncResult.result())

  vertx.eventBus.registerHandler("xtv.started") {Message message ->
    // If deployed correctly then start the tests!
    VertxTests.startTests(this)
  }
})





