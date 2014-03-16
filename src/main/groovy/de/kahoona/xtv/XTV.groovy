/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package de.kahoona.xtv

import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer
import org.vertx.groovy.platform.Verticle

class XTV extends Verticle {

  def start() {
      def log = container.logger

      RouteMatcher routeMatcher = new RouteMatcher()

      routeMatcher.get("/") { req ->
          req.response.sendFile "web/index.html"
      }

      routeMatcher.get("/favicon.ico") { req ->
          req.response.sendFile "web/img/favicon.ico"
      }

      routeMatcher.getWithRegEx("^\\/web\\/.*") { req ->
          req.response.sendFile(req.path.substring(1))
      }

      HttpServer server = vertx.createHttpServer()
      server.requestHandler(routeMatcher.asClosure())

      vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

      server.listen(8080)

      log.info "The http server is started"
  }
}
