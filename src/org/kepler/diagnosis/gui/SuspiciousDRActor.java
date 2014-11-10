package org.kepler.diagnosis.gui;

public class SuspiciousDRActor {

	public SuspiciousDRActor() {
		// TODO Auto-generated constructor stub
	}
	/** 
	 * @param name
	 * @param sus
	 * */
	public SuspiciousDRActor(String name, int sus) {
		_name = name;
		this.sus = sus;
	}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public int getSus() {
		return sus;
	}
	public void setSus(int sus) {
		this.sus = sus;
	}
	public float getVal() {
		return val;
	}
	public void setVal(float val) {
		this.val = val;
	}
	private String _name;
	private int sus = 0;
	private float val = 0;		// normalized sus value
}
