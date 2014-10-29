package org.kepler.diagnosis.gui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ProvenanceTableModel extends DefaultTableModel
{
	
	public void addRow(ProvenanceTableRow rowData)
	{
		data.addElement(rowData);
		
		Vector<Object> tmp = new Vector<Object>();
		tmp.addElement(rowData.getTokenID());
		tmp.addElement(rowData.getTokenValue());
		super.addRow(tmp);
	}
	
	public ProvenanceTableRow getTableRowAt(int row)
	{
		return data.get(row);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	public Vector<ProvenanceTableRow> getData() {
		return data;
	}

	public void setData(Vector<ProvenanceTableRow> data) {
		this.data = data;
	}

	private Vector<ProvenanceTableRow> data = new Vector<ProvenanceTableRow>();
}
