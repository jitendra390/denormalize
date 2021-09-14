package com.aug.denormalize.model;

import java.io.Serializable;

public class Streamz implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1224432L;
	private String message;
	private String version;
	private String user_id; 
	private String track_id;
	private String length;
	private String cached;
	private String source; 
	private String source_uri; 
	private String device_type; 
	private String os;
	private String stream_country; 
	private String timestamp; 
	private String report_date;
	
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

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getTrack_id() {
		return track_id;
	}

	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getCached() {
		return cached;
	}

	public void setCached(String cached) {
		this.cached = cached;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource_uri() {
		return source_uri;
	}

	public void setSource_uri(String source_uri) {
		this.source_uri = source_uri;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getReport_date() {
		return report_date;
	}

	public void setReport_date(String report_date) {
		this.report_date = report_date;
	}


}
