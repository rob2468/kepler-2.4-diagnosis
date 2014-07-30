package org.kepler.diagnosis.workflowmanager.gui;

public class WorkflowRow
{
	private Integer _id = null;
	private String _name = null;
	private String _lsid = null;
	
	public Integer getId()
	{
		return _id;
	}
	public void setId(Integer _id)
	{
		this._id = _id;
	}
	public String getName()
	{
		return _name;
	}
	public void setName(String _name)
	{
		this._name = _name;
	}
	public String getLsid()
	{
		return _lsid;
	}
	public void setLsid(String _lsid)
	{
		this._lsid = _lsid;
	}
}
