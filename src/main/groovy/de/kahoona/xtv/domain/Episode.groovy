package de.kahoona.xtv.domain

/**
 * Created by benjamin.ernst on 27.06.2014.
 */
class Episode {

  String name
  int season
  int number

  public Episode (com.omertron.thetvdbapi.model.Episode episode) {
    this.name = episode.episodeName
    this.season = episode.seasonNumber
    this.number = episode.episodeNumber
  }

}
