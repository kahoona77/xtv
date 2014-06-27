package de.kahoona.xtv.services

import de.kahoona.xtv.db.MongoDB
import de.kahoona.xtv.domain.Packet
import de.kahoona.xtv.domain.Show
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

}
