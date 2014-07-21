package org.kepler.diagnosis.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * A instance of this class is a panel for the canvas.
 * */
public class DiagnosisGraphPanel extends JPanel
{
	public static class Factory
	{
		public DiagnosisGraphPanel createDiagnosisGraphPanel()
		{
			DiagnosisGraphPanel canvasPanel = new DiagnosisGraphPanel();
			
			canvasPanel.setBorder(null);
			canvasPanel.setLayout(new BorderLayout());
			
			
			return canvasPanel;
		}
		
	}
}
