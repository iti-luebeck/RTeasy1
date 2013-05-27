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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SimObjectsBase;
import de.uniluebeck.iti.rteasy.frontend.MemoryParser;
import de.uniluebeck.iti.rteasy.kernel.Memory;
import de.uniluebeck.iti.rteasy.kernel.MemoryEntry;
import de.uniluebeck.iti.rteasy.kernel.Register;

public class MemoryFrame extends JInternalFrame {
  public MemoryFrameTableModel model;
  public JTable table;
  private JScrollPane scrollPane;
  private Memory memory;
  private String lf;
  private File workDir = null;

  public void simUpdate() {
    model.fireTableDataChanged();
  }

	class PointerCellRenderer extends DefaultTableCellRenderer {

		public PointerCellRenderer(){
			super();
			setBackground(Color.WHITE);
			setForeground(Color.black);
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
				setForeground(Color.black);
			}
			return this;
		}
		
	}
  
	public int getPointer(){
		Register r = memory.getAddr();
		String s = RTSimGlobals.boolArray2String(r.getBoolArrayOld(),
				RTSimGlobals.BASE_DEC);
		return Integer.parseInt(s);
	}
	
  private JFileChooser getFileChooser() {
    JFileChooser chooser;
    try {
      if(workDir == null) workDir = new File(System.getProperty("user.dir"));
      chooser = new JFileChooser(workDir);
    }
    catch(Throwable t) {
      chooser = new JFileChooser();
    }
    return chooser;
  }

  private void loadFile() {
    JFileChooser chooser = getFileChooser();
    //chooser.setFileFilter(fileFilter);
    int result = chooser.showOpenDialog(this);
    if(result == JFileChooser.CANCEL_OPTION) return;
    try {
      File file = chooser.getSelectedFile();
      workDir = file.getParentFile();
      FileReader fr = new FileReader(file);
      MemoryParser parser = new MemoryParser(fr);
      parser.setAddrWidth(memory.getAddrWidth());
      parser.setDataWidth(memory.getDataWidth());
      MemoryEntry me = parser.parse();
      fr.close();
      if(parser.hasSyntaxError()) {
        JOptionPane.showInternalMessageDialog(this,parser.getSyntaxErrorMessage(),"Syntax-Fehler bei Laden des Speicherinhalts",JOptionPane.ERROR_MESSAGE);
	return;
      }
      memory.clear();
      ListIterator it;
      boolean addr[];
      boolean data[];
      do {
        it = me.entries.listIterator(0);
	addr = me.addr;
        while(it.hasNext()) {
	  data = (boolean[]) it.next(); 
	  memory.setDataAt(addr,data);
	  RTSimGlobals.boolArrayInc(addr);
	}
	me = me.child;
      } while(me != null);
      simUpdate();
    }
    catch (Throwable t) {
      JOptionPane.showInternalMessageDialog(this,t.getLocalizedMessage(),"Eingabe-Fehler bei Laden des Speicherinhalts",JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  private void saveFileAs() {
    JFileChooser chooser = getFileChooser();
    int result = chooser.showSaveDialog(this);
    if(result == JFileChooser.CANCEL_OPTION) return;
    try {
      File file = chooser.getSelectedFile();
      workDir = file.getParentFile();
      FileWriter fw = new FileWriter(file);
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,IUI.getLocale());
      fw.write("# "+IUI.get("TEXT_GENERATED_BY_RTEASY")+" "+df.format(new Date())+lf);
      fw.write("# "+IUI.get("TEXT_MEMORY_CONFIG").replaceAll("%%MEMORY",memory.getPrettyDecl())+lf);
      fw.write("# "+IUI.get("TEXT_ADDRWIDTH")+": "+Integer.toString(memory.getAddrWidth())+", "+IUI.get("TEXT_DATAWIDTH")+": "+Integer.toString(memory.getDataWidth())+lf+lf);
      ArrayList cells = memory.getUsedCellsSorted();
      boolean old_addr[] = null;
      boolean addr[];
      int aw = memory.getAddrWidth();
      int dw = memory.getDataWidth();
      ListIterator it = cells.listIterator(0);
      int state = 0;
      while(it.hasNext()) {
        addr = (boolean[]) it.next();
        switch(state) {
          case 1:
            RTSimGlobals.boolArrayInc(old_addr);
            if(RTSimGlobals.boolArrayCompare(addr,old_addr) == 0) {
              fw.write(","+lf+"  "+RTSimGlobals.base2String(model.base)+RTSimGlobals.boolArray2String(memory.getDataAt(addr),model.base));
              break;
            }
            fw.write(";"+lf);
          case 0:
            fw.write("$"+RTSimGlobals.boolArray2String(addr,RTSimGlobals.BASE_HEX)+":"+lf+"  "+RTSimGlobals.base2String(model.base)
              +RTSimGlobals.boolArray2String(memory.getDataAt(addr),model.base));
            state = 1;
            break;
        }
        old_addr = addr;
      }
      if(old_addr != null) fw.write(";"+lf);
      else fw.write("$0: $0;"+lf);
      fw.close();
    }
    catch(Throwable t) {
      JOptionPane.showInternalMessageDialog(this,IUI.get("DIALOG_ERROR_MEMORY_SAVE")+": "+t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
      t.printStackTrace(System.err); 
    }
  }

  MemoryFrame(Memory m) {
    super(IUI.get("TITLE_MEMORY")+" "+m.getIdStr(),true,true,true,true);
    memory = m;
    table = new JTable();
    model = new MemoryFrameTableModel(m,this);
    table.setModel(model);
    PointerCellRenderer renderer = new PointerCellRenderer();
    table.getColumnModel().getColumn(0).setCellRenderer(renderer);
    renderer.setHorizontalAlignment(JTextField.RIGHT);
    table.getColumnModel().getColumn(1).setCellRenderer(renderer);
    final JTextField textField = new JTextField();
    textField.setHorizontalAlignment(JTextField.RIGHT);
    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textField));
    Dimension d = table.getPreferredSize();
    d.height = 250;
    table.setPreferredScrollableViewportSize(d);
    table.setRowSelectionAllowed(false);
    table.setColumnSelectionAllowed(false);
    model.fireTableDataChanged();
    scrollPane = new JScrollPane(table);
    final JComboBox baseBox = new JComboBox();
    SimObjectsBase hexBase = new SimObjectsBase(RTSimGlobals.BASE_HEX);
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_BIN));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC));
    baseBox.addItem(hexBase);
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC2));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_HEX2));
    baseBox.setSelectedItem(hexBase);
    baseBox.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        model.base = ((SimObjectsBase) baseBox.getSelectedItem()).getValue();
        model.fireTableDataChanged();
      }
    });
    JButton gotoButton = new JButton(IUI.get("BUTTON_GOTO_ADDRESS"));
    gotoButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        String bk = JOptionPane.showInternalInputDialog(
          MemoryFrame.this,IUI.get("DIALOG_GOTO_ADDRESS_INPUT"),IUI.get("BUTTON_GOTO_ADDRESS"),JOptionPane.QUESTION_MESSAGE);
        try {
          int row = Integer.parseInt(bk,16);
          if(row < 0 || row >= model.getMemSize()) {
            JOptionPane.showInternalMessageDialog(MemoryFrame.this,
            IUI.get("DIALOG_GOTO_ADDRESS_OUT_OF_RANGE").replaceAll(
            "%%RANGE","0 .. "+Integer.toString(model.getMemSize()-1,16).toUpperCase()),
            IUI.get("TITLE_ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
          }
          int pixels = 0;
          if(row > 0) pixels += table.getRowHeight()*(row-1);
          //if(row > 1) pixels += table.getRowMargin()*(row-2);
          scrollPane.getVerticalScrollBar().setValue(pixels);
        }
        catch(NumberFormatException e) {
          JOptionPane.showInternalMessageDialog(MemoryFrame.this,
          IUI.get("DIALOG_HEX_INPUT_SYNTAX_ERROR").replaceAll(
          "%%INPUT","address"),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    JButton resetButton = new JButton(IUI.get("BUTTON_RESET"));
    resetButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        MemoryFrame.this.memory.clear();
	MemoryFrame.this.model.fireTableDataChanged();
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(new JLabel(IUI.get("LABEL_BASE_CONTENTS")+": "));
    buttonPanel.add(baseBox);
    buttonPanel.add(gotoButton);
    buttonPanel.add(resetButton);

    JButton loadButton = new JButton(IUI.get("BUTTON_LOAD"));
    loadButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        MemoryFrame.this.loadFile();
      }
    });
    JButton saveButton = new JButton(IUI.get("BUTTON_SAVE"));
    saveButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        MemoryFrame.this.saveFileAs();
      }
    });
    JPanel loadSavePanel = new JPanel();
    loadSavePanel.add(loadButton);
    loadSavePanel.add(saveButton);
    getContentPane().add(buttonPanel,BorderLayout.NORTH);
    getContentPane().add(scrollPane,BorderLayout.CENTER);
    getContentPane().add(loadSavePanel,BorderLayout.SOUTH);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    try {
      lf = System.getProperty("line.separator");
    }
    catch (Throwable t) {
      lf = "\n";
    }
  }

  public void updateCaptions() {
    setTitle(IUI.get("TITLE_MEMORY"));
    pack();
    if(isShowing()) show();
  }
}
