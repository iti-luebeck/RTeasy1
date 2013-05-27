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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.event.*;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.kernel.RTProgram;

public class BreakpointFrame extends JInternalFrame {
  class TableModel extends AbstractTableModel {
    public Object breakpointsSorted[];

    TableModel() {
      readBreakpoints(); 
    }

    private void readBreakpoints() {
      if(BreakpointFrame.this.breakpoints != null) {
        breakpointsSorted = BreakpointFrame.this.breakpoints.toArray();
        Arrays.sort(breakpointsSorted); 
      }
      else breakpointsSorted = new Object[0];
    }
    
    public void fireTableDataChanged() {
      readBreakpoints();
      super.fireTableDataChanged();
    }

    public boolean isCellEditable(int row, int col) {
      //return col == 0 && row < breakpointsSorted.length && row >= 0;
      return false;
    }

    public Object getValueAt(int row, int col) {
      if(col == 0 && row < breakpointsSorted.length && row >= 0) return 
       IUI.get("CYCLE")+" "+breakpointsSorted[row].toString();
      else return "";
    }

    public void setValueAt(Object value, int row, int col) {
    }    

    public String getColumnName(int col) {
      if(col == 0) return IUI.get("LABEL_BREAKPOINTS")+":";
      else return "";
    }

    public int getRowCount() { return breakpointsSorted.length; }
    public int getColumnCount() { return 1; }
  }
 
