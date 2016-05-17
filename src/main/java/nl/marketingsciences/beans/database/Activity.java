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
@Table(name="activities")
public class Activity {
	
	@Id
	@Column(name = "id")
	@GeneratedValue
	private Integer id;

	@Column(name = "conversion_id",unique=true)
    private String conversionId;
	
	@Column(name = "person_id")
	private Integer personId;

	@Temporal (TemporalType.TIMESTAMP)
	@Column(name = "timestamp")
	private Date timestamp;
	
	@Column(name = "activity")
	private String activity;
	
	@Column(name = "report_id")
	private Long reportId;
	
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
	
    public String getConversionId() {
		return conversionId;
	}

	public void setConversionId(String conversionId) {
		this.conversionId = conversionId;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

//	public String getFloodlightAttributeType() {
//		return floodlightAttributeType;
//	}
//
//	public void setFloodlightAttributeType(String floodlightAttributeType) {
//		this.floodlightAttributeType = floodlightAttributeType;
//	}
}
