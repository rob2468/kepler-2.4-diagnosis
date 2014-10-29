package org.kepler.diagnosis.constraintsofrelation.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.kepler.diagnosis.constraintsofrelation.ConstraintModel;
import org.kepler.diagnosis.constraintsofrelation.ConstraintMutableTreeNode;
import org.kepler.diagnosis.constraintsofrelation.ConstraintTreeModel;

import com.google.common.collect.Constraints;

public class ConstraintDialog extends JDialog
{
	public ConstraintDialog(JFrame owner, String title, ConstraintsOfRelationPanel consOfRelationPanel, ConstraintMutableTreeNode selectedNode)
	{
		super(owner, title, true);
		
		_consOfRelationPanel = consOfRelationPanel;
		_selectedNode = selectedNode;
		
		getContentPane().setLayout(null);
		
		JLabel displayNameLabel = new JLabel("Constraint Name:");
		displayNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		displayNameLabel.setBounds(83, 44, 130, 20);
		displayNameLabel.setBackground(Color.yellow);
		
		_displayNameTextField = new JTextField();
		_displayNameTextField.setEditable(true);
		_displayNameTextField.setBounds(225, 40, 248, 28);
		
		JLabel constraintLabel = new JLabel("Constraint:");
		constraintLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		constraintLabel.setBounds(83, 102, 130, 20);
		
		_constraintTextField = new JTextField();
		_constraintTextField.setEditable(true);
		_constraintTextField.setBounds(225, 98, 400, 28);
		
		_commitButton = new JButton("Commit");
		_commitButton.setBounds(96, 166, 117, 29);
		_commitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ConstraintMutableTreeNode selectedNode = ConstraintDialog.this.getSelectedNode();
				ConstraintsOfRelationPanel cons = ConstraintDialog.this.getConsOfRelationPanel();
				JTree relationTree = cons.getRelationTree();
				ConstraintTreeModel treeModel = (ConstraintTreeModel) relationTree.getModel();
				
				String nameStr = _displayNameTextField.getText();
				nameStr = nameStr.trim();
				String constraintStr = _constraintTextField.getText();
				constraintStr = constraintStr.trim();
				
				if (nameStr!=null && !nameStr.equals("") && constraintStr!=null && !constraintStr.equals(""))
				{
					ConstraintModel cm = new ConstraintModel(nameStr, constraintStr);
					
					ConstraintMutableTreeNode newChildNode = new ConstraintMutableTreeNode(cm);
					treeModel.insertNodeInto(newChildNode, selectedNode, selectedNode.getChildCount());
					
					relationTree.scrollPathToVisible(new TreePath(newChildNode.getPath()));
				}
				ConstraintDialog.this.dispose();
			}
		});
		
		_cancelButton = new JButton("Cancel");
		_cancelButton.setBounds(417, 166, 117, 29);
		_cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ConstraintDialog.this.dispose();
			}
		});
		
		getContentPane().add(displayNameLabel);
		getContentPane().add(_displayNameTextField);
		getContentPane().add(constraintLabel);
		getContentPane().add(_constraintTextField);
		getContentPane().add(_commitButton);
		getContentPane().add(_cancelButton);
		
		setBounds(120, 120, 700, 250);
				
		if (owner != null)
		{
            setLocationRelativeTo(owner);
        }
		else
		{
			Toolkit tk = Toolkit.getDefaultToolkit();
            setLocation((tk.getScreenSize().width - getSize().width) / 2,
                    (tk.getScreenSize().height - getSize().height) / 2);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);		
	}
	
	public ConstraintsOfRelationPanel getConsOfRelationPanel()
	{
		return _consOfRelationPanel;
	}
	
	public void setConsOfRelationPanel(ConstraintsOfRelationPanel _consOfRelationPanel)
	{
		this._consOfRelationPanel = _consOfRelationPanel;
	}
	
	public ConstraintMutableTreeNode getSelectedNode()
	{
		return _selectedNode;
	}
	
	public void setSelectedNode(ConstraintMutableTreeNode _selectedNode)
	{
		this._selectedNode = _selectedNode;
	}
	
	private JTextField _displayNameTextField;
	private JTextField _constraintTextField;
	
	private JButton _commitButton;
	private JButton _cancelButton;
	
	private ConstraintsOfRelationPanel _consOfRelationPanel;
	private ConstraintMutableTreeNode _selectedNode;
	
}
