package nl.marketingsciences.beans.database;

import java.util.List;

public class GroupProperties {
	private String groupId;
	private List<String> campaignsList;
	private List<String> keywordList;
	
	
	public List<String> getCampaignsList() {
		return campaignsList;
	}
	public void setCampaignsList(List<String> campaignsList) {
		this.campaignsList = campaignsList;
	}
	public List<String> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
