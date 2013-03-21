package de.uniluebeck.iti.rteasy.gui;


import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.Memory;

import java.awt.Component;

public class MemoryFrameTableModel extends AbstractTableModel {
  private Memory m;
  public int base = RTSimGlobals.BASE_HEX;
  private int memdw, memaw;
  private int memsize;
  private boolean taddr[];
  private Component parent;

  MemoryFrameTableModel(Memory tm, Component tp) {
    m = tm;
    parent = tp;
    memaw = m.getAddrWidth();
    taddr = new boolean[memaw];
    memdw = m.getDataWidth();
    memsize = 1 << memaw;
  }

  public int getMemSize() { return memsize; }
  public boolean isCellEditable(int row, int col) {
    return col == 1;
  }

  public int getRowCount() { return memsize; }
  public int getColumnCount() { return 2; }

  public Object getValueAt(int row, int col) {
    if(col == 0) 
      return Integer.toString(row,16).toUpperCase();
    else if(col == 1) {
      RTSimGlobals.intInBoolArray(taddr,row);
      return RTSimGlobals.boolArray2String(m.getDataAt(taddr),base);
    }
    else return "";
  }

  public void setValueAt(Object value, int row, int col) {
    if(col == 1 && row < memsize) {
      RTSimGlobals.intInBoolArray(taddr,row);
      try {
        m.setDataAt(taddr,RTSimGlobals.string2boolArray(value.toString(),
           memdw,base));
      }
      catch (NumberFormatException e) {
        JOptionPane.showInternalMessageDialog(parent,
          RTSimGlobals.baseInputErrorMsg(base),
          "Eingabefehler in Speicherzelle",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public String getColumnName(int col) {
    switch(col) {
      case 0: return IUI.get("COLUMN_ADDRESS");
      case 1: return IUI.get("BUTTON_CONTENT");
    }
    return "";
  }
} 
