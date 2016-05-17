package nl.marketingsciences.beans.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tms")
public class TMS {
	
	@Id
	@Column(name = "id")
	@GeneratedValue
	private Integer id;
	
	@Column(name = "imp_click")
	private String impclick;
	
	@Column(name="group_number")
	private int groupNumber;
	
	@Column(name="state_from")
	private int stateFrom;
	
	@Column(name="state_to")
	private int stateTo;
	
	@Column(name="cell_value")
	private float cellValue;
	
	@Column(name="report_id")
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

	public String getImpclick() {
		return impclick;
	}

	public void setImpclick(String impclick) {
		this.impclick = impclick;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getStateFrom() {
		return stateFrom;
	}

	public void setStateFrom(int stateFrom) {
		this.stateFrom = stateFrom;
	}

	public int getStateTo() {
		return stateTo;
	}

	public void setStateTo(int stateTo) {
		this.stateTo = stateTo;
	}

	public float getCellValue() {
		return cellValue;
	}

	public void setCellValue(float cellValue) {
		this.cellValue = cellValue;
	}
}
