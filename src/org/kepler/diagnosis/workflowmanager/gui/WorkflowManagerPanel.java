package org.kepler.diagnosis.workflowmanager.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.kepler.diagnosis.workflowmanager.WMDefaults;
import org.kepler.gui.TabPane;
import org.kepler.gui.TabPaneFactory;

import ptolemy.actor.gui.TableauFrame;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

public class WorkflowManagerPanel extends JPanel implements TabPane, ActionListener
{
	private TableauFrame _frame;
	private String _title;
	
	private WorkflowManagerTableModel wmTableModel = null;
	private JTable wmjTable = null;
	private JPopupMenu popupMenu = new JPopupMenu();
	private static final String OPEN_WORKFLOW = "Open";
	private static final String REFRESH = "Refresh";
	
	private JButton refreshButton = new JButton();
	
	private JMenuItem openMenuItem = new JMenuItem(OPEN_WORKFLOW);
	
	private static int doubleClick = 2;
	
	private boolean lastMouseEventWasPopupTrigger;
	
	public WorkflowManagerPanel(String title)
	{
		_title = title;
	}
	public void initializeTab() throws Exception
	{
		this.removeAll();
		this.repaint();
		this.revalidate();
		// Add components to the JPanel here.
		this.setLayout(new BorderLayout());
		
		wmTableModel = new WorkflowManagerTableModel(_frame);
		Vector<String> columnIdentifiers = new Vector<String>();
		columnIdentifiers.addElement(WMDefaults.ID);
		columnIdentifiers.addElement(WMDefaults.NAME);
		wmTableModel.setColumnIdentifiers(columnIdentifiers);
		wmTableModel.updataData();
		
		setupContextMenu();
		
		wmjTable = new JTable(wmTableModel);
		
		wmjTable.getColumn(WMDefaults.ID).setPreferredWidth(WMDefaults.ID_COLUMN_WIDTH);
		wmjTable.getColumn(WMDefaults.NAME).setPreferredWidth(WMDefaults.NAME_COLUMN_WIDTH);
		
		wmjTable.getColumn(WMDefaults.ID).setCellRenderer(new WorkflowManagerTableCellRenderer());
		wmjTable.getColumn(WMDefaults.NAME).setCellRenderer(new WorkflowManagerTableCellRenderer());		
		
		wmjTable.addMouseListener(new java.awt.event.MouseAdapter() {
			// os X
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if ((evt.getClickCount() == doubleClick)
						&& !evt.isPopupTrigger()
						&& !lastMouseEventWasPopupTrigger) {
					//openMenuItem.doClick();
				} else {
//					setTagContextToCurrentlySelectedRows();
					jTableShowContextMenu(evt);
				}
				lastMouseEventWasPopupTrigger = evt.isPopupTrigger();
			}

			// Windows
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if ((evt.getClickCount() == doubleClick)
						&& !evt.isPopupTrigger()
						&& !lastMouseEventWasPopupTrigger) {
				} else {
					jTableShowContextMenu(evt);
				}
				lastMouseEventWasPopupTrigger = evt.isPopupTrigger();
			}
		});
		
		JScrollPane _workflowManagerPane = new JScrollPane(wmjTable);
		
		refreshButton.setText("Refresh");
		refreshButton.setActionCommand(REFRESH);
		refreshButton.addActionListener(this);
		refreshButton.setSize(180, 25);
		refreshButton.setEnabled(true);
		
		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		JPanel upperPanel = new JPanel(flowLayout);
		
		upperPanel.add(refreshButton);
		
		_workflowManagerPane.setMinimumSize(new Dimension(WMDefaults.WM_PANE_WIDTH, WMDefaults.WM_PANE_HEIGHT));
		_workflowManagerPane.setPreferredSize(new Dimension(WMDefaults.WM_PANE_WIDTH, WMDefaults.WM_PANE_HEIGHT));		
		this.add(upperPanel, BorderLayout.NORTH);
		this.add(_workflowManagerPane);
		this.revalidate();		
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
	
	public static class Factory extends TabPaneFactory
	{

		public Factory(NamedObj container, String name)
				throws IllegalActionException, NameDuplicationException
		{
			super(container, name);
		}
		
		public TabPane createTabPane(TableauFrame parent)
		{
			WorkflowManagerPanel wmp = new WorkflowManagerPanel(this.getName());
			return wmp;
		}
	}
	
	private void setupContextMenu()
	{
		openMenuItem.removeActionListener(this);
		openMenuItem.addActionListener(this);
		openMenuItem.setActionCommand(OPEN_WORKFLOW);
		openMenuItem.setEnabled(true);
		
		popupMenu.add(openMenuItem);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		
		
	}
	
	private void jTableShowContextMenu(java.awt.event.MouseEvent mouseEvent)
	{
		if (mouseEvent.isPopupTrigger() || mouseEvent.isControlDown())
		{
			int row = wmjTable.rowAtPoint(mouseEvent.getPoint());
			popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}
	}
	public class WorkflowManagerTableCellRenderer extends DefaultTableCellRenderer
	{
		public WorkflowManagerTableCellRenderer()
		{
			setOpaque(true);
		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			JLabel jl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			jl.setToolTipText(jl.getText());
			
			return jl;
		}
	}
}
