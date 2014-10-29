package org.kepler.diagnosis.gui;

/**
 * Provenance table row is encapsulated as ProvenanceTableRow
 * */
public class ProvenanceTableRow {

	public ProvenanceTableRow() {
		// TODO Auto-generated constructor stub
	}
	
	public ProvenanceTableRow(Integer tokenID, String tokenValue)
	{
		this._tokenID = tokenID;
		this._tokenValue = tokenValue;
	}

	public Integer getTokenID() {
		return _tokenID;
	}

	public void setTokenID(Integer _tokenID) {
		this._tokenID = _tokenID;
	}

	public String getTokenValue() {
		return _tokenValue;
	}

	public void setTokenValue(String _tokenValue) {
		this._tokenValue = _tokenValue;
	}

	public Integer getSus() {
		return sus;
	}

	public void setSus(Integer sus) {
		this.sus = sus;
	}

	private Integer _tokenID;
	private String _tokenValue;
	
	private Integer sus = 0;
}
