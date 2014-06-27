package de.kahoona.xtv.domain

import com.mongodb.util.JSON
import com.omertron.thetvdbapi.model.Series
import org.apache.commons.lang3.StringUtils

/**
 * Created by benjamin.ernst on 27.06.2014.
 */
class Show {

  String name
  String tvdbId
  Map<String,List<Episode>> episodes

  static Show fromSeries (Series series) {
    return new Show(
      name: series.seriesName,
      tvdbId: series.id
    )
  }

  Map toMap() {
    return [
      '_id'      : "${tvdbId}".toString(),
      'name'     : name,
      'tvdbId'   : tvdbId,
      'episodes' : this.episodes.collectEntries {String season, List<Episode> el ->
          return [season, el.collect {Episode ep ->return [series: this.name,name: ep.name, season: ep.season, number: ep.number]}]
      }
    ]
  }

  void createEpisodes (List<com.omertron.thetvdbapi.model.Episode> episodes) {
    List<Episode> eps = []
    episodes.each {
      eps << new Episode(it)
    }
    this.episodes = eps.groupBy {StringUtils.leftPad (it.season as String, 2, '0')}
  }
}
