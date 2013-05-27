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

import java.awt.Component;
import java.awt.image.ColorModel;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SimObjectsBase;
import de.uniluebeck.iti.rteasy.kernel.BitVector;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;

public class RegArrayFrameTableModel extends AbstractTableModel {
	private RegisterArray regarray;
	  public int base = RTSimGlobals.BASE_HEX;
	  public SimObjectsBase[] regBase;
	  private int col1, col2, col3;
	  private int regwidth;
	  private Component parent;
	  private int rowsUsed;

	  RegArrayFrameTableModel(RegisterArray r, Component tp) {
	    regarray = r;
	    parent = tp;
	    col2 = regarray.getWidth();
	    col1 = col2;
	    col3 = col2;
	    regwidth = 1 << col2;
	    rowsUsed = r.getLength() +1 ;
	    regBase = new SimObjectsBase[r.getLength()];
	    for (int i=0; i<regBase.length; i++) {
	    	regBase[i] = new SimObjectsBase(RTSimGlobals.BASE_BIN);
	    }
	  }
	  public int getregwidth() { return regarray.getLength(); }
	  public boolean isCellEditable(int row, int col) {
	    return col == 1 || col ==2;
	  }

	  public int getRowCount() { return regarray.getLength(); }
	  public int getColumnCount() { return 3; }
	  
	  public boolean registerValueChangedAt(int row, int col) {
		  if(col==1) {
		  }
		  return true;
	  }

	  public Object getValueAt(int row, int col) {
	    if(col == 0 && row < rowsUsed) 
	      return Integer.toString(row).toUpperCase();
	    else if(col == 1 && row < rowsUsed)
	        return regarray.getRegister(row).getContentStr(regBase[row].getValue());
	      else if(col == 2 && row<rowsUsed) return regBase[row];
	    else return "";
	  }

	  public void setValueAt(Object value, int row, int col) {
	    if(col == 1 && row < rowsUsed) {
	      try {
	    	  regarray.editContent(new BitVector(value.toString()), row);
	    	  fireTableDataChanged();
	      }
	      catch (NumberFormatException e) {
	        JOptionPane.showInternalMessageDialog(parent,
	          RTSimGlobals.baseInputErrorMsg(base),
	          "Eingabefehler in Speicherzelle",JOptionPane.ERROR_MESSAGE);
	      }
	    } else if (col == 2 && row < rowsUsed) {
	    	regBase[row] = (SimObjectsBase) value;
	        fireTableDataChanged();
	    }
	  }

	  public String getColumnName(int col) {
	    switch(col) {
	      case 0: return IUI.get("COLUMN_POSITION");
	      case 1: return IUI.get("BUTTON_CONTENT");
	      case 2: return IUI.get("LABEL_BASE");
	    }
	    return "";
	  }
} 