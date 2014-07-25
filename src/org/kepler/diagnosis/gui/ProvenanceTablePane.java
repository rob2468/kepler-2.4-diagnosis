package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ptolemy.kernel.ComponentRelation;

/** 
 * A panel is displayed in the canvas to display provenance data.
 * A panel corresponds to one relation in one graph model.
 * This mapping relationship is reflected, when displayed in the canvas.
 * */
public class ProvenanceTablePane extends JPanel
{
	public ProvenanceTablePane(Object[][] rowData,Object[] columnNames)
	{
		_tablePane = new JTable(rowData, columnNames);
	}
	
	public static class Factory
	{
		public JComponent createProvenanceTablePane(Object[][] rowData,Object[] columnNames)
		{
			ProvenanceTablePane provTablePane = new ProvenanceTablePane(rowData, columnNames);
			provTablePane.setLayout(new BorderLayout());
			
			provTablePane.getTablePane().setPreferredScrollableViewportSize(new Dimension(200, 100));
			
			JScrollPane scrollPane = new JScrollPane(provTablePane.getTablePane());
			provTablePane.add(scrollPane, BorderLayout.CENTER);
			return provTablePane;
		}
	}
	
	public JTable getTablePane()
	{
		return _tablePane;
	}

	public void setTablePane(JTable _tablePane)
	{
		this._tablePane = _tablePane;
	}
	
	public ComponentRelation getRelation()
	{
		return _relation;
	}

	public void setRelation(ComponentRelation _relation)
	{
		this._relation = _relation;
	}

	private JTable _tablePane;
	
	private ComponentRelation _relation;
}
