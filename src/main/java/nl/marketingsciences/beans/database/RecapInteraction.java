package nl.marketingsciences.beans.database;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="recap_interactions")
public class RecapInteraction {
	@Id
	@Column(name = "id")
	@GeneratedValue
	private Integer id;
	
	@Column(name = "person_id")
    private Integer personId;
	
	@Column(name = "group_id")
	private int groupId;
	
	@Column(name = "begin_state")
	private String beginState;
	
	@Column(name = "end_state")
	private String endState;
	
	@Temporal (TemporalType.TIMESTAMP)
	@Column(name = "timestamp")
	private Date timestamp;
	
	@Column(name = "time_to_next_state")
	private int timeToNextState;
	
	@Column(name = "report_id")
	private Long reportId;
	
	@Column(name = "weight")
	private float weight;
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public int getGroup() {
		return groupId;
	}

	public void setGroup(int groupId) {
		this.groupId = groupId;
	}

	public String getBeginState() {
		return beginState;
	}

	public void setBeginState(String beginState) {
		this.beginState = beginState;
	}

	public String getEndState() {
		return endState;
	}

	public void setEndState(String endState) {
		this.endState = endState;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getTimeToNextState() {
		return timeToNextState;
	}

	public void setTimeToNextState(int timeToNextState) {
		this.timeToNextState = timeToNextState;
	}
}
