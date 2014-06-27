package de.kahoona.xtv.server

import com.omertron.thetvdbapi.TheTVDBApi
import com.omertron.thetvdbapi.model.Episode
import com.omertron.thetvdbapi.model.Series
import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.domain.Show
import de.kahoona.xtv.domain.XtvSettings
import de.kahoona.xtv.services.ShowsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Benni on 19.04.2014.
 */
class ShowsHandler extends JSONHandler{

  private static Logger log = LoggerFactory.getLogger(ShowsHandler.class)
  TheTVDBApi tvDB
  ShowsService showsService


  public ShowsHandler (ShowsService showsService) {
    this.showsService = showsService
     this.tvDB = new TheTVDBApi("6674FEB575D89227");
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {
      try {
        URI uri = exchange.getRequestURI()
        switch (uri.path) {
          case "/shows/load":
            loadShows (exchange)
            break
          case "/shows/search":
            searchShow (exchange)
            break
          case "/shows/add":
            addShow (exchange)
            break
          case "/shows/delete":
            deleteShow (exchange)
            break

        }
      } catch (Exception e) {
        log.error('Error while handling request.',e)
      }

    }

    private void loadShows (HttpExchange exchange) {
      def shows = showsService.findAllShows ()

      def data = [success: true, status: 'ok', results: shows]
      jsonResponse(data, exchange)
    }

    private void searchShow (HttpExchange exchange) {
      Map params = parseQuery (exchange)
      Iterable<Series> result = tvDB.searchSeries (params.query, null)
      jsonResponse([status: 'ok', results: result], exchange)
    }

    private void addShow (HttpExchange exchange) {
      Map params = parseJsonPost (exchange)


      Series series = tvDB.getSeries (params.data.id, null)
      List<Episode> episodes = tvDB.getAllEpisodes (series.id, null)

      Show show = Show.fromSeries (series)
      show.createEpisodes (episodes)

      showsService.saveShow (show)

      jsonResponse([success: true, status: 'ok'], exchange)
    }

  private void deleteShow (HttpExchange exchange) {
    Map params = parseJsonPost (exchange)

    showsService.deleteShow (params.data)

    jsonResponse([success: true, status: 'ok'], exchange)
  }

}
