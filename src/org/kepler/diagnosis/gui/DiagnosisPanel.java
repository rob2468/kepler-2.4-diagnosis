package org.kepler.diagnosis.gui;

import javax.swing.JPanel;

import org.kepler.gui.KeplerGraphFrame;
import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;
import org.kepler.gui.ViewManager;
import org.kepler.gui.state.StateChangeEvent;
import org.kepler.gui.state.StateChangeListener;
import org.kepler.gui.state.StateChangeMonitor;
import org.kepler.util.WorkflowRun;

import ptolemy.actor.gui.Configuration;
import ptolemy.actor.gui.ModelDirectory;
import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.MoMLParser;

/**
 * This class will be instantiated by view manager. But not add to the application's frame.
 * Used as event listener.
 * */
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
		if (event.getChangedState().equals(WorkflowRun.WORKFLOWRUN_SELECTED))
		{
			NamedObj namedObj = event.getReference();
			
			DiagnosisGraphPanel.Factory factory = new DiagnosisGraphPanel.Factory();
			DiagnosisGraphPanel canvasPanel = factory.createDiagnosisGraphPanel();
			ViewManager viewman = ViewManager.getInstance();
			try
			{
				canvasPanel.setName(namedObj.getDisplayName());
				viewman.addDiagnosisCanvasToLocationNE(canvasPanel, _frame);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
