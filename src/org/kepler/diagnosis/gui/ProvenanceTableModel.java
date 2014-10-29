package org.kepler.diagnosis.gui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ProvenanceTableModel extends DefaultTableModel
{
	
	public void addRow(ProvenanceTableRow rowData)
	{
		Vector<Object> tmp = new Vector<Object>();
		tmp.addElement(rowData.getTokenID());
		tmp.addElement(rowData.getTokenValue());
		super.addRow(tmp);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
}
