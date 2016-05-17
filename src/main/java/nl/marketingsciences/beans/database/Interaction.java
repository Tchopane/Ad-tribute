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
@Table(name="interactions")
public class Interaction {


	@Id
    @Column(name = "interaction_id")
    @GeneratedValue
	private Integer id;
	
	@Column(name = "person_id")
	private Integer personId;
	
	//@JsonIgnore
	@Temporal (TemporalType.TIMESTAMP)
	@Column(name = "timestamp")
	private Date timestamp;
	
	@Column(name = "imp_click")
	private String impclick;
	
	@Column(name = "placement")
	private String placement;
	
	@Column(name = "report_id")
	private Long reportId;
	
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	public String getPaidSearchCampaign() {
		return paidSearchCampaign;
	}

	public void setPaidSearchCampaign(String paidSearchCampaign) {
		this.paidSearchCampaign = paidSearchCampaign;
	}

	public String getPaidSearchKeyword() {
		return paidSearchKeyword;
	}

	public void setPaidSearchKeyword(String paidSearchKeyword) {
		this.paidSearchKeyword = paidSearchKeyword;
	}

	public String getPaidSearchMatchType() {
		return paidSearchMatchType;
	}

	public void setPaidSearchMatchType(String paidSearchMatchType) {
		this.paidSearchMatchType = paidSearchMatchType;
	}

	@Column(name = "paid_search_campaign")
	private String paidSearchCampaign;
	
	@Column(name = "paid_search_keyword")
	private String paidSearchKeyword;
	
	@Column(name = "paid_search_match_type")
	private String paidSearchMatchType;
	
	
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getImpclick() {
		return impclick;
	}

	public void setImpclick(String impclick) {
		this.impclick = impclick;
	}

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String placement) {
		this.placement = placement;
	}


	
}
