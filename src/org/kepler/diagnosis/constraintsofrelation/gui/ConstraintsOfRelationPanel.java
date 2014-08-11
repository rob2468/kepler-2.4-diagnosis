package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;
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
		
		// response to select tree node selection
		relationTree.addTreeSelectionListener(new TreeSelectionListener(){

			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node =  (DefaultMutableTreeNode) relationTree.getLastSelectedPathComponent();
				if (node == null)
					return;
				
				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof ConstraintModel)
				{
					ConstraintModel cm = (ConstraintModel) nodeInfo;
					System.out.println(cm);
				}
			}
			
		});
		
		// response to right click and pop up context menu
		relationTree.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				TreePath path = relationTree.getPathForLocation(evt.getX(), evt.getY());
				if (path != null)
				{
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
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) _relationTree.getLastSelectedPathComponent();
		if (e.getActionCommand().equals(ADD_CONSTRAINT))
		{
			DefaultTreeModel treeModel = (DefaultTreeModel) _relationTree.getModel();
			ConstraintModel cm = new ConstraintModel("new constraint");
			DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(cm);
			treeModel.insertNodeInto(newChildNode, selectedNode, selectedNode.getChildCount());
			
			_relationTree.scrollPathToVisible(new TreePath(newChildNode.getPath()));
		}
		else if (e.getActionCommand().equals(GOOD_CONSTRAINT))
		{
			
			
		}
		else if (e.getActionCommand().equals(BAD_CONSTRAINT))
		{
			
		}
		else if (e.getActionCommand().equals(CLEAR_CONSTRAINT))
		{
			
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
