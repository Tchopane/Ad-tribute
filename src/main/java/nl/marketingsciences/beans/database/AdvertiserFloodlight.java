package nl.marketingsciences.beans.database;

public class AdvertiserFloodlight {

	private long advertiserId;
	private String name;
	private long floodlightConfigurationId;

	public AdvertiserFloodlight(long advertiserId, String name, long floodlightConfigurationId) {
		super();
		this.advertiserId = advertiserId;
		this.name = name;
		this.floodlightConfigurationId = floodlightConfigurationId;
	}

	public AdvertiserFloodlight() {

	}

	public long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFloodlightConfigurationId() {
		return floodlightConfigurationId;
	}

	public void setFloodlightConfigurationId(long floodlightConfigurationId) {
		this.floodlightConfigurationId = floodlightConfigurationId;
	}

}
