package de.kahoona.xtv.server

import com.omertron.thetvdbapi.TheTVDBApi
import com.omertron.thetvdbapi.model.Episode
import com.omertron.thetvdbapi.model.Series
import com.sun.net.httpserver.HttpExchange
import de.kahoona.xtv.domain.Show
import de.kahoona.xtv.services.ShowsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Benni on 19.04.2014.
 */
class ShowsHandler extends JSONHandler {

  private static Logger log = LoggerFactory.getLogger(ShowsHandler.class)
  TheTVDBApi tvDB
  ShowsService showsService


  public ShowsHandler(ShowsService showsService) {
    this.showsService = showsService
    this.tvDB = new TheTVDBApi("6674FEB575D89227");
  }

  @Override
  void handle(HttpExchange exchange) throws IOException {
    try {
      URI uri = exchange.getRequestURI()
      switch (uri.path) {
        case "/shows/load":
          loadShows(exchange)
          break
        case "/shows/search":
          searchShow(exchange)
          break
        case "/shows/add":
          addShow(exchange)
          break
        case "/shows/delete":
          deleteShow(exchange)
          break
        case "/shows/loadEpisodes":
          loadEpisodes(exchange)
          break
        case "/shows/saveEpisode":
          saveEpisode (exchange)
          break
        case "/shows/markShowLoaded":
          markShowLoaded (exchange)
          break
        case "/shows/markSeasonLoaded":
          markSeasonLoaded (exchange)
          break

      }
    } catch (Exception e) {
      log.error('Error while handling request.', e)
    }

  }

  private void loadShows(HttpExchange exchange) {
    def shows = showsService.findAllShows()

    def data = [success: true, status: 'ok', results: shows]
    jsonResponse(data, exchange)
  }

  private void searchShow(HttpExchange exchange) {
    Map params = parseQuery(exchange)
    Iterable<Series> result = tvDB.searchSeries(params.query, null)
    jsonResponse([status: 'ok', results: result], exchange)
  }

  private void addShow(HttpExchange exchange) {
    Map params = parseJsonPost(exchange)

    //create show
    Series series = tvDB.getSeries(params.data.id, null)
    Show show = Show.fromSeries(series)
    def saveResult = showsService.saveShow(show)
    show._id = saveResult.id

    //create episodes
    List<Episode> episodes = tvDB.getAllEpisodes(series.id, null)
    showsService.createEpisodes(show, episodes)

    jsonResponse([success: true, status: 'ok'], exchange)
  }

  private void loadEpisodes (HttpExchange exchange) {
    Map params = parseQuery(exchange)
    def episodes = showsService.loadEpisodes(params.showId as String)
    jsonResponse([status: 'ok', data: episodes], exchange)
  }

  private void deleteShow(HttpExchange exchange) {
    Map params = parseJsonPost(exchange)

    showsService.deleteShow(params.data)

    jsonResponse([success: true, status: 'ok'], exchange)
  }

  private void saveEpisode (HttpExchange exchange) {
    Map params = parseJsonPost(exchange)
    def episode = params.data
    showsService.db.save ('episodes', episode)
    jsonResponse([success: true, status: 'ok'], exchange)
  }

  private void markShowLoaded (HttpExchange exchange) {
    Map params = parseJsonPost(exchange)
    def seasons = showsService.loadEpisodes(params.showId as String)
    seasons.each {season, episodes ->
      episodes.each {episode ->
        episode.loaded = true
        showsService.db.save ('episodes', episode)
      }
    }
    jsonResponse([success: true, status: 'ok'], exchange)
  }

  private void markSeasonLoaded (HttpExchange exchange) {
    Map params = parseJsonPost(exchange)
    def seasons = showsService.loadEpisodes(params.showId as String)
    seasons.each {season, episodes ->
      if (params.season == season) {
        episodes.each {episode ->
          episode.loaded = true
          showsService.db.save ('episodes', episode)
        }
      }
    }
    jsonResponse([success: true, status: 'ok'], exchange)
  }

}
