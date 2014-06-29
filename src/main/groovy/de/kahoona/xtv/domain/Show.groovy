package de.kahoona.xtv.domain

import com.mongodb.util.JSON
import com.omertron.thetvdbapi.model.Series
import org.apache.commons.lang3.StringUtils

/**
 * Created by benjamin.ernst on 27.06.2014.
 */
class Show {

  String _id
  String name
  String tvdbId

  static Show fromSeries (Series series) {
    return new Show(
      name: series.seriesName,
      tvdbId: series.id
    )
  }

  Map toMap() {
    return [
      '_id'      : _id,
      'name'     : name,
      'tvdbId'   : tvdbId,
    ]
  }
}
