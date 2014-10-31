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
	
	public int getMaxSusValue()
	{
		int res = 0;
		for(int i=0; i<data.size(); i++)
		{
			if(data.get(i).getSus() > res)
			{
				res = data.get(i).getSus();
			}
		}
		return res;
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
	
	public void normalizeSusValuesAccordingTo(int maxSus)
	{
		for (int i=0; i<data.size(); i++)
		{
			data.get(i).setVal((float)data.get(i).getSus()/(float)maxSus);
		}
	}

	// data: used to stored custom structured provenance data
	private Vector<ProvenanceTableRow> data = new Vector<ProvenanceTableRow>();
}
