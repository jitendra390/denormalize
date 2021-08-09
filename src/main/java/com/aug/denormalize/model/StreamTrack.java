package com.aug.denormalize.model;

import java.io.Serializable;


public class StreamTrack implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -4980730564901366478L;

  private String user_id;
  private String cached;
  private String timestamp;
  private String source_uri;
  private String track_id;
  private String source;
  private String length;
  private String version;
  private String device_type;
  private String message;
  private String os;
  private String stream_country;
  private String report_date;
  private String isrc;
  private String album_code;


  public StreamTrack(String user_id, String cached, String timestamp, String source_uri,
      String track_id, String source, String length, String version, String device_type,
      String message, String os, String stream_country, String report_date, String isrc,
      String album_code) {
    super();
    this.user_id = user_id;
    this.cached = cached;
    this.timestamp = timestamp;
    this.source_uri = source_uri;
    this.track_id = track_id;
    this.source = source;
    this.length = length;
    this.version = version;
    this.device_type = device_type;
    this.message = message;
    this.os = os;
    this.stream_country = stream_country;
    this.report_date = report_date;
    this.isrc = isrc;
    this.album_code = album_code;
  }


  public StreamTrack(String user_id, String cached, String timestamp, String source_uri,
      String track_id, String source, String length, String version, String device_type,
      String message, String os, String stream_country, String report_date) {
    super();
    this.user_id = user_id;
    this.cached = cached;
    this.timestamp = timestamp;
    this.source_uri = source_uri;
    this.track_id = track_id;
    this.source = source;
    this.length = length;
    this.version = version;
    this.device_type = device_type;
    this.message = message;
    this.os = os;
    this.stream_country = stream_country;
    this.report_date = report_date;
    this.isrc = null;
    this.album_code = null;
  }

  public String getUser_id() {
    return user_id;
  }


  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }


  public String getCached() {
    return cached;
  }


  public void setCached(String cached) {
    this.cached = cached;
  }


  public String getTimestamp() {
    return timestamp;
  }


  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }


  public String getSource_uri() {
    return source_uri;
  }


  public void setSource_uri(String source_uri) {
    this.source_uri = source_uri;
  }


  public String getTrack_id() {
    return track_id;
  }


  public void setTrack_id(String track_id) {
    this.track_id = track_id;
  }


  public String getSource() {
    return source;
  }


  public void setSource(String source) {
    this.source = source;
  }


  public String getLength() {
    return length;
  }


  public void setLength(String length) {
    this.length = length;
  }


  public String getVersion() {
    return version;
  }


  public void setVersion(String version) {
    this.version = version;
  }


  public String getDevice_type() {
    return device_type;
  }


  public void setDevice_type(String device_type) {
    this.device_type = device_type;
  }


  public String getMessage() {
    return message;
  }


  public void setMessage(String message) {
    this.message = message;
  }


  public String getOs() {
    return os;
  }


  public void setOs(String os) {
    this.os = os;
  }


  public String getStream_country() {
    return stream_country;
  }


  public void setStream_country(String stream_country) {
    this.stream_country = stream_country;
  }


  public String getReport_date() {
    return report_date;
  }


  public void setReport_date(String report_date) {
    this.report_date = report_date;
  }


  public String getIsrc() {
    return isrc;
  }


  public void setIsrc(String isrc) {
    this.isrc = isrc;
  }


  public String getAlbum_code() {
    return album_code;
  }


  public void setAlbum_code(String album_code) {
    this.album_code = album_code;
  }


  public static long getSerialversionuid() {
    return serialVersionUID;
  }





}
