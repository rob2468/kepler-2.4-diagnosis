package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;

public class RelationTreeCellRenderer extends DefaultTreeCellRenderer
{
	public RelationTreeCellRenderer()
	{
		
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
		if (userObject instanceof ConstraintModel)
		{
			setToolTipText(((ConstraintModel)userObject).getConstraints());
		}
		
		return this;
		
	}
}
