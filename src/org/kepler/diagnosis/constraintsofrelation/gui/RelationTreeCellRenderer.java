package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode.NodeStates;

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
		
		ConstraintMutableTreeNode node = (ConstraintMutableTreeNode) value;
		Object userObject = node.getUserObject();
		setToolTipText(null);
		if (userObject instanceof ConstraintModel)
		{
			setToolTipText(((ConstraintModel)userObject).getConstraints());
		}
		
		setBackground(null);
		if (node.getState() == NodeStates.GOOD)
		{
			setBackground(Color.GREEN);
		}
		else if (node.getState() == NodeStates.BAD)
		{
			setBackground(Color.RED);
		}
		
		return this;
	}
}
