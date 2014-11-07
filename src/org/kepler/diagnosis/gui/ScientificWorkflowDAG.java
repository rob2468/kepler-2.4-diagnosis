package org.kepler.diagnosis.gui;

import java.util.HashSet;
import java.util.Vector;

public class ScientificWorkflowDAG {

	public ScientificWorkflowDAG() {
		// TODO Auto-generated constructor stub
	}

	public HashSet<SuspiciousActor> getVertices() {
		return _vertices;
	}
	public void setVertices(HashSet<SuspiciousActor> _vertices) {
		this._vertices = _vertices;
	}

	public HashSet<Vector<SuspiciousActor>> getEdges() {
		return _edges;
	}

	public void setEdges(HashSet<Vector<SuspiciousActor>> _edges) {
		this._edges = _edges;
	}

	protected HashSet<SuspiciousActor> _vertices;
	protected HashSet<Vector<SuspiciousActor>> _edges;
	
}
