package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;

import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

public class ConstraintsOfRelationPanel extends JPanel implements TabPane
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
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("nil");
		JTree relationTree = new JTree(top);
		relationTree.setRootVisible(false);
		relationTree.setShowsRootHandles(true);
		relationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		add(relationTree, BorderLayout.CENTER);
		setRelationTree(relationTree);
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

	private JTree _relationTree;
	
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
