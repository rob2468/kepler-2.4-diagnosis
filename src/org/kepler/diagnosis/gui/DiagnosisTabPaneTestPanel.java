package org.kepler.diagnosis.gui;

import javax.swing.JPanel;

import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;

import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

public class DiagnosisTabPaneTestPanel extends JPanel implements TabPane
{

	private TableauFrame _frame;
	private String _title;
	
	public DiagnosisTabPaneTestPanel(String title)
	{
		_title = title;
	}
	
	@Override
	public void initializeTab() throws Exception
	{
		// TODO Auto-generated method stub
		
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
			DiagnosisTabPaneTestPanel dtptp = new DiagnosisTabPaneTestPanel(this.getName());
			return dtptp;
		}
	}
}
