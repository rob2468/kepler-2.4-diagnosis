package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;

public class RelationTreeCellRenderer extends DefaultTreeCellRenderer
{
	public RelationTreeCellRenderer()
	{
		
	}
	
	public Color getBackgroundNonSelectionColor()
	{
        return (null);
    }
	
	public Color getBackgroundSelectionColor()
	{
        return Color.BLUE;
    }

	public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object userObject = node.getUserObject();
		setToolTipText(null);
		if (userObject instanceof ConstraintModel)
		{
			setToolTipText(((ConstraintModel)userObject).getConstraints());
		}
		
		setBackground(null);
		if (_goodNodes.contains(node))
		{
			setBackground(Color.GREEN);
		}
		else if (_badNodes.contains(node))
		{
			setBackground(Color.RED);
		}
		
		return this;
		
	}
	
	public boolean addGoodNode(DefaultMutableTreeNode node)
	{
		if (_goodNodes.contains(node))
		{
			return false;
		}
		else
		{
			removeBadNode(node);
			_goodNodes.add(node);
			return true;
		}
	}
	
	public boolean removeGoodNode(DefaultMutableTreeNode node)
	{
		if (_goodNodes.contains(node))
		{
			_goodNodes.remove(node);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean addBadNode(DefaultMutableTreeNode node)
	{
		if (_badNodes.contains(node))
		{
			return false;
		}
		else
		{
			removeGoodNode(node);
			_badNodes.add(node);
			return true;
		}
	}
	
	public boolean removeBadNode(DefaultMutableTreeNode node)
	{
		if (_badNodes.contains(node))
		{
			_badNodes.remove(node);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void clearNode(DefaultMutableTreeNode node)
	{
		_goodNodes.remove(node);
		_badNodes.remove(node);
	}
	
	private LinkedList<DefaultMutableTreeNode> _goodNodes = new LinkedList<DefaultMutableTreeNode>();
	private LinkedList<DefaultMutableTreeNode> _badNodes = new LinkedList<DefaultMutableTreeNode>();
}
