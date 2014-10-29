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
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setToolTipText(getText());
		
		// retrieve the corresponding table row to judge whether this is a suspicious token
		ProvenanceTableModel tableModel = (ProvenanceTableModel)table.getModel();
		int modelRow = table.convertRowIndexToModel(row);
		ProvenanceTableRow tableRow = tableModel.getTableRowAt(modelRow);
		
		setBackground(Color.white);
		if (tableRow.getSus() == 1)
		{
			setBackground(Color.RED);
		}
		
		return this;
	}
}
