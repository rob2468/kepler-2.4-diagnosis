package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode.NodeStates;
import org.kepler.diagnosis.gui.DiagnosisGraphPanel;
import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;

import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

public class ConstraintsOfRelationPanel extends JPanel implements TabPane, ActionListener
{

	private TableauFrame _frame;
	private String _title;
	
	public ConstraintsOfRelationPanel(String title)
	{
		_title = title;
	}
	
	@Override
	public void initializeTab() throws Exception
	{
		this.setLayout(new BorderLayout());
		
		setupContextMenu();
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("top");
		JTree relationTree = new JTree(top);
		relationTree.setRootVisible(false);
		relationTree.setShowsRootHandles(true);
		relationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		relationTree.setCellRenderer(new RelationTreeCellRenderer());
		ToolTipManager.sharedInstance().registerComponent(relationTree);
		
		// response to tree node selection
		relationTree.addTreeSelectionListener(new TreeSelectionListener(){
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				
			}
		});
		
		relationTree.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				TreePath path = relationTree.getPathForLocation(evt.getX(), evt.getY());
				if (path != null)
				{
					// response to select tree node selection
					if (evt.getClickCount() == 2)
					{
						ConstraintMutableTreeNode node =  (ConstraintMutableTreeNode) relationTree.getLastSelectedPathComponent();
						if (node == null)
							return;
						
						Object nodeInfo = node.getUserObject();
						if (nodeInfo instanceof ConstraintModel)
						{
							ConstraintModel cm = (ConstraintModel) nodeInfo;
							String relationStr = node.getParent().toString();
							_graphPanel.applyConstraints(cm.getConstraints(), relationStr);
						}
					}
					
					pointedTreeNode = (ConstraintMutableTreeNode) path.getLastPathComponent();
					// pop up context menu
					if (path.getPathCount()==2)
					{
						jTableShowRelationContextMenu(evt);
					}
					else
					{
						jTableShowConstraintContextMenu(evt);
					}
				}
			}
		});
		
		JScrollPane treeView = new JScrollPane(relationTree);
		add(treeView, BorderLayout.CENTER);
		setRelationTree(relationTree);
	}
	
	private void setupContextMenu()
	{
		// for relation
		_addConstraintMenuItem.removeActionListener(this);
		_addConstraintMenuItem.addActionListener(this);
		_addConstraintMenuItem.setActionCommand(ADD_CONSTRAINT);
		_addConstraintMenuItem.setEnabled(true);
		
		_relationPopupMenu.add(_addConstraintMenuItem);
		
		// for constraint
		_goodMenuItem.removeActionListener(this);
		_goodMenuItem.addActionListener(this);
		_goodMenuItem.setActionCommand(GOOD_CONSTRAINT);
		_goodMenuItem.setEnabled(true);
		
		_badMenuItem.removeActionListener(this);
		_badMenuItem.addActionListener(this);
		_badMenuItem.setActionCommand(BAD_CONSTRAINT);
		_badMenuItem.setEnabled(true);
		
		_clearMenuItem.removeActionListener(this);
		_clearMenuItem.addActionListener(this);
		_clearMenuItem.setActionCommand(CLEAR_CONSTRAINT);
		_clearMenuItem.setEnabled(true);
		
		_constraintPopupMenu.add(_goodMenuItem);
		_constraintPopupMenu.add(_badMenuItem);
		_constraintPopupMenu.add(_clearMenuItem);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ADD_CONSTRAINT))
		{
			String dialogTitle = "New constraint for " + pointedTreeNode.getUserObject();
			ConstraintDialog consDialog = new ConstraintDialog(_frame, dialogTitle, this, pointedTreeNode);
			consDialog.setVisible(true);
		}
		else if (e.getActionCommand().equals(GOOD_CONSTRAINT))
		{
			pointedTreeNode.setState(NodeStates.GOOD);
			_relationTree.repaint();
		}
		else if (e.getActionCommand().equals(BAD_CONSTRAINT))
		{
			pointedTreeNode.setState(NodeStates.BAD);
			_relationTree.repaint();
		}
		else if (e.getActionCommand().equals(CLEAR_CONSTRAINT))
		{
			pointedTreeNode.setState(NodeStates.CLEAR);
			_relationTree.repaint();
		}
	}

	private void jTableShowRelationContextMenu(java.awt.event.MouseEvent mouseEvent)
	{
		if (mouseEvent.isPopupTrigger() || mouseEvent.isControlDown())
		{
			_relationPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}
	}
	
	private void jTableShowConstraintContextMenu(java.awt.event.MouseEvent mouseEvent)
	{
		if (mouseEvent.isPopupTrigger() || mouseEvent.isControlDown())
		{
			_constraintPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}
	}
	
	@Override
	public String getTabName()
	{
		return _title;
	}

	@Override
	public TableauFrame getParentFrame()
	{
		return _frame;
	}

	@Override
	public void setParentFrame(TableauFrame parent)
	{
		_frame = parent;
	}
	
	public JTree getRelationTree()
	{
		return _relationTree;
	}

	public void setRelationTree(JTree _relationTree)
	{
		this._relationTree = _relationTree;
	}
	
	public DiagnosisGraphPanel getGraphPanel()
	{
		return _graphPanel;
	}
	
	public void setGraphPanel(DiagnosisGraphPanel _graphPanel)
	{
		this._graphPanel = _graphPanel;
	}

	private JTree _relationTree;
	
	// pop up menu for relation
	private JPopupMenu _relationPopupMenu = new JPopupMenu();
	private static final String ADD_CONSTRAINT = "Add constraint";
	private JMenuItem _addConstraintMenuItem = new JMenuItem(ADD_CONSTRAINT);
	
	// pop up menu for constraints
	private JPopupMenu _constraintPopupMenu = new JPopupMenu();
	private static final String GOOD_CONSTRAINT = "Good";
	private static final String BAD_CONSTRAINT = "Bad";
	private static final String CLEAR_CONSTRAINT = "Clear";
	private JMenuItem _goodMenuItem = new JMenuItem(GOOD_CONSTRAINT);
	private JMenuItem _badMenuItem = new JMenuItem(BAD_CONSTRAINT);
	private JMenuItem _clearMenuItem = new JMenuItem(CLEAR_CONSTRAINT);
	
	private DiagnosisGraphPanel _graphPanel;
	
	// save the tree node that mouse pointing to
	// to solve the problem that the selected node isn't the same with the pointed node
	private ConstraintMutableTreeNode pointedTreeNode;
	
	public static class Factory extends TabPaneFactory
	{

		public Factory(NamedObj container, String name)
				throws IllegalActionException, NameDuplicationException
		{
			super(container, name);
		}
		
		public TabPane createTabPane(TableauFrame parent)
		{
			ConstraintsOfRelationPanel cORPanel = new ConstraintsOfRelationPanel(this.getName());
			return cORPanel;
		}
		
	}

}
