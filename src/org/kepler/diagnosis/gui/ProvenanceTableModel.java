package org.kepler.diagnosis.gui;

import javax.swing.table.DefaultTableModel;

public class ProvenanceTableModel extends DefaultTableModel
{
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
}
