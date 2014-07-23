package org.kepler.diagnosis.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import diva.gui.GUIUtilities;

public class DiagnosisAction extends AbstractAction
{
	public DiagnosisAction(String description)
	{
		super(description);
		
		GUIUtilities.addIcons(this, new String[][]{
				{"/org/kepler/diagnosis/gui/img/diagnosis.gif", GUIUtilities.LARGE_ICON}, 
				{"/org/kepler/diagnosis/gui/img/diagnosis_o.gif", GUIUtilities.ROLLOVER_ICON}, 
				{"/org/kepler/diagnosis/gui/img/diagnosis_ov.gif", GUIUtilities.ROLLOVER_SELECTED_ICON}, 
				{"/org/kepler/diagnosis/gui/img/diagnosis_on.gif", GUIUtilities.SELECTED_ICON}
		});
		putValue("tooltip", description);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("diagnosis button pressed");		
	}

}
