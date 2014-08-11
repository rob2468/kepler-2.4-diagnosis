package org.kepler.diagnosis.constraintsofrelation;

/** 
 * Instances of this class are utilized as user objects of tree node
 * */
public class ConstraintModel
{
	public ConstraintModel() {}
	
	public ConstraintModel(String name)
	{
		_name = name;
	}
	
	public String toString()
	{
		return _name;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String _name)
	{
		this._name = _name;
	}
	
	private String _name = "";
	
}
