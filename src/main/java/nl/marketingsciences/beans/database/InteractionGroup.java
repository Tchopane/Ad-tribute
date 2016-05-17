package nl.marketingsciences.beans.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="interaction_group")
public class InteractionGroup {

	@Id
	@Column(name = "interaction_group_id")
	private int interactionGroupId;
	
    @Column(name = "interaction_id")
	private int interactionId;
	
	@Column(name = "groupname")
	private String groupName;
	
	@Column(name="group_number")
	private int groupNumber;

	@Column(name = "report_id")
	private Long reportId;
	
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	public int getInteractionGroupId() {
		return interactionGroupId;
	}

	public void setInteractionGroupId(int interactionGroupId) {
		this.interactionGroupId = interactionGroupId;
	}

	public int getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(int interactionId) {
		this.interactionId = interactionId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	
}
