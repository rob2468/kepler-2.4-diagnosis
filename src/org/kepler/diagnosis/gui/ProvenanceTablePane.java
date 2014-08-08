package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import ptolemy.kernel.ComponentRelation;

/** 
 * A panel is displayed in the canvas to display provenance data.
 * A panel corresponds to one relation in one graph model.
 * This mapping relationship is reflected, when displayed in the canvas.
 * */
public class ProvenanceTablePane extends JPanel
{
	public ProvenanceTablePane()
	{
		_titleLabel = new JLabel();
		
		_tablePane = new JTable();
		_tablePane.setSelectionBackground(_tablePane.getBackground());
		_tablePane.setSelectionForeground(_tablePane.getForeground());
	}
	
	public ProvenanceTablePane(Object[][] rowData,Object[] columnNames)
	{
		_tablePane = new JTable(rowData, columnNames);
	}
	
	public void setTablePaneModel(TableModel dataModel)
	{
		_tablePane.setModel(dataModel);
	}
	
	public static class Factory
	{
		public JComponent createProvenanceTablePane()
		{
			ProvenanceTablePane provTablePane = new ProvenanceTablePane();
			provTablePane.setLayout(new BorderLayout());
			
			JScrollPane scrollPane = new JScrollPane(provTablePane.getTablePane());
			provTablePane.add(scrollPane, BorderLayout.CENTER);
			
			provTablePane.add(provTablePane.getTitleLabel(), BorderLayout.NORTH);
			
			return provTablePane;
		}
	}// Factory
	
	public JLabel getTitleLabel()
	{
		return _titleLabel;
	}

	public void setTitleLabel(JLabel _titleLabel)
	{
		this._titleLabel = _titleLabel;
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
	
	private JLabel _titleLabel;
	
	private JTable _tablePane;
	
	private ComponentRelation _relation;
}
