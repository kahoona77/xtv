package de.kahoona.xtv.server

import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper


/**
 * Created by Benni on 19.04.2014.
 */
abstract class JSONHandler implements HttpHandler {

    static void jsonResponse (Map data, HttpExchange exchange) {
        JsonBuilder builder = new JsonBuilder()
        builder (data)
        String response = builder.toString()

        Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", 'application/json');
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.bytes);
        exchange.close();
    }

    static Map parseJsonPost(HttpExchange exchange) {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        JsonSlurper slurper = new JsonSlurper()
        return slurper.parseText(query) as Map
    }

    static Map parseQuery(HttpExchange exchange) {
        String query = exchange.requestURI.query

        def paramMap = query.split('&').collectEntries { param ->
            param.split('=').collect { URLDecoder.decode(it, 'UTF-8') }
        }
        return paramMap
    }
}
