package de.kahoona.xtv.server

import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.services.PacketsService
import org.apache.commons.lang3.StringUtils

/**
 * Created by Benni on 19.04.2014.
 */
class PacketHandler extends JSONHandler{

    PacketsService packetsService

    public PacketHandler (PacketsService packetsService) {
        this.packetsService = packetsService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {
      try {
        URI uri = exchange.getRequestURI()
        switch (uri.path) {
          case "/packets/countPackets":
            countPackets (exchange)
            break
          case "/packets/findPackets":
            findPackets (exchange)
            break
        }
      } catch (Exception e) {
        e.printStackTrace()
      }

    }

    //count all packets
    private void countPackets (HttpExchange exchange) {
        packetsService.cleanPackets()
        long count = packetsService.getPacketCount ()
        jsonResponse([status: 'ok', count: count], exchange)
    }

    //find packets
    private void findPackets (HttpExchange exchange) {
        Map params = parseQuery (exchange)
        String query = createRegexQuery (params.query)

        List packets = packetsService.findPackets ([name: ['$regex': query, '$options': 'i' ]])

        jsonResponse([status: 'ok', results: packets], exchange)
    }

    private static String createRegexQuery (String query) {
        String[] parts = StringUtils.split (query, ' ')
        return  parts.join ('.*')
    }
}
