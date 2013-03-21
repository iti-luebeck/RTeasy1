package de.uniluebeck.iti.rteasy.gui;

import javax.swing.*;
import javax.swing.table.*;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SimObjectsBase;
import de.uniluebeck.iti.rteasy.kernel.Bus;
import de.uniluebeck.iti.rteasy.kernel.Memory;
import de.uniluebeck.iti.rteasy.kernel.Register;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;

import java.awt.event.*;
import java.util.*;
import java.awt.Component;

public class SimObjectsTableModel extends AbstractTableModel {
  private int register_offset;
  private int bus_offset;
  private int memory_offset;
  //neu
  private int regar_offset;
  private ArrayList<Register> regList;
  private ArrayList<Bus> busList;
  private ArrayList<Memory> memList;
  private ArrayList<RegisterArray> regarList;
  private SimObjectsBase regBase[], busBase[];
  private int rowsUsed;
  private Component parent;
  private RegisterArray ra1;

  SimObjectsTableModel(LinkedList registerOrder, LinkedList busOrder, LinkedList memoryOrder, 
		  LinkedList regarOrder, Component tp) {
    parent = tp;
    int i = 0;
    int ub;
    register_offset = 1;
    regList = new ArrayList(registerOrder);
    ub = regList.size();
    regBase = new SimObjectsBase[ub];
    for(i=0;i<ub;i++) { 
      regBase[i] = new SimObjectsBase(RTSimGlobals.BASE_BIN);
    }
    bus_offset = register_offset + regList.size() + 2;
    busList = new ArrayList(busOrder);
    ub = busList.size();
    busBase = new SimObjectsBase[ub];
    for(i=0;i<ub;i++) {
      busBase[i] = new SimObjectsBase(RTSimGlobals.BASE_BIN);
    }
    memory_offset = bus_offset + busList.size() + 2; 
    memList = new ArrayList(memoryOrder);
    //carina für Registerarrays:
    regar_offset = memory_offset + memList.size() + 2;
    regarList = new ArrayList<RegisterArray>(regarOrder);
    rowsUsed = regar_offset + regarList.size() + 1;
  }

  public boolean isCellEditable(int row, int col) {
    return (col == 1 || col == 2) &&
      (   (row >= register_offset
           && row < (bus_offset-2) )
       || (row >= bus_offset
           && row < (memory_offset-2) )
       || (row >= memory_offset
    	   && row < (regar_offset-2) 
    	   && col == 1)
       || (row >= regar_offset
           && row < rowsUsed -1 
           && col == 1)  );
  }

  public int getRowCount() { return rowsUsed; }
  public int getColumnCount() { return 3; }

  public String getLongestColumnValue(int col) {
    switch(col) {
      case 0: return /*"BezeichnerWWWW"*/ "RegisterarrayWW";
      case 1:
        int maxWidth = 10;
        int d;
        ListIterator it;
        for(it = regList.listIterator();it.hasNext();) {
          d = ((Register) it.next()).getWidth();
          if(d>maxWidth) maxWidth = d;
        }
        for(it = busList.listIterator();it.hasNext();) {
          d = ((Bus) it.next()).getWidth();
          if(d>maxWidth) maxWidth = d;
        }
        String bk = "00000000000000";
        for(int i=10;i<maxWidth;i++) bk += "0";
        return bk;
      case 2: return "WWWWWWWW";
    }
    return "";
  }

  public boolean registerValueChangedAt(int row, int col) {
    if((col == 1) && (row >= register_offset) && (row < (bus_offset-2))) {
      Register r = (Register) regList.get(row-register_offset);
      return r.valueChanged();
    }
    else return false;
  }

  public Object getValueAt(int row, int col) {
    if(row == (bus_offset-2) || row == (memory_offset-2) || row == (regar_offset-2)) return "";
    if(row == 0) {
      if(col == 0) return IUI.get("LABEL_REGISTERS")+":"; 
      else return "";
    }
    if(row == (bus_offset-1)) {
      if(col == 0) return IUI.get("LABEL_BUSES")+":";
      else return "";
    }
    if(row == (memory_offset-1)) {
      if(col == 0) return IUI.get("LABEL_MEMORIES")+":";
      else return "";
    }
    // neu
    if(row == (regar_offset-1)) {
    	if(col == 0) return IUI.get("LABEL_REGARRAYS")+":";
    	else return "";
    }
    if(row < (bus_offset - 2)) {
      Register r = (Register) regList.get(row-register_offset);
      if(col == 0) return r.getPrettyDecl();
      else if(col == 1)
        return r.getContentStr(regBase[row-register_offset].getValue());
      else if(col == 2) return regBase[row-register_offset];
      else return "";
    }
    if(row < (memory_offset-2)) {
      Bus b = (Bus) busList.get(row-bus_offset);
      if(col == 0) return b.getPrettyDecl();
      else if(col == 1) return b.getContentStr(busBase[row-bus_offset].getValue());  
      else if(col == 2) return busBase[row-bus_offset];
      else return "";
    }
    //neu
    if(row < (regar_offset-2)) {
      Memory m = (Memory) memList.get(row-memory_offset);
      if(col == 0) return m.getPrettyDecl();
      else if(col == 1) return m;
      else return "";
    }
    if(row < (rowsUsed-1)) {
        //Memory m = (Memory) memList.get(row-memory_offset);
        //if(col == 0) return m.getPrettyDecl();
        //else if(col == 1) return m;
        if (col == 0) { return regarList.get(row-regar_offset).getPrettyDecl(); }
        else if (col == 1) return (RegisterArray) regarList.get(row-regar_offset);
    	else return "";
      }
    return "";
  }

  public void setValueAt(Object value, int row, int col) {
    if(col == 1) {
      String s = (String) value;
      if(row >= register_offset && row < (bus_offset-2)) {
        Register r = (Register) regList.get(row-register_offset);
        try {
          boolean inp[] = RTSimGlobals.string2boolArray(s,r.getWidth(),regBase[row-register_offset].getValue());
          r.editContent(inp);
          fireTableDataChanged();
        }
        catch(NumberFormatException e) {
          JOptionPane.showInternalMessageDialog(parent,
            RTSimGlobals.baseInputErrorMsg(regBase[row-register_offset].getValue()),
            IUI.get("TITLE_INPUT_ERROR"),JOptionPane.ERROR_MESSAGE);
        }
      }
      else if(row >= bus_offset && row < (memory_offset-2)) {
        Bus b = (Bus) busList.get(row-bus_offset);
        try {
          boolean inp[] = RTSimGlobals.string2boolArray(s,b.getWidth(),
            busBase[row-bus_offset].getValue());
          b.editContent(inp);
          fireTableDataChanged();
        }
        catch(NumberFormatException e) {
          JOptionPane.showInternalMessageDialog(parent,
            RTSimGlobals.baseInputErrorMsg(busBase[row-bus_offset].getValue()),
            IUI.get("TITLE_INPUT_ERROR"),JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    if(col == 2) {
      if(row >= register_offset && row < (bus_offset-2)) {
        regBase[row-register_offset] = (SimObjectsBase) value;
        fireTableDataChanged();
      }
      else if(row >= bus_offset && row < (memory_offset-2)) {
        busBase[row-bus_offset] = (SimObjectsBase) value;
        fireTableDataChanged();
      }
    }
  }

  public String getColumnName(int col) {
    switch(col) {
      case 0: return IUI.get("LABEL_IDENTIFIER");
      case 1: return IUI.get("LABEL_VALUE");
      case 2: return IUI.get("LABEL_BASE");
    }
    return "";
  }

}    
