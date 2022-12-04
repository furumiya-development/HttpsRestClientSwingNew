package main.java.desk.model;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/** JacksonライブラリーによるJsonデータマッピング
 * @author none **/
public class ShohinEntity {
	@JsonProperty("uniqueId")
	private String uniqueId;
	@JsonProperty("shohinCode")
	private Short shohinCode;
	@JsonProperty("shohinName")
	private String shohinName;
	@JsonProperty("editDate")
	private BigDecimal editDate;
	@JsonProperty("editTime")
	private BigDecimal editTime;
	@JsonProperty("remarks")
	private String remarks;
	
	public String getNumId() {
		return uniqueId;
	}
	public void setNumId(String value) {
		this.uniqueId = value;
	}

	public Short getShohinCode() {
		return shohinCode;
	}
	public void setShohinCode(Short value) {
		this.shohinCode = value;
	}

	public String getShohinName() {
		return shohinName;
	}
	public void setShohinName(String value) {
		this.shohinName = value;
	}

	public BigDecimal getEditDate() {
		return editDate;
	}
	public void setEditDate(BigDecimal value) {
		this.editDate = value;
	}

	public BigDecimal getEditTime() {
		return editTime;
	}
	public void setEditTime(BigDecimal value) {
		this.editTime = value;
	}

	public String getNote() {
		return remarks;
	}
	public void setNote(String value) {
		this.remarks = value;
	}
}