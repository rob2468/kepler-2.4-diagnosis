package org.kepler.diagnosis.gui;

import ptolemy.actor.Actor;

public class SuspiciousActor {

	public SuspiciousActor() {
		// TODO Auto-generated constructor stub
	}
	
	public SuspiciousActor(Actor actor)
	{
		_actor = actor;
	}
	
	public Actor getActor() {
		return _actor;
	}
	public void setActor(Actor _actor) {
		this._actor = _actor;
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

	private Actor _actor;
	private int sus = 0;
	private float val = 0;		// normalized sus value

}
