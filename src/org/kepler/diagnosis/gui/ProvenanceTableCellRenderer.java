package org.kepler.diagnosis.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ProvenanceTableCellRenderer extends DefaultTableCellRenderer
{
	/** table row matching _tokenIDs needs specific background color */
	private LinkedList<Integer> _rows;
	
	public ProvenanceTableCellRenderer()
	{
		setOpaque(true);
	}
	
	public ProvenanceTableCellRenderer(LinkedList<Integer> rows)
	{
		_rows = rows;
		setOpaque(true);
	}
	
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
	{
		JLabel jl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		jl.setBackground(Color.white);
		if (_rows != null)
		{
			if (_rows.contains(row))
			{
				jl.setBackground(Color.cyan);
			}
		}
		
		jl.setToolTipText(jl.getText());
		
		return jl;
	}
}
