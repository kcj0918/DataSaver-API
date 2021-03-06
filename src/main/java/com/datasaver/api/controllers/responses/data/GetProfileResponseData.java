package com.datasaver.api.controllers.responses.data;

public class GetProfileResponseData {
	private long idx;
	private String name;
	private String phoneNumber;
	private String profileImgUrl;
	private MostRecentlyUsedWiFi mostRecentlyUsedWiFi;

	public GetProfileResponseData() {
	}

	public GetProfileResponseData(long idx, String name, String phoneNumber, String profileImgUrl,
			MostRecentlyUsedWiFi mostRecentlyUsedWiFi) {
		this.idx = idx;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.profileImgUrl = profileImgUrl;
		this.mostRecentlyUsedWiFi = mostRecentlyUsedWiFi;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUr(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public MostRecentlyUsedWiFi getMostRecentlyUsedWiFi() {
		return mostRecentlyUsedWiFi;
	}

	public void setMostRecentlyUsedWiFi(MostRecentlyUsedWiFi mostRecentlyUsedWiFi) {
		this.mostRecentlyUsedWiFi = mostRecentlyUsedWiFi;
	}

	public static class MostRecentlyUsedWiFi {
		private long idx;
		private String ssid;
		private double latitude;
		private double longitude;

		public MostRecentlyUsedWiFi() {
		}

		public MostRecentlyUsedWiFi(long idx, String ssid, double latitude, double longitude) {
			this.idx = idx;
			this.ssid = ssid;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public long getIdx() {
			return idx;
		}

		public void setIdx(long idx) {
			this.idx = idx;
		}

		public String getSsid() {
			return ssid;
		}

		public void setSsid(String ssid) {
			this.ssid = ssid;
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
}