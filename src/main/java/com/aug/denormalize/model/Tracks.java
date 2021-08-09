package com.aug.denormalize.model;

import java.io.Serializable;

public class Tracks implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 8389092612870029688L;

  private String message;
  private String version;
  private String track_id;
  private String uri;
  private String Isrc;
  private String album_code;
  private String album_artist;
  private String track_name;
  private String album_name;
  private String track_artists;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTrack_id() {
    return track_id;
  }

  public void setTrack_id(String track_id) {
    this.track_id = track_id;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getIsrc() {
    return Isrc;
  }

  public void setIsrc(String isrc) {
    Isrc = isrc;
  }

  public String getAlbum_code() {
    return album_code;
  }

  public void setAlbum_code(String album_code) {
    this.album_code = album_code;
  }

  public String getAlbum_artist() {
    return album_artist;
  }

  public void setAlbum_artist(String album_artist) {
    this.album_artist = album_artist;
  }

  public String getTrack_name() {
    return track_name;
  }

  public void setTrack_name(String track_name) {
    this.track_name = track_name;
  }

  public String getAlbum_name() {
    return album_name;
  }

  public void setAlbum_name(String album_name) {
    this.album_name = album_name;
  }

  public String getTrack_artists() {
    return track_artists;
  }

  public void setTrack_artists(String track_artists) {
    this.track_artists = track_artists;
  }


}
