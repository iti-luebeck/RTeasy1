/*
 * Copyright (c) 2003-2013, University of Luebeck, Institute of Computer Engineering
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Luebeck, the Institute of Computer
 *       Engineering nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE UNIVERSITY OF LUEBECK OR THE INSTITUTE OF COMPUTER
 * ENGINEERING BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package de.uniluebeck.iti.rteasy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SimObjectsBase;
import de.uniluebeck.iti.rteasy.kernel.BitVector;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;

public class RegArrayFrame extends JInternalFrame {
	
	public JTable table;
	public JScrollPane scrollpane;
	public JButton okbutton;
	public JTextField[] tf;
	public RegArrayFrameTableModel model;
	public RegisterArray regarray;
	public JLabel regarrcontent;
	
	public void simUpdate() {
		setNewHead("" +regarray.getRegisterNumber());
	    model.fireTableDataChanged();
	}
	
	class PointerCellRenderer extends DefaultTableCellRenderer {

		public PointerCellRenderer(){
			super();
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int row, int col) {
			setBackground(null);
			setHorizontalAlignment(0);
			super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
			if (col == 0) {
				setHorizontalAlignment(JTextField.CENTER);
			}
			else {
				setHorizontalAlignment(JTextField.RIGHT);
			}
			if (row==getPointer()) {
				setBackground(Color.GREEN);
				setForeground(Color.BLACK);
			} else {
				setBackground(Color.white);
				setForeground(Color.BLACK);
			}
			return this;
		}
		
	}
	
	public int getPointer() {
		
		return regarray.getRegisterNumber();
	}
	
	public void setData() {
		BitVector bv;
	}
	
	public void setNewHead(String ref) {
		regarrcontent.setText(ref);
	}
	
	RegArrayFrame(RegisterArray r) {
		super(("Registerarray "+r.getPrettyDecl()), true, true, true, true);
		regarray = r;
		table = new JTable();
		model = new RegArrayFrameTableModel(r, this);
		table.setModel(model);
		tf = new JTextField[r.getWidth()];
		PointerCellRenderer pcr = new PointerCellRenderer();
	    table.getColumnModel().getColumn(0).setCellRenderer(pcr);
	    table.getColumnModel().getColumn(1).setCellRenderer(pcr);
	    table.getColumnModel().getColumn(2).setCellRenderer(pcr);
		final JTextField textField = new JTextField();
	    textField.setHorizontalAlignment(JTextField.RIGHT);
	    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textField));
	    Dimension d = table.getPreferredSize();
	    d.height = 250;
	    table.setPreferredScrollableViewportSize(d);
	    model.fireTableDataChanged();
	    scrollpane = new JScrollPane(table);
	    JComboBox baseBox = new JComboBox();
	    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_BIN));
	    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC));
	    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_HEX));
	    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC2));
	    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_HEX2));
	    table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(baseBox));
	    JLabel register = new JLabel("");//(" " + regarray.getReference().getPrettyDecl() + " = ");
	    regarrcontent = new JLabel("0");
	    JLabel field = new JLabel();
	    field.setText(IUI.get("LABEL_REGARRAYFRAME") + " :  ");
	    JPanel head = new JPanel();
	    head.add(field);
	    //head.add(register);
	    head.add(regarrcontent);
	    okbutton = new JButton(IUI.get("BUTTON_APPLY"));
	    okbutton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		setData();
	    		RegArrayFrame.this.model.fireTableDataChanged();
	    		simUpdate();
	    	}
	    });
	    table.setCellSelectionEnabled(true);
	    JPanel okpanel = new JPanel();
	    okpanel.add(okbutton);
	    getContentPane().add(head, BorderLayout.NORTH);
	    getContentPane().add(scrollpane,BorderLayout.CENTER);
	    getContentPane().add(okpanel, BorderLayout.SOUTH);
	    pack();
	}
	
	public void check() {
		for (int i=0; i<regarray.getLength(); i++) {
		}
	}

}
