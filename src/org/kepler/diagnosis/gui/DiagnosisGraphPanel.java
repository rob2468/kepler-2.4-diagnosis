package org.kepler.diagnosis.gui;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.kepler.diagnosis.DiagnosisManager;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode;
import org.kepler.diagnosis.constraintsofrelation.ConstraintTreeModel;
import org.kepler.diagnosis.constraintsofrelation.gui.ConstraintsOfRelationPanel;
import org.kepler.diagnosis.sql.DiagnosisSQLQuery;
import org.kepler.diagnosis.workflowmanager.gui.WorkflowRow;
import org.kepler.gui.TabManager;
import org.kepler.gui.ViewManager;
import org.kepler.objectmanager.lsid.KeplerLSID;
import org.kepler.provenance.QueryException;
import org.kepler.util.WorkflowRun;
import org.kepler.workflowrunmanager.WorkflowRunManager;
import org.kepler.workflowrunmanager.WorkflowRunManagerManager;

import diva.graph.GraphEvent;
import diva.graph.GraphListener;
import diva.graph.GraphUtilities;
import diva.graph.JGraph;
import diva.gui.toolbox.JCanvasPanner;
import diva.util.java2d.ShapeUtilities;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.gui.SizeAttribute;
import ptolemy.actor.gui.Tableau;
import ptolemy.actor.gui.TableauFrame;
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
	
	public DiagnosisGraphPanel(NamedObj workflow, TableauFrame frame)
	{
		setFrame(frame);
		
		// add graph model to the graph panel
		ActorGraphModel graphModel = new ActorGraphModel(workflow);
		setModel(graphModel);
		
		// add graph controller to the graph panel
		ActorEditorGraphController graphController = new ActorEditorGraphController();
		graphController.setConfiguration(frame.getConfiguration());
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
		// collect all ports
		LinkedList<TypedIOPort> tmpAllPorts = new LinkedList<TypedIOPort>();
		Iterator<?> nodesIter = GraphUtilities.nodeSet(_model.getRoot(), _model).iterator();
		while (nodesIter.hasNext())
		{
			Object node = nodesIter.next();
			if (node instanceof TypedIOPort)
			{
				tmpAllPorts.add((TypedIOPort) node);
			}
		}
		_allPorts = tmpAllPorts;
		
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
			_allTablePanes = new LinkedList<ProvenanceTablePane>();

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
			
			DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();

			// create provenance table panel for each relation
			Iterator<ComponentRelation> relationsIter = relations.iterator();
			while (relationsIter.hasNext())
			{
				ComponentRelation relation = relationsIter.next();
				
				ProvenanceTablePane.Factory factory = new ProvenanceTablePane.Factory();
				ProvenanceTablePane tablePane = (ProvenanceTablePane) factory.createProvenanceTablePane();
				tablePane.setRelation(relation);
				//set title for table pane
				tablePane.getTitleLabel().setText(relation.getName());
								
				List<Integer> tokenIDs = queryRelationTokenIDs(relation, null);
				
				// set table model(table content data) for this table pane
				ProvenanceTableModel tableModel = new ProvenanceTableModel();
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
							e.printStackTrace();
						}
						ProvenanceTableRow rowData = new ProvenanceTableRow(tokenID, tokenValue);
						tableModel.addRow(rowData);
					}
				}
				tablePane.setTablePaneModel(tableModel);
				
				// add list selection listener
				tablePane.getTablePane().getSelectionModel().addListSelectionListener(_listSelectionListener);
				
				// initial location
				int x = 0, y = 0, width = 100, height = 100;
				tablePane.setBounds(x, y,  width, height);
				
				// custom table cell
				for (int i=0; i<columnIdentifiers.size(); i++)
				{
					TableColumn tc = tablePane.getTablePane().getColumn(columnIdentifiers.get(i));
					tc.setCellRenderer(new ProvenanceTableCellRenderer());
				}
				
				_allTablePanes.add(tablePane);
				
				layoutAllTablePanes();
			}
		}
	}
	
	public static class TokenAndPort
	{
		private Integer _tokenID;
		private Integer _portID;
		public TokenAndPort() {}
		public TokenAndPort(Integer tokenID, Integer portID)
		{
			_tokenID = tokenID;
			_portID = portID;
		}
		public Integer getTokenID()
		{
			return _tokenID;
		}
		public void setTokenID(Integer _tokenID)
		{
			this._tokenID = _tokenID;
		}
		public Integer getPortID()
		{
			return _portID;
		}
		public void setPortID(Integer _portID)
		{
			this._portID = _portID;
		}
	}
	
	/** Query relation related (a.k.a provenance table pane related) token ids
	 *  The returned token ids will be displayed in the relation related provenance table pane
	 *  @param relation The specific relation
	 *  @param readTokenIDs Token ids for the read port event ids. If it's not null, it won't be calculated
	 *  @return relation related token ids
	 *  */
	public LinkedList<Integer> queryRelationTokenIDs(ComponentRelation relation, LinkedList<TokenAndPort> readTokenIDs)
	{
		List<?> ports = relation.linkedPortList();
		Iterator<?> portsIter = ports.iterator();
		
		DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
		
		LinkedList<Integer> tokenIDs = new LinkedList<Integer>();
		LinkedList<TokenAndPort> writeTokenIDs = new LinkedList<TokenAndPort>();
		
		// collect all write token ids
		while (portsIter.hasNext())
		{
			TypedIOPort port = (TypedIOPort) portsIter.next();

			String fullPortName = port.getFullName();
			Integer portID;
			
			try
			{
				String entityName = fullPortName.substring(1);
				int firstDot = entityName.indexOf('.');
				entityName = entityName.substring(firstDot);
				portID = query.getEntityId(entityName, _workflowLSID);
				
				if (getGraphType().equals(WORKFLOW_GRAPH_TYPE))
				{
					List<Integer> list1 = query.getWriteTokensForPortID(portID);
					Iterator<Integer> list1Iter = list1.iterator();
					while (list1Iter.hasNext())
					{
						Integer id = list1Iter.next();
						writeTokenIDs.add(new TokenAndPort(id, portID));
					}
				}
				else// if (getGraphType().equals(WORKFLOW_RUN_GRAPH_TYPE))
				{
					List<Integer> list1 = query.getWriteTokensForExecutionAndPortID(_runID, portID);
					Iterator<Integer> list1Iter = list1.iterator();
					while (list1Iter.hasNext())
					{
						Integer id = list1Iter.next();
						writeTokenIDs.add(new TokenAndPort(id, portID));
					}					
				}
			} catch (QueryException e)
			{
				e.printStackTrace();
			}
		}// while
		
		// collect all read token ids if readTokenIDs parameter is null
		if (readTokenIDs == null)
		{
			portsIter = ports.iterator();
			readTokenIDs = new LinkedList<TokenAndPort>();
			while (portsIter.hasNext())
			{
				TypedIOPort port = (TypedIOPort) portsIter.next();
	
				String fullPortName = port.getFullName();
				Integer portID;
				
				try
				{
					String entityName = fullPortName.substring(1);
					int firstDot = entityName.indexOf('.');
					entityName = entityName.substring(firstDot);
					portID = query.getEntityId(entityName, _workflowLSID);
					
					if (getGraphType().equals(WORKFLOW_GRAPH_TYPE))
					{						
						List<Integer> list2 = query.getReadTokensForPortID(portID);
						Iterator<Integer> list2Iter = list2.iterator();
						while (list2Iter.hasNext())
						{
							Integer id = list2Iter.next();
							readTokenIDs.add(new TokenAndPort(id, portID));
						}
					}
					else// if (getGraphType().equals(WORKFLOW_RUN_GRAPH_TYPE))
					{
						List<Integer> list2 = query.getReadTokensForExecutionAndPortID(_runID, portID);
						Iterator<Integer> list2Iter = list2.iterator();
						while (list2Iter.hasNext())
						{
							Integer id = list2Iter.next();
							readTokenIDs.add(new TokenAndPort(id, portID));
						}
					}
				} catch (QueryException e)
				{
					e.printStackTrace();
				}
			}// while
		}
		
		for (int i=0; i<readTokenIDs.size(); i++)
		{
			TokenAndPort r = readTokenIDs.get(i);
			for (int j=0; j<writeTokenIDs.size(); j++)
			{
				TokenAndPort w = writeTokenIDs.get(j);
				if (r.getTokenID().equals(w.getTokenID()) && !r.getPortID().equals(w.getPortID()))
				{
					Integer newToken = r.getTokenID();
					if (!tokenIDs.contains(newToken))
					{
						tokenIDs.add(newToken);
						break;
					}
				}
			}
		}
		
		// sort from small to large
		Integer[] array = tokenIDs.toArray(new Integer[0]);
        Arrays.sort(array);
        LinkedList<Integer> retval = new LinkedList<Integer>();
        for(int i = 0; i < array.length; i++)
        {
        	retval.addLast(array[i]);
        }
        return retval;
	}
	
	public void layoutAllTablePanes()
	{
		Iterator<ProvenanceTablePane> tablePanesIter = _allTablePanes.iterator();
		// iterate all table panes
		while (tablePanesIter.hasNext())
		{
			ProvenanceTablePane tablePane = (ProvenanceTablePane) tablePanesIter.next();
			int x = 0, y = 0, width = 100, height = 100;
			
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
			
			ComponentRelation relation = tablePane.getRelation();
			List<?> ports = relation.linkedPortList();
			Iterator<?> portsIter = ports.iterator();
			int num = 0;
			
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
						break;
					}// if
				}// for
			}// while
			if (num != 0)
			{
				x /= num;
				y /= num;
			}
			tablePane.setBounds(x+_xOffset, y+_yOffset,  width, height);
		}// while iterate all table panes
	}
	
	public boolean isMeetConstraints(String constraintsStr, Integer id, String data)
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		engine.put("id", id);
		engine.put("data", data);
		Boolean res = false;
		try
		{
			res = (Boolean) engine.eval(constraintsStr);
		} catch (ScriptException e)
		{
			e.printStackTrace();
		}
		
		return res;
	}
	// actions for applying constraints
	public void applyConstraintsAndAddSus(String constraintsStr, String relationStr, boolean add)
	{
		int susInterval = (add)? 1: -1;
		
		if (constraintsStr==null || constraintsStr.equals(""))
		{
			return ;
		}
		
		// get the table pane that this constraints correspond to
		ProvenanceTablePane pTablePane = null;
		for (int i=0; i<_allTablePanes.size(); i++)
		{
			ProvenanceTablePane tmpDTablePane = (ProvenanceTablePane) _allTablePanes.get(i);
			if (tmpDTablePane.getRelation().getName().equals(relationStr))
			{
				pTablePane = tmpDTablePane;
				break;
			}
		}
		
		// get the token id according to the constraints		
		LinkedList<Integer> tokenIDs = new LinkedList<Integer>();
		ProvenanceTableModel tableModel = (ProvenanceTableModel) pTablePane.getTablePane().getModel();
		for (int i=0; i<tableModel.getRowCount(); i++)
		{
			Integer id = (Integer) tableModel.getValueAt(i, 0);
			String data = (String) tableModel.getValueAt(i, 1);
			
			if (isMeetConstraints(constraintsStr, id, data))
			{
				tokenIDs.add(id);
				
				ProvenanceTableRow tableRow = tableModel.getTableRowAt(i);
				int susTmp = tableRow.getSus()+susInterval;
				susTmp = (susTmp>=0)? susTmp: 0;
				tableRow.setSus(susTmp);
			}
		}
		
		// DEPENDENCY CALCULATING
		calculateDependencyAndAddSus(tokenIDs, add);
		
		normalizeTokenSusValues();
		repaintAllProvenanceTables();
	}
	
	// actions for selecting one row in provenance pane
	private ListSelectionListener _listSelectionListener = new ListSelectionListener() 
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
				if (listSelectionModel.isSelectionEmpty())
					return ;
				
				// get the table pane that user operate on
				ProvenanceTablePane pTablePane = null;
				for (int i=0; i<_allTablePanes.size(); i++)
				{
					ProvenanceTablePane tmpDTablePane = (ProvenanceTablePane) _allTablePanes.get(i);
					if (tmpDTablePane.getTablePane().getSelectionModel()==listSelectionModel)
					{
						pTablePane = tmpDTablePane;
						break;
					}
				}
				
				// get the token id that user select
				int idxes[] = pTablePane.getTablePane().getSelectedRows();
				LinkedList<Integer> tokenIDs = new LinkedList<Integer>();
				for (int i=0; i<pTablePane.getTablePane().getSelectedRowCount(); i++)
				{
					Integer tokenID = (Integer) pTablePane.getTablePane().getModel().getValueAt(idxes[i], 0);
					tokenIDs.add(tokenID);
				}
				
				// render the selected row
				ProvenanceTableModel tableModel = (ProvenanceTableModel) pTablePane.getTablePane().getModel();
				for (int i=0; i<pTablePane.getTablePane().getSelectedRowCount(); i++)
				{
					int row = pTablePane.getTablePane().convertRowIndexToModel(idxes[i]);
					ProvenanceTableRow tableRow = tableModel.getTableRowAt(row);
					tableRow.setSus(tableRow.getSus()+1);
				}
				//
				pTablePane.getTablePane().clearSelection();
				
				// DEPENDENCY CALCULATING
				calculateDependencyAndAddSus(tokenIDs, true);
				
				normalizeTokenSusValues();
				repaintAllProvenanceTables();
			}
		}
	};
	
	/** given write token id, return all actor fire related read token and port */
	public LinkedList<TokenAndPort> getAllInputTokenIDsForOutputTokenID(Integer writeTokenID)
	{
		LinkedList<TokenAndPort> allInputTokenIDs = new LinkedList<TokenAndPort>();
		
		DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
		
		try
		{
			Integer fireID = query.getActorFireIDForToken(writeTokenID);
			allInputTokenIDs = query.getInputTokenIDsForActorFireID(fireID);
		} catch (QueryException e)
		{
			e.printStackTrace();
		}
		
		return allInputTokenIDs;
	}
	
	/** get token and port related table pane.
	 *  @param tokenAndPort Input token and port
	 **/
	public ProvenanceTablePane getTablePanesForTokenAndPort(TokenAndPort tokenAndPort)
	{
		ProvenanceTablePane pTablePane = null;

		DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
		try
		{
			// get write port name
			String portName1 = query.getPortNameForTokenID(tokenAndPort.getTokenID());
			
			// get write port
			TypedIOPort port1 = null;
			for (int i=0; i<_allPorts.size(); i++)
			{
				String fullPortName = _allPorts.get(i).getFullName();
				String entityName = fullPortName.substring(1);
				int firstDot = entityName.indexOf('.');
				entityName = entityName.substring(firstDot);
				
				if (portName1.equals(entityName))
				{
					port1 = _allPorts.get(i);
					break;
				}
			}
			
			// get read port name
			String portName2 = query.getPortNameForPortID(tokenAndPort.getPortID());
			
			// get read port
			TypedIOPort port2 = null;
			for (int i=0; i<_allPorts.size(); i++)
			{
				String fullPortName = _allPorts.get(i).getFullName();
				String entityName = fullPortName.substring(1);
				int firstDot = entityName.indexOf('.');
				entityName = entityName.substring(firstDot);
				
				if (portName2.equals(entityName))
				{
					port2 = _allPorts.get(i);
					break;
				}
			}
			
			// get provenance table pane
			for (int i=0; i<_allTablePanes.size(); i++)
			{
				ProvenanceTablePane oneTablePane = (ProvenanceTablePane) _allTablePanes.get(i);
				ComponentRelation oneRelation = oneTablePane.getRelation();
				
				if (oneRelation.linkedPortList().contains(port1) && oneRelation.linkedPortList().contains(port2))
				{
					pTablePane = oneTablePane;
					break;
				}
			}
		} catch (QueryException e)
		{
			e.printStackTrace();
		}
		return pTablePane;
	}
	
	public void calculateDependencyAndAddSus(LinkedList<Integer> tokenIDs, boolean add)
	{
		int susInterval = (add)? 1: -1;
		
		if (tokenIDs==null)
			return ;
		
		// get all actor fire related, input tokens
		LinkedList<TokenAndPort> allInputTokenIDs = new LinkedList<TokenAndPort>();
		for (int i=0; i<tokenIDs.size(); i++)
		{
			Integer tokenID = tokenIDs.get(i);
			allInputTokenIDs.addAll(getAllInputTokenIDsForOutputTokenID(tokenID));
		}
		
		// get all provenance table pane that one step preceding
		LinkedList<ProvenanceTablePane> pTablePanes = new LinkedList<ProvenanceTablePane>();
		for (int i=0; i<allInputTokenIDs.size(); i++)
		{
			ProvenanceTablePane tmpPTablePane = getTablePanesForTokenAndPort(allInputTokenIDs.get(i));
			pTablePanes.add(tmpPTablePane);
		}
		LinkedList<ProvenanceTablePane> tmpPTablePanes = new LinkedList<ProvenanceTablePane>();
		for (int i=0; i<pTablePanes.size(); i++)
		{
			if (!tmpPTablePanes.contains(pTablePanes.get(i)))
			{
				tmpPTablePanes.add(pTablePanes.get(i));
			}
		}
		pTablePanes = tmpPTablePanes;
		
		// processes for all the one step preceding dependency table panes
		for (int i=0; i<pTablePanes.size(); i++)
		{
			LinkedList<Integer> localTokenIDs = queryRelationTokenIDs(pTablePanes.get(i).getRelation(), allInputTokenIDs);
			
			// collect rows that need to mark with background color
			if (localTokenIDs != null)
			{
				ProvenanceTableModel tableModel = (ProvenanceTableModel) pTablePanes.get(i).getTablePane().getModel();
				for (int k=0; k<tableModel.getRowCount(); k++)
				{
					Integer value = (Integer) tableModel.getValueAt(k, 0);
					if (localTokenIDs.contains(value))
					{
						int row = pTablePanes.get(i).getTablePane().convertRowIndexToModel(k);
						ProvenanceTableRow tableRow = tableModel.getTableRowAt(row);
						int susTmp = tableRow.getSus()+susInterval;
						susTmp = (susTmp>=0)? susTmp: 0;
						tableRow.setSus(susTmp);
					}
				}
			}
			pTablePanes.get(i).repaint();
			
			// recursing dependency for each table pane
			if (localTokenIDs != null)
			{
				calculateDependencyAndAddSus(localTokenIDs, true);
			}
		}// for table panes
	}
	
	private void normalizeTokenSusValues()
	{
		int maxSus = 0;
		for (int i=0; i<_allTablePanes.size(); i++)
		{
			ProvenanceTableModel tableModel = (ProvenanceTableModel) _allTablePanes.get(i).getTablePane().getModel();
			int tableMaxSus = tableModel.getMaxSusValue();
			if (tableMaxSus > maxSus)
			{
				maxSus = tableMaxSus;
			}
		}
		
		for (int i=0; i<_allTablePanes.size(); i++)
		{
			ProvenanceTableModel tableModel = (ProvenanceTableModel) _allTablePanes.get(i).getTablePane().getModel();
			tableModel.normalizeSusValuesAccordingTo(maxSus);
		}
	}
	
	private void repaintAllProvenanceTables()
	{
		for (int i=0; i<_allTablePanes.size(); i++)
		{
			_allTablePanes.get(i).repaint();
		}
	}
	
	// set contents of the constraints of relation panel, according to this new graph panel
	public void refreshConstraintsOfRelationPanel()
	{
		TabManager tabman = TabManager.getInstance();
		ConstraintsOfRelationPanel cORPanel= (ConstraintsOfRelationPanel) tabman.getTab(_frame, "Constraints Of Relation");
		
		// set graph panel for constraints of relation panel
		cORPanel.setGraphPanel(this);
		JTree relationTree = cORPanel.getRelationTree();
		
		if (constraintTreeModel == null)
		{
			ConstraintMutableTreeNode top = new ConstraintMutableTreeNode("Top");
			for (int i=0; i<_allTablePanes.size(); i++)
			{
				ProvenanceTablePane tablePane = _allTablePanes.get(i);
				String relationName = tablePane.getRelation().getName();
				top.add(new ConstraintMutableTreeNode(relationName));
			}
			constraintTreeModel = new ConstraintTreeModel(top);
		}
		relationTree.setModel(constraintTreeModel);
	}
	
	public static class Factory
	{
		// create diagnosis graph panel for a single workflow run
		public DiagnosisGraphPanel createDiagnosisGraphPanel(NamedObj workflow, WorkflowRun wfRun, TableauFrame frame)
		{
			int runID = wfRun.getExecId();
			KeplerLSID workflowLSID = wfRun.getWorkflowLSID();
			
			DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
			Integer workflowID = null;
			try
			{
				workflowID = query.getWorkflowID(workflowLSID);
			} catch (QueryException e1)
			{
				e1.printStackTrace();
			}
			
			// check if this workflow run diagnosis graph panel has already been added
			ViewManager vm = ViewManager.getInstance();
			Vector<Component> canvasPanels = vm.getDiagnosisCanvases();
			int i;
			for (i=0; i<canvasPanels.size(); i++)
			{
				DiagnosisGraphPanel temp = (DiagnosisGraphPanel) canvasPanels.get(i);
				if (temp.getGraphType().equals(WORKFLOW_RUN_GRAPH_TYPE) && temp.getRunID()==runID)
				{
					return temp;
				}
			}
			
			DiagnosisGraphPanel canvasPanel = new DiagnosisGraphPanel(workflow, frame);
			canvasPanel.setGraphType(WORKFLOW_RUN_GRAPH_TYPE);
			canvasPanel.initDiagnosisGraphPanel();
			canvasPanel.setRunID(runID);
			canvasPanel.setWorkflowLSID(workflowLSID);
			
			canvasPanel.setWorkflowID(workflowID);
			canvasPanel.setTitle(workflow.getName());
			
			// create all table panes after set variables
			canvasPanel.createAllTablePanes();
			Iterator<ProvenanceTablePane> tablePanesIter = canvasPanel._allTablePanes.iterator();
			while (tablePanesIter.hasNext())
			{
				JComponent tablePane = tablePanesIter.next();
				canvasPanel._jgraph.add(tablePane);
			}
			
			return canvasPanel;
		}
		
		// create diagnosis graph panel for a workflow
		public DiagnosisGraphPanel createDiagnosisGraphPanel(WorkflowRow workflowRow, TableauFrame frame)
		{
			int workflowID = workflowRow.getId();
			String workflowName = workflowRow.getName();
			KeplerLSID workflowLSID = null;
			DiagnosisSQLQuery query;
			KeplerLSID runLSID = null;
			try
			{
				// add a fake revision number to the workflow lsid
				workflowLSID = new KeplerLSID(workflowRow.getLsid()+":1");
				query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
				runLSID = query.getLastExecutionLSIDForWorkflow(workflowLSID);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// check if this workflow diagnosis graph panel has already been added
			ViewManager vm = ViewManager.getInstance();
			Vector<Component> canvasPanels = vm.getDiagnosisCanvases();
			int i;
			for (i=0; i<canvasPanels.size(); i++)
			{
				DiagnosisGraphPanel temp = (DiagnosisGraphPanel) canvasPanels.get(i);
				if (temp.getGraphType().equals(WORKFLOW_GRAPH_TYPE) && temp.getWorkflowID()==workflowID)
				{
					return temp;
				}
			}
			
			WorkflowRunManagerManager wrmm = WorkflowRunManagerManager.getInstance();
			WorkflowRunManager wrm = wrmm.getWRM(frame);
			NamedObj workflow = wrm.getAssociatedWorkflowForWorkflowRun(runLSID);
			
			DiagnosisGraphPanel canvasPanel = new DiagnosisGraphPanel(workflow, frame);
			canvasPanel.setGraphType(WORKFLOW_GRAPH_TYPE);
			canvasPanel.initDiagnosisGraphPanel();
			canvasPanel.setWorkflowID(workflowID);
			canvasPanel.setTitle(workflowName);
			canvasPanel.setWorkflowLSID(workflowLSID);
			
			// create all table panes after set variables
			canvasPanel.createAllTablePanes();
			Iterator<ProvenanceTablePane> tablePanesIter = canvasPanel._allTablePanes.iterator();
			while (tablePanesIter.hasNext())
			{
				JComponent tablePane = tablePanesIter.next();
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
                
                 _xOffset += (int) (visibleRect.getX() - newLeft.getX());
                Iterator<ProvenanceTablePane> tablePanesIte = _allTablePanes.iterator();
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
                
                _yOffset += (int) (visibleRect.getY() - newTop.getY());
                Iterator<ProvenanceTablePane> tablePanesIte = _allTablePanes.iterator();
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
    
    private class ChangeListener implements GraphListener
    {

		@Override
		public void edgeHeadChanged(GraphEvent e)
		{
			
		}

		@Override
		public void edgeTailChanged(GraphEvent e)
		{
			
		}

		@Override
		public void nodeAdded(GraphEvent e)
		{
			
		}

		@Override
		public void nodeRemoved(GraphEvent e)
		{
			
		}

		@Override
		public void structureChanged(GraphEvent e)
		{			
			layoutAllTablePanes();
		}
    }
    
    public String getTitle()
	{
		return _title;
	}

	public void setTitle(String _title)
	{
		this._title = _title;
	}
	
    public TableauFrame getFrame()
    {
    	return _frame;
    }
    
    public void setFrame(TableauFrame _frame)
    {
    	this._frame = _frame;
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
		this._model.addGraphListener(_localListener);
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
	
	public String getGraphType()
	{
		return _graphType;
	}
	
	public void setGraphType(String _graphType)
	{
		this._graphType = _graphType;
	}
	
	public KeplerLSID getWorkflowLSID()
	{
		return _workflowLSID;
	}

	public void setWorkflowLSID(KeplerLSID _workflowLSID)
	{
		this._workflowLSID = _workflowLSID;
	}
	
	public ConstraintTreeModel getConstraintTreeModel() {
		return constraintTreeModel;
	}

	public void setConstraintTreeModel(ConstraintTreeModel constraintTreeModel) {
		this.constraintTreeModel = constraintTreeModel;
	}

	private String _title;

	private TableauFrame _frame;
	
	/** The instance of JGraph for this editor */
	private JGraph _jgraph;
	
	private ActorGraphModel _model;
	
	private ActorEditorGraphController _controller;
	
	/** The workflow run id of this diagnosis graph panel corresponds to */
	private int _runID;
	
	private int _workflowID;
	
	private List<TypedIOPort> _allPorts;
	
	/** _graphType indicates that this graph panel is a workflow or a workflow run */
	public final static String WORKFLOW_GRAPH_TYPE = "WORKFLOW_GRAPH_TYPE";
	public final static String WORKFLOW_RUN_GRAPH_TYPE = "WORKFLOW_RUN_GRAPH_TYPE";
	private String _graphType;
	
	private KeplerLSID _workflowLSID;
	
	private JCanvasPanner _graphPanner;
	
	/** All table panes that used to display provenance data */
	private LinkedList<ProvenanceTablePane> _allTablePanes;
		
	private JScrollBar _horizontalScrollBar;
	private ScrollBarListener _horizontalScrollBarListener;
	
	private JScrollBar _verticalScrollBar;
	private ScrollBarListener _verticalScrollBarListener;
	
	private Integer _xOffset = 0;
	private Integer _yOffset = 0;
	
	private ChangeListener _localListener = new ChangeListener();
	
	// each graph panel has a corresponding constraint tree model
	private ConstraintTreeModel constraintTreeModel;
}
