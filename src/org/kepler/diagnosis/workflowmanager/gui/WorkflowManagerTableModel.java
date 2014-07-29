package org.kepler.diagnosis.workflowmanager.gui;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import org.kepler.diagnosis.DiagnosisManager;
import org.kepler.diagnosis.sql.DiagnosisSQLQuery;
import org.kepler.provenance.QueryException;

import ptolemy.actor.gui.TableauFrame;

public class WorkflowManagerTableModel extends DefaultTableModel
{
	private ArrayList<WorkflowRow> workflows;
	private TableauFrame tableauFrame = null;

	public WorkflowManagerTableModel(TableauFrame frame)
	{
		setTableauFrame(frame);
	}
	
	public void updataData()
	{
		DiagnosisSQLQuery query = (DiagnosisSQLQuery) DiagnosisManager.getInstance().getQueryable();
		try
		{
			workflows = query.getAllWorkflowIDAndName();
		} catch (QueryException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount()
	{
		if (workflows == null)
		{
			return 0;
		}
		return workflows.size();
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		WorkflowRow workflow = workflows.get(rowIndex);
		if (columnIndex == 0)
		{
			return workflow.getId();
		}
		else// if (columnIndex == 1)
		{
			return workflow.getName();
		}
	}
	public TableauFrame getTableauFrame()
	{
		return tableauFrame;
	}
	public void setTableauFrame(TableauFrame tableauFrame)
	{
		this.tableauFrame = tableauFrame;
	}

}
