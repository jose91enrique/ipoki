/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

/**
 *
 * @author Xavi
 */
public class Friend {
	private String name;
	private String latitude;
	private String longitude;
	private String dateTime;

	public Friend(String name, String latitude, String longitude, String dateTime) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dateTime = dateTime;
	}

	public String getName() {
		return this.name;
	}

	public String getLocationTime() {
		return this.dateTime;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}
}

