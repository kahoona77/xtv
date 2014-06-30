package de.kahoona.xtv.services

import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.Episode
import de.kahoona.xtv.domain.Packet
import de.kahoona.xtv.domain.Show
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Benni on 21.04.2014.
 */
class ShowsService {

  private static Logger log = LoggerFactory.getLogger(ShowsService.class)

  MongoDB db

  public ShowsService () {
    this.db = MongoDB.newInstance()
  }


  public List findAllShows () {
    return this.db.findAll ('shows')
  }

  public def saveShow (Show show) {
    return this.db.save('shows', show.toMap())
  }

  public def deleteShow (def show) {
    return this.db.remove ('shows', show)
  }

  public List<Episode> createEpisodes (Show show, List<com.omertron.thetvdbapi.model.Episode> episodes) {
    List<Episode> eps = []
    episodes.each {
      Episode episode = new Episode(show, it)
      this.db.save('episodes', episode.toMap())
      eps << episode
    }
    return eps
  }

  public Map loadEpisodes (String showId) {
    def result = db.findWithQuery ('episodes', [showId: showId], 500)
    return result.groupBy {StringUtils.leftPad (it.season as String, 2, '0')}
  }

}
