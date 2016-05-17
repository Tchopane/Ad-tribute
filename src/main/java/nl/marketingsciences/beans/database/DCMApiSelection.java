package nl.marketingsciences.beans.database;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DCMApiSelection {

	private List<Long> selectedFloodlights;
	private Date startDate;
	private Date endDate;
	private Long floodlightConfigurationId;
	private Long advertiserId;
	
	
	
	public Long getAdvertiserId() {
		return advertiserId;
	}
	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}
	public List<Long> getSelectedFloodlights() {
		return selectedFloodlights;
	}
	public void setSelectedFloodlights(List<Long> selectedFloodlights) {
		this.selectedFloodlights = selectedFloodlights;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getFloodlightConfigurationId() {
		return floodlightConfigurationId;
	}
	public void setFloodlightConfigurationId(Long floodlightConfigurationId) {
		this.floodlightConfigurationId = floodlightConfigurationId;
	}

}
