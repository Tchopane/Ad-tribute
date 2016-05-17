package nl.marketingsciences.websocket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import nl.marketingsciences.beans.database.GroupProperties;
import nl.marketingsciences.beans.database.GroupingChoice;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class Globals {
	public int i = 0;
	public int usedLines = 1;	
	public long FILEID;
	public long REPORTID;
	public final String USER_PROFILE_ID = "2147802";
	public boolean excludeBehavior;
	public int actionBehavior;	
	public long currentSessionReportId;
	public List <GroupingChoice> groupingChoices = new ArrayList<GroupingChoice>();
	public List <GroupProperties> allGroupsProperties = new ArrayList<GroupProperties>();
	public List<String> chosenPlacements = new ArrayList<String>();
	
	
	public List<String> getChosenPlacements() {
		return chosenPlacements;
	}
	public void setChosenPlacements(List<String> chosenPlacements) {
		this.chosenPlacements = chosenPlacements;
	}
	public long getCurrentSessionReportId() {
		return currentSessionReportId;
	}
	public void setCurrentSessionReportId(long currentSessionReportId) {
		this.currentSessionReportId = currentSessionReportId;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getUsedLines() {
		return usedLines;
	}
	public void setUsedLines(int usedLines) {
		this.usedLines = usedLines;
	}
	public long getFILEID() {
		return FILEID;
	}
	public void setFILEID(long fILEID) {
		FILEID = fILEID;
	}
	public long getREPORTID() {
		return REPORTID;
	}
	public void setREPORTID(long rEPORTID) {
		REPORTID = rEPORTID;
	}
	public boolean isExcludeBehavior() {
		return excludeBehavior;
	}
	public void setExcludeBehavior(boolean excludeBehavior) {
		this.excludeBehavior = excludeBehavior;
	}
	public int getActionBehavior() {
		return actionBehavior;
	}
	public void setActionBehavior(int actionBehavior) {
		this.actionBehavior = actionBehavior;
	}
	public List<GroupingChoice> getGroupingChoices() {
		return groupingChoices;
	}
	public void setGroupingChoices(List<GroupingChoice> groupingChoices) {
		this.groupingChoices = groupingChoices;
	}
	public List<GroupProperties> getAllGroupsProperties() {
		return allGroupsProperties;
	}
	public void setAllGroupsProperties(List<GroupProperties> allGroupsProperties) {
		this.allGroupsProperties = allGroupsProperties;
	}
	public String getUSER_PROFILE_ID() {
		return USER_PROFILE_ID;
	}
}
