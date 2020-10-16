package com.mycompany.app;


public class IpAddressModel {
	private String region_name;
	private String country_name;
	private String city_name;
	private double latitude;
	private double longitude;
	
	public IpAddressModel(String region_name, String country_name,String city_name, double latitude, double longitude) {
		this.setRegion_name(region_name);
		this.setCountry_name(country_name);
		this.setCity_name(city_name);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public String getRegion_name() {
		return region_name;
	}
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public String getCountry_name() {
		return country_name;
	}

	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}