  class BreakpointField extends JTextField {
    public int breakpoint;
    BreakpointField() {
      super();
      setEditable(false);
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          BreakpointFrame.this.selectBreakpoint(breakpoint);
        }
      });
    }
  }
    
  class CellRenderer extends DefaultTableCellRenderer {
    CellRenderer() {
      super();
      addMouseListener(new MouseListener() {
	  public void mouseClicked(MouseEvent me) {
	    //System.err.println("mouseClicked");
	  }
	  public void mouseEntered(MouseEvent me) {}
	  public void mouseExited(MouseEvent me) {}
	  public void mousePressed(MouseEvent me) {}
	  public void mouseReleased(MouseEvent me) {}
	});
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int col) {
      if(isSelected && hasFocus)
        if(row < BreakpointFrame.this.model.breakpointsSorted.length
           && row >= 0) {
	          BreakpointFrame.this.breakpointSelectAndFocus(((Integer)
	    BreakpointFrame.this.model.breakpointsSorted[row]).intValue());
        }
      return super.getTableCellRendererComponent(table,value,isSelected,
        hasFocus,row,col);
    }
  }

  class CellEditor extends DefaultCellEditor {
    final BreakpointField textField = new BreakpointField();

    CellEditor() {
      super(new JTextField());
      setClickCountToStart(1);
      textField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          fireEditingStopped();
        }
      });
    }

    protected void fireEditingStopped() { super.fireEditingStopped(); }

    public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int col) {
      textField.breakpoint = ((Integer) value).intValue();
      return textField;
    }
  } 

  HashSet breakpoints = null;
  JTextArea editorArea = null;
  Cursor editorCursor;
  TableModel model;
  JButton addButton, deleteButton, deselButton;
  Object highlight = null;
  public boolean modeSelect = false;
  int selectedBreakpoint;
  boolean selected = false;
  RTProgram rtprog;
  JDesktopPane desktop;
  boolean onDesktop = false;

  BreakpointFrame(JDesktopPane toDesktop) {
    super(IUI.get("TITLE_BREAKPOINTS"),true,true,false,true);
    desktop = toDesktop;
    breakpoints = new HashSet();
    addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent ife) {
        BreakpointFrame.this.revokeInternal();
      }
    });
    model = new TableModel();
    JTable table = new JTable(model);
    final CellRenderer breakRenderer = new CellRenderer(); 
    table.getColumnModel().getColumn(0).setCellRenderer(breakRenderer);
    table.setCellSelectionEnabled(true);
    addButton = new JButton(IUI.get("BUTTON_ADD_BREAKPOINT"));
    addButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        BreakpointFrame.this.modeAddBreakpoint();
      }
    });
    deleteButton = new JButton(IUI.get("BUTTON_DELETE_BREAKPOINT"));
    deleteButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        BreakpointFrame.this.deleteBreakpoint();
      }
    });
    deleteButton.setEnabled(false);
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(addButton);
    panel.add(deleteButton);
    getContentPane().add(panel,BorderLayout.NORTH);
    getContentPane().add(new JScrollPane(table),BorderLayout.CENTER);
    deselButton = new JButton(IUI.get("BUTTON_RELEASE_BREAKPOINT_MARKING"));
    deselButton.addActionListener( new ActionListener() {
	public void actionPerformed(ActionEvent ae) {
	  deselectBreakpoint();
	}
      });
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.add(deselButton);
    getContentPane().add(panel,BorderLayout.SOUTH);
    pack();
    setSize(220,300);
    prepareShow();
    setVisible(false);
  }

  private void prepareShow() {
    setLocation(600,200);
  }

  public void setEditorArea(JTextArea ea) {
    editorArea = ea;
    editorArea.addCaretListener( new CaretListener() {
      public void caretUpdate(CaretEvent ce) {
        if(BreakpointFrame.this.modeSelect) {
          BreakpointFrame.this.addBreakpoint(ce.getDot());
        }
      }
    });
  }

  public void setBreakpoints(HashSet tb) {
    breakpoints = tb;
    model.fireTableDataChanged();
  }

  public HashSet getBreakpoints() {
    return breakpoints;
  }

  public void resetBreakpoints() {
    setBreakpoints(new HashSet());
  }

  public void setProgram(RTProgram tp) {
    rtprog = tp;
  }

  public void setHighlight() {
    if(!selected) return;
    PositionRange pr = rtprog.getPositionRangeAt(selectedBreakpoint,0);
    Element root = editorArea.getDocument().getDefaultRootElement();
    int begin = root.getElement(pr.beginLine-1).getStartOffset()
                + pr.beginColumn - 1;
    int end = root.getElement(pr.endLine-1).getStartOffset()
                + pr.endColumn;
    try {
      if(highlight != null)
        editorArea.getHighlighter().changeHighlight(highlight,begin,end);
      else
        highlight = editorArea.getHighlighter().addHighlight(begin,end,
          new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE));
      editorArea.repaint();
    }    
    catch(Throwable t) {
      JOptionPane.showInternalMessageDialog(this,
        IUI.get("DIALOG_BREAKPOINT_MARKING_ERROR").replaceAll("%%CYCLE",Integer.toString(selectedBreakpoint))
        +": "+t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
      unsetHighlight(); 
    }
  }

  public void unsetHighlight() {
    if(highlight == null) return;
    editorArea.getHighlighter().removeHighlight(highlight);
    editorArea.repaint();
    highlight = null;
  }

  public void selectBreakpoint(int b) {
    if(breakpoints.contains(new Integer(b))) {
      selected = true; 
      selectedBreakpoint = b;
      setHighlight();
      deleteButton.setEnabled(true);
    }
    else deselectBreakpoint();
  }

  public void deselectBreakpoint() {
    unsetHighlight();
    selected = false;
    deleteButton.setEnabled(false);
  } 

  public void breakpointSelectAndFocus(int b) {
    selectBreakpoint(b);
  }

  public void deleteBreakpoint() {
    if(!selected) return;
    if(breakpoints.remove(new Integer(selectedBreakpoint))) {
      deselectBreakpoint();
      model.fireTableDataChanged();
    }
    else deselectBreakpoint();
  }

  public void modeAddBreakpoint() {
    deselectBreakpoint();
    if(modeSelect) {
      addButton.setText(IUI.get("BUTTON_ADD_BREAKPOINT"));
      modeSelect = false;
      editorArea.setCursor(editorCursor);
    }
    else {
      editorCursor = editorArea.getCursor();
      modeSelect = true;
      addButton.setText(IUI.get("BUTTON_CANCEL"));
      editorArea.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
  }

  public void addBreakpoint(int dot) {
    if(!modeSelect) return;
    modeAddBreakpoint();
    Element root = editorArea.getDocument().getDefaultRootElement();
    int line = root.getElementIndex(dot);
    int column = dot - root.getElement(line).getStartOffset() + 1;
    line++; 
    int idx = rtprog.getParStatsIndexAtPosition(line,column);
    if(idx == -1) return;
    breakpoints.add(new Integer(idx));
    selectBreakpoint(idx);
    model.fireTableDataChanged();
    this.moveToFront();
  }

  public void invoke() {
	if(!onDesktop) {
      desktop.add(this);
      onDesktop = true;
    }
    prepareShow();
    setVisible(true);
    moveToFront();
    if(modeSelect) modeAddBreakpoint();
  }

  public void revokeInternal() {
    onDesktop = false;
    deselectBreakpoint();
    if(modeSelect) modeAddBreakpoint();
  }

  public void revoke() {
    revokeInternal();
    setVisible(false);
    desktop.remove(this);
  }

  public void updateCaptions() {
    setTitle(IUI.get("TITLE_BREAKPOINTS"));
    addButton.setText(IUI.get("BUTTON_ADD_BREAKPOINT"));
    deleteButton.setText(IUI.get("BUTTON_DELETE_BREAKPOINT"));
    deselButton.setText(IUI.get("BUTTON_RELEASE_BREAKPOINT_MARKING"));
    pack();
    if(isShowing()) show();
  }
} 
