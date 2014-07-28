package org.kepler.diagnosis.gui;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.kepler.diagnosis.DiagnosisManager;
import org.kepler.diagnosis.sql.DiagnosisSQLQuery;
import org.kepler.objectmanager.lsid.KeplerLSID;
import org.kepler.provenance.QueryException;
import org.kepler.util.WorkflowRun;

import diva.graph.GraphUtilities;
import diva.graph.JGraph;
import diva.gui.toolbox.JCanvasPanner;
import diva.util.java2d.ShapeUtilities;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.gui.SizeAttribute;
import ptolemy.actor.gui.Tableau;
import ptolemy.kernel.ComponentRelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.Location;
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
		// add graph model to the graph panel
		ActorGraphModel graphModel = new ActorGraphModel(workflow);
		setModel(graphModel);
		
		// add graph controller to the graph panel
		ActorEditorGraphController graphController = new ActorEditorGraphController();
		setController(graphController);
		
		// set graph pane of _jgraph
		BasicGraphPane graphPane = new BasicGraphPane(getController(), getModel(), workflow);
		_jgraph = new JGraph(graphPane);
				
		_graphPanner = new JCanvasPanner(_jgraph);
	}
	
	public DiagnosisGraphPanel(CompositeEntity entity, Tableau tableau)
	{
		
	}
	
	/**
     * Return the size of the visible part of the canvas, in canvas coordinates.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getVisibleSize() {
        AffineTransform current = _jgraph.getGraphPane().getCanvas()
                .getCanvasPane().getTransformContext().getTransform();
        AffineTransform inverse;
        try {
            inverse = current.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e.toString());
        }
        Dimension size = _jgraph.getGraphPane().getCanvas().getSize();
        Rectangle2D visibleRect = new Rectangle2D.Double(0, 0, size.getWidth(),
                size.getHeight());
        return ShapeUtilities.transformBounds(visibleRect, inverse);
    }
    
	// initial this graph panel according to variables set in the ctor
	public void initDiagnosisGraphPanel()
	{
		// set this graph panel
		setBorder(null);
		setLayout(new BorderLayout());
		
		try
		{
			SizeAttribute size = (SizeAttribute) ((NamedObj) _model.getRoot()).getAttribute("_vergilSize", SizeAttribute.class);
			if (size != null)
				size.setSize(_jgraph);
		} catch (IllegalActionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set scroll bar and add to the graph panel
		_horizontalScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
		_verticalScrollBar = new JScrollBar(Adjustable.VERTICAL);
					
		add(_horizontalScrollBar, BorderLayout.SOUTH);
		add(_verticalScrollBar, BorderLayout.EAST);
		
		_horizontalScrollBar.setModel(_jgraph.getGraphPane().getCanvas().getHorizontalRangeModel());
		_verticalScrollBar.setModel(_jgraph.getGraphPane().getCanvas().getVerticalRangeModel());

		_horizontalScrollBarListener =  new ScrollBarListener(_horizontalScrollBar);
		_verticalScrollBarListener = new ScrollBarListener(_verticalScrollBar);
		
		_horizontalScrollBar.addAdjustmentListener(_horizontalScrollBarListener);
		_verticalScrollBar.addAdjustmentListener(_verticalScrollBarListener);
		
		add(_jgraph, BorderLayout.CENTER);
	}
	
	/** Create all table panes on the basis of _model and store in _allTablePanes */
	public void createAllTablePanes()
	{
		if (_allTablePanes == null)
		{
			_allTablePanes = new Vector<JComponent>();

			// collect locatable nodes for actors
			Vector<Location> locatableNodes = new Vector<Location>();
			Iterator<?> nodesIter = GraphUtilities.nodeSet(_model.getRoot(), _model).iterator();
			while (nodesIter.hasNext())
			{
				Object node = nodesIter.next();
				if (node instanceof Location)
				{
					locatableNodes.addElement((Location) node);
				}
			}
			
			// collect relations according to edges(link)
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
			
			// prepare provenance data
			DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
			
			// create provenance table panel for each relation
			Iterator<ComponentRelation> relationsIter = relations.iterator();
			while (relationsIter.hasNext())
			{
				ComponentRelation relation = relationsIter.next();
				
				ProvenanceTablePane.Factory factory = new ProvenanceTablePane.Factory();
				ProvenanceTablePane tablePane = (ProvenanceTablePane) factory.createProvenanceTablePane();
				tablePane.setRelation(relation);
				
				int x = 0, y = 0, width = 100, height = 100;
				
				List<?> ports = relation.linkedPortList();
				Iterator<?> portsIter = ports.iterator();
				int num = 0;
				
				List<Integer> tokenIDs = null;
				while (portsIter.hasNext())
				{
					TypedIOPort port = (TypedIOPort) portsIter.next();
					NamedObj node = port.getContainer();
					for (int i = 0; i<locatableNodes.size(); i++)
					{
						if (locatableNodes.elementAt(i).getContainer() == node)
						{
							double []location = locatableNodes.elementAt(i).getLocation();
							x += (int) location[0];
							y += (int) location[1];
							num++;
							
							String fullPortName = port.getFullName();
							Integer portID;
							try
							{
								if (tokenIDs == null || tokenIDs.size()==0)
								{
									String entityName = fullPortName.substring(1);
									int firstDot = entityName.indexOf('.');
									entityName = entityName.substring(firstDot);
									portID = query.getEntityId(entityName, _workflowLSID);
									
									tokenIDs = query.getTokensForExecution(_runID, portID, false);
								}
							} catch (QueryException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							break;
						}// if
					}// for
				}// while
				if (num != 0)
				{
					x /= num;
					y /= num;
				}
				
				DefaultTableModel tableModel = new DefaultTableModel();
				Vector<String> columnIdentifiers = new Vector<String>();
				columnIdentifiers.addElement("id");
				columnIdentifiers.addElement("data");
				
				tableModel.setColumnIdentifiers(columnIdentifiers);
				if (tokenIDs != null)
				{
					for (int i=0; i<tokenIDs.size(); i++)
					{
						Integer tokenID = tokenIDs.get(i);
						String tokenValue = "";
						try
						{
							tokenValue = query.getTokenValue(tokenID);
						} catch (QueryException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Vector<Object> rowData = new Vector<Object>();
						rowData.addElement(tokenID);
						rowData.addElement(tokenValue);
						tableModel.addRow(rowData);
					}
				}
				tablePane.setTablePaneModel(tableModel);
				tablePane.setBounds(x, y,  width, height);				
				
				for (int i=0; i<columnIdentifiers.size(); i++)
				{
					TableColumn tc = tablePane.getTablePane().getColumn(columnIdentifiers.get(i));
					tc.setCellRenderer(tablePane.new ProvTableCellRenderer());
				}
				_allTablePanes.addElement(tablePane);
			}
		}
	}
	
	public static class Factory
	{
		public DiagnosisGraphPanel createDiagnosisGraphPanel(NamedObj workflow, WorkflowRun wfRun)
		{
			int runID = wfRun.getExecId();
			KeplerLSID workflowLSID = wfRun.getWorkflowLSID();
			
			DiagnosisGraphPanel canvasPanel = new DiagnosisGraphPanel(workflow);
			
			canvasPanel.initDiagnosisGraphPanel();
			canvasPanel.setRunID(runID);
			canvasPanel.setWorkflowLSID(workflowLSID);
			DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
			int workflowID;
			try
			{
				workflowID = query.getWorkflowID(workflowLSID);
				canvasPanel.setWorkflowID(workflowID);
			} catch (QueryException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
	
	/**
     * Listener for scrollbar events.
     */
    public class ScrollBarListener implements java.awt.event.AdjustmentListener
    {
    	
    	public ScrollBarListener(JScrollBar sb)
    	{
             if (sb.getOrientation() == Adjustable.HORIZONTAL)
             {
                 orientation = "h";
             } else
             {
                 orientation = "v";
             }
         }
    	 
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			int val = e.getValue();
			
			if (orientation.equals("h"))
			{
				Rectangle2D visibleRect = getVisibleSize();
                Point2D newLeft = new Point2D.Double(val, 0);
                AffineTransform newTransform = _jgraph.getGraphPane()
                        .getCanvas().getCanvasPane().getTransformContext()
                        .getTransform();
                newTransform.translate(visibleRect.getX() - newLeft.getX(), 0);

                _jgraph.getGraphPane().getCanvas().getCanvasPane()
                        .setTransform(newTransform);

                if (_graphPanner != null) {
                    _graphPanner.repaint();
                }
                
                Iterator<JComponent> tablePanesIte = _allTablePanes.iterator();
				while (tablePanesIte.hasNext())
				{
					JComponent tablePane = tablePanesIte.next();
					
					double xVal = visibleRect.getX() - newLeft.getX();
					int x = tablePane.getX();
					int y = tablePane.getY();
					int width = tablePane.getWidth();
					int height = tablePane.getHeight();
					tablePane.setBounds(x+(int)xVal, y, width, height);
				}
			} else
			{
				Rectangle2D visibleRect = getVisibleSize();
                Point2D newTop = new Point2D.Double(0, val);
                AffineTransform newTransform = _jgraph.getGraphPane()
                        .getCanvas().getCanvasPane().getTransformContext()
                        .getTransform();

                newTransform.translate(0, visibleRect.getY() - newTop.getY());

                _jgraph.getGraphPane().getCanvas().getCanvasPane()
                        .setTransform(newTransform);

                if (_graphPanner != null) {
                    _graphPanner.repaint();
                }
                
                Iterator<JComponent> tablePanesIte = _allTablePanes.iterator();
				while (tablePanesIte.hasNext())
				{
					JComponent tablePane = tablePanesIte.next();
					
					double yVal = visibleRect.getY() - newTop.getY();
					int x = tablePane.getX();
					int y = tablePane.getY();
					int width = tablePane.getWidth();
					int height = tablePane.getHeight();
					tablePane.setBounds(x, y+(int)yVal, width, height);
				}
			}
		}
		
		private String orientation = "";
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
	
	public int getRunID()
	{
		return _runID;
	}

	public void setRunID(int _runID)
	{
		this._runID = _runID;
	}

	public ActorEditorGraphController getController()
	{
		return _controller;
	}

	public void setController(ActorEditorGraphController _controller)
	{
		this._controller = _controller;
	}

	public int getWorkflowID()
	{
		return _workflowID;
	}

	public void setWorkflowID(int _workflowID)
	{
		this._workflowID = _workflowID;
	}
	
	public KeplerLSID getWorkflowLSID()
	{
		return _workflowLSID;
	}

	public void setWorkflowLSID(KeplerLSID _workflowLSID)
	{
		this._workflowLSID = _workflowLSID;
	}

	/** The instance of JGraph for this editor */
	private JGraph _jgraph;
	
	private ActorGraphModel _model;
	
	private ActorEditorGraphController _controller;
	
	/** The workflow run id of this diagnosis graph panel corresponds to */
	private int _runID;
	
	private int _workflowID;
	
	private KeplerLSID _workflowLSID;
	
	private JCanvasPanner _graphPanner;
	
	/** All table panes that used to display provenance data */
	private Vector<JComponent> _allTablePanes;
	
	private JScrollBar _horizontalScrollBar;
	private ScrollBarListener _horizontalScrollBarListener;
	
	private JScrollBar _verticalScrollBar;
	private ScrollBarListener _verticalScrollBarListener;
	
}
