package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import diva.graph.GraphUtilities;
import diva.graph.JGraph;
import ptolemy.actor.gui.Tableau;
import ptolemy.kernel.ComponentRelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.NamedObj;
import ptolemy.vergil.actor.ActorEditorGraphController;
import ptolemy.vergil.actor.ActorGraphModel;
import ptolemy.vergil.basic.BasicGraphPane;
import ptolemy.vergil.kernel.Link;

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
	
	/** Create all table panes on the basis of _model and store in _allTablePanes */
	public void createAllTablePanes()
	{
		if (_allTablePanes == null)
		{
			_allTablePanes = new Vector<JComponent>();
			
			Vector<ComponentRelation> relations = new Vector<ComponentRelation>();
			Iterator<?> edgesIter = GraphUtilities.totallyContainedEdges(_model.getRoot(), _model);
			while (edgesIter.hasNext())
			{
				Link edge = (Link) edgesIter.next();
				ComponentRelation relationOfEdge = edge.getRelation();
				if (!relations.contains(relationOfEdge))
				{
					relations.addElement(relationOfEdge);
				}
			}
			
			Iterator<ComponentRelation> relationsIter = relations.iterator();
			while (relationsIter.hasNext())
			{
				ComponentRelation relation = relationsIter.next();
				
				Object[][] playerInfo={
		                 {"fudan",new Integer(66)},
		                {"fudan",new Integer(82)},
				};
				String[] Names={"name","test"};
				
				ProvenanceTablePane.Factory factory = new ProvenanceTablePane.Factory();
				JComponent tablePane = factory.createProvenanceTablePane(playerInfo, Names);
				((ProvenanceTablePane) tablePane).setRelation(relation);
				
				int x = 0, y = 0, width = 200, height = 100;
//				if (node instanceof Location)
//				{
//					double []location = ((Location)node).getLocation();
//					x = (int)location[0];
//					y = (int)location[1];
//				}
				tablePane.setBounds(x, y, width, height);
				
				_allTablePanes.addElement(tablePane);
			}
		}
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
			
			canvasPanel.createAllTablePanes();
			Iterator<JComponent> tablePanesIte = canvasPanel._allTablePanes.iterator();
			while (tablePanesIte.hasNext())
			{
				JComponent tablePane = tablePanesIte.next();
				canvasPanel._jgraph.add(tablePane);
			}
			
			return canvasPanel;
		}
		
	}
	
	public JGraph getGraph()
	{
		return _jgraph;
	}
	
	public void setGraph(JGraph _jgraph)
	{
		this._jgraph = _jgraph;
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
	
	/** All table panes that used to display provenance data */
	private Vector<JComponent> _allTablePanes;
}
