package nl.marketingsciences.beans.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="placement_group")
public class PlacementGroup {
	
	//@JsonIgnore
	@Id
    @Column(name = "placement_group_id")
    @GeneratedValue
	private Integer id;
	
	@Column(name = "placement")
	private String placement;

	@Column(name = "groupname")
	private String group;
	
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

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String placement) {
		this.placement = placement;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
