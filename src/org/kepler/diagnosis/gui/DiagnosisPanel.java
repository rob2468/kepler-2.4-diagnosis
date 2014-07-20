package org.kepler.diagnosis.gui;

import javax.swing.JPanel;

import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;
import org.kepler.gui.state.StateChangeEvent;
import org.kepler.gui.state.StateChangeListener;
import org.kepler.gui.state.StateChangeMonitor;
import org.kepler.util.WorkflowRun;

import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

public class DiagnosisPanel extends JPanel implements TabPane, StateChangeListener
{

	private TableauFrame _frame;
	private String _title;
	
	public DiagnosisPanel(String title)
	{
		_title = title;
	}
	
	@Override
	public void initializeTab() throws Exception
	{
		StateChangeMonitor.getInstance().addStateChangeListener(WorkflowRun.WORKFLOWRUN_SELECTED, this);
		
	}

	@Override
	public String getTabName()
	{
		return _title;
	}

	@Override
	public TableauFrame getParentFrame()
	{
		return _frame;
	}

	@Override
	public void setParentFrame(TableauFrame parent)
	{
		_frame = parent;
	}
	
	/**
	 *  A factory that creates the library panel for the editors.
	 */
	public static class Factory extends TabPaneFactory
	{

		public Factory(NamedObj container, String name)
				throws IllegalActionException, NameDuplicationException
		{
			super(container, name);
		}
		
		public TabPane createTabPane(TableauFrame parent)
		{
			DiagnosisPanel dtptp = new DiagnosisPanel(this.getName());
			return dtptp;
		}
	}

	@Override
	public void handleStateChange(StateChangeEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("received state change event");
		System.out.print(event);
	}
}
