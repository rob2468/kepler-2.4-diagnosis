package org.kepler.diagnosis.constraintsofrelation;

import javax.swing.tree.DefaultMutableTreeNode;

public class ConstraintMutableTreeNode extends DefaultMutableTreeNode {

	public ConstraintMutableTreeNode() {
		// TODO Auto-generated constructor stub
	}

	public ConstraintMutableTreeNode(Object userObject) {
		super(userObject);
		
		
		
	}

	public ConstraintMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		// TODO Auto-generated constructor stub
	}
	
	public NodeStates getState() {
		return state;
	}

	public void setState(NodeStates state) {
		this.state = state;
	}

	public enum NodeStates {CLEAR, GOOD, BAD};
	private NodeStates state = NodeStates.CLEAR;
}
