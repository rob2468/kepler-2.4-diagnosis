package org.kepler.diagnosis.workflowmanager;

import java.awt.Font;

public final class WMDefaults
{
	static private WMDefaults _instance = null;
	
	static final public String ID = "id";
	static final public String NAME = "name";
	
	static final public int WM_PANE_WIDTH = 200;
	static final public int WM_PANE_HEIGHT = 180;
	
	static final public int WM_FONT_SIZE = 11;
	static final public Font WM_FONT = new Font("LucidaGrande", Font.PLAIN,
			WM_FONT_SIZE);
	static final public int WM_ROW_HEIGHT = 14;

	// preferred column widths
	static final public int ID_COLUMN_WIDTH = 35;
	static final public int NAME_COLUMN_WIDTH = 165;
	
	private WMDefaults()
	{
		
	}
	
	public synchronized static WMDefaults getInstance()
	{
		if (_instance == null) {
			_instance = new WMDefaults();
		}
		return _instance;
	}
}
