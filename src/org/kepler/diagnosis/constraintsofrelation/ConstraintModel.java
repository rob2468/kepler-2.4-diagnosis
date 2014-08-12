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
	
	public String getConstraints()
	{
		return _constraints;
	}

	public void setConstraints(String _constraints)
	{
		this._constraints = _constraints;
	}

	// display string
	private String _name = "";
	
	// filter conditions
	private String _constraints = "id==352 || data.equals(\"false\")";
}
