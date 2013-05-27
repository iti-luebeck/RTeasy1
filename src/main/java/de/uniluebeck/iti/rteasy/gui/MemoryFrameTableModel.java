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
