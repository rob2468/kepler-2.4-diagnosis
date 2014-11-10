package org.kepler.diagnosis.gui;

import java.util.HashSet;
import java.util.Vector;

public class DiagnosisRecordDAG {

	public DiagnosisRecordDAG() {
		// TODO Auto-generated constructor stub
	}
	
	public HashSet<SuspiciousDRActor> getVertices() {
		return _vertices;
	}
	public void setVertices(HashSet<SuspiciousDRActor> _vertices) {
		this._vertices = _vertices;
	}

	public HashSet<Vector<SuspiciousDRActor>> getEdges() {
		return _edges;
	}

	public void setEdges(HashSet<Vector<SuspiciousDRActor>> _edges) {
		this._edges = _edges;
	}

	private HashSet<SuspiciousDRActor> _vertices = new HashSet<SuspiciousDRActor>();
	private HashSet<Vector<SuspiciousDRActor>> _edges = new HashSet<Vector<SuspiciousDRActor>>();
}
