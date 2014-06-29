package de.kahoona.xtv.domain

/**
 * Created by benjamin.ernst on 27.06.2014.
 */
class Episode {

  String  _id
  String  showId
  String  name
  int     season
  int     number
  boolean watched
  String  airDate

  public Episode (Show show, com.omertron.thetvdbapi.model.Episode episode) {
    this.showId = show._id
    this.name = episode.episodeName
    this.season = episode.seasonNumber
    this.number = episode.episodeNumber
    this.airDate = episode.firstAired
  }

  Map toMap() {
    return [
        '_id'      : _id,
        'showId'   : showId,
        'name'     : name,
        'season'   : season,
        'number'   : number,
        'watched'  : watched,
        'airDate'  : airDate,
    ]
  }

}
