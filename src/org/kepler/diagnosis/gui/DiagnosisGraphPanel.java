package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import diva.graph.JGraph;
import ptolemy.actor.gui.Tableau;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.NamedObj;
import ptolemy.vergil.actor.ActorEditorGraphController;
import ptolemy.vergil.actor.ActorGraphModel;
import ptolemy.vergil.basic.BasicGraphPane;

/**
 * A instance of this class is a panel for the canvas.
 * */
public class DiagnosisGraphPanel extends JPanel
{
	public DiagnosisGraphPanel() {}
	
	public DiagnosisGraphPanel(NamedObj workflow)
	{
		setModel(new ActorGraphModel(workflow));
		
	}
	
	public DiagnosisGraphPanel(CompositeEntity entity, Tableau tableau)
	{
		
	}
	
	public static class Factory
	{
		public DiagnosisGraphPanel createDiagnosisGraphPanel(NamedObj workflow)
		{
			DiagnosisGraphPanel canvasPanel = new DiagnosisGraphPanel();
			
			ActorGraphModel graphModel = new ActorGraphModel(workflow);
			canvasPanel.setModel(graphModel);
			
			ActorEditorGraphController graphController = new ActorEditorGraphController();
			canvasPanel.setController(graphController);
			
			BasicGraphPane graphPane = new BasicGraphPane(canvasPanel.getController(), canvasPanel.getModel(), workflow);
			canvasPanel._jgraph = new JGraph(graphPane);
			
			canvasPanel.setBorder(null);
			canvasPanel.setLayout(new BorderLayout());
			
			canvasPanel.add(canvasPanel._jgraph, BorderLayout.CENTER);
			
			return canvasPanel;
		}
		
	}
	
	public ActorGraphModel getModel()
	{
		return _model;
	}

	public void setModel(ActorGraphModel _model)
	{
		this._model = _model;
	}
	
	public ActorEditorGraphController getController()
	{
		return _controller;
	}

	public void setController(ActorEditorGraphController _controller)
	{
		this._controller = _controller;
	}

	/** The instance of JGraph for this editor */
	private JGraph _jgraph;
	
	private ActorGraphModel _model;
	
	private ActorEditorGraphController _controller;
}
