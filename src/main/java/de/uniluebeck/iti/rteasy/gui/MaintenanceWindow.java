package de.uniluebeck.iti.rteasy.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MaintenanceWindow extends JInternalFrame{

	public JTextArea text;
	private JButton hide;
	private JPanel bpanel;
	private JDesktopPane desk;
	
	public MaintenanceWindow(JDesktopPane desktop) {
		super("Internal Errors",true,true,false,true);
		desk = desktop;
		text = new JTextArea();
		text.setEditable(false);
		getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
		hide = new JButton("Hide");
		hide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		bpanel = new JPanel();
		bpanel.add(hide);
		getContentPane().add(bpanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		pack();
		setSize(300,350);
		setVisible(false);
	}
	
	public void shows(){
		desk.add(this);
		setLocation(200,200);
		setVisible(true);
		moveToFront();
	}
	
	public void set(String s) {
		text.append(s+"\n");
	}
}
