package de.uniluebeck.iti.rteasy.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SimObjectsBase;
import de.uniluebeck.iti.rteasy.kernel.Memory;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;

import java.util.*;


public class SimObjectsFrame extends JInternalFrame {

  public SimObjectsTableModel model;
  public JTable table;
  private JDesktopPane desktop;
  private Hashtable memoryFrames;
  private Hashtable regarrayFrames;
  private JLabel virtualPCLabel, cycleCountLabel, stateCaptionLabel, cycleCaptionLabel;
  private MemoryCellRenderer memoryCellRenderer;
  LinkedList<RegisterArray> ro = new LinkedList<RegisterArray>();
  

  public class MemoryCellRenderer extends DefaultTableCellRenderer {
    public final JButton button = new JButton(IUI.get("BUTTON_CONTENT"));
    private Color valueChangedBackground = Color.YELLOW;
    private Color oldBackground;

    MemoryCellRenderer() {
      super();
      setHorizontalAlignment(JLabel.RIGHT);
      oldBackground = getBackground();
    } 

     public Component getTableCellRendererComponent(JTable table,
       Object value, boolean isSelected, boolean hasFocus, int row, int col) {
       setBackground(oldBackground);
       
       if(value instanceof Memory || value instanceof RegisterArray) 
         return button;
       else if(((SimObjectsTableModel) table.getModel()).registerValueChangedAt(row,col)) {
	 // catch if register has changed value
         setBackground(valueChangedBackground);
         Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,col);
         // setBackground(oldBackground);
         return c;
       }
       else
         return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,col);
     }
  }

  class MemoryButton extends JButton {
    public Memory memory;
    MemoryButton(Memory m) {
      super(IUI.get("BUTTON_CONTENT"));
      memory = m;
    }
  }
  
  class RegArrayButton extends JButton {
	  public RegisterArray regarray;
	  RegArrayButton(RegisterArray r) {
		  super (IUI.get("BUTTON_CONTENT"));
		  regarray = r;
	  }
  }
  
  class MRCellEditor extends DefaultCellEditor {
	    RegArrayButton rbutton;
	    MemoryButton mbutton;

	    MRCellEditor(RegArrayButton b, MemoryButton m) {
		      super(new JTextField());
		      rbutton = b;
		      mbutton = m;
		      setClickCountToStart(1);
		      rbutton.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent ae) {
		          fireEditingStopped();
		        }
		      });
		      mbutton.addActionListener(new ActionListener() {
		          public void actionPerformed(ActionEvent ae) {
		            fireEditingStopped();
		          }
		        });
		    }
	    
	    protected void fireEditingStopped() {super.fireEditingStopped(); }

	    public Component getTableCellEditorComponent(JTable table, Object value,
	      boolean isSelected, int row, int col) {
	     if(value instanceof RegisterArray){
	        rbutton.regarray = (RegisterArray) value; 
	        
	        return rbutton;
	      } else if(value instanceof Memory) {
	    	  mbutton.memory = (Memory) value;
	    	  
	    	  return mbutton;
	      } else {
	    	  return super.getTableCellEditorComponent(table,value,isSelected,row,col);
	      }
	    }
	  }
  
  public void setUpMRCellEditor() {
	    final RegArrayButton button = new RegArrayButton(null);
	    final MemoryButton mbutton = new MemoryButton(null);
	    final MRCellEditor regarrEditor = new MRCellEditor(button, mbutton);
	    table.getColumnModel().getColumn(1).setCellEditor(regarrEditor);
	    button.addActionListener(new ActionListener() {
	  	      public void actionPerformed(ActionEvent ae) {
	  	        RegArrayFrame rf = SimObjectsFrame.this.getRegArrayFrame(button.regarray);
	  	        //wegen mangelnder regarOrder erstmal ohne Hashtable
	  	    	//RegArrayFrame rf = new RegArrayFrame(ra);
	  	    	rf.setLocation(680,20);
	  	        desktop.add(rf);
	  	        rf.setVisible(true); 
	  	      }
	  	    });
	    mbutton.addActionListener(new ActionListener() {
  	      public void actionPerformed(ActionEvent ae) {
  	        MemoryFrame mf = SimObjectsFrame.this.getMemoryFrame(mbutton.memory);
  	        mf.setLocation(680,20);
  	        if(!desktop.isAncestorOf(mf)) {
  	        desktop.add(mf);
  	        }
  	        mf.setVisible(true); 
  	      }
  	    });
  }
  
  public MemoryFrame getMemoryFrame(Memory m) {
    return (MemoryFrame) memoryFrames.get(m);
  }
  
  public RegArrayFrame getRegArrayFrame(RegisterArray r) {
	  return (RegArrayFrame) regarrayFrames.get(r);
  }

  public void simUpdate() {
    model.fireTableDataChanged();
    Enumeration e = memoryFrames.keys();
    Enumeration er = regarrayFrames.keys();
    while(e.hasMoreElements()) {
      ((MemoryFrame) memoryFrames.get(e.nextElement())).simUpdate();
    }
    while(er.hasMoreElements()) {
    ((RegArrayFrame) regarrayFrames.get(er.nextElement())).simUpdate();
    }
   }

  private void setUpMemoryFrames(LinkedList memoryOrder) {
    memoryFrames = new Hashtable();
    ListIterator it = memoryOrder.listIterator();
    MemoryFrame mf;
    Memory m;
    while(it.hasNext()) {
      m = (Memory) it.next();
      mf = new MemoryFrame(m);
      memoryFrames.put(m,mf);
    }
  }
  
  private void setUpRegArrayFrames(LinkedList<RegisterArray> regarOrder) {
	  //RegarOrder erstmal per Hand:
	  
	  //regarOrder = ro;
	  //bis hier
	  regarrayFrames = new Hashtable();
	  ListIterator lit = regarOrder.listIterator();
	  RegArrayFrame rf;
	  RegisterArray r;
	  while(lit.hasNext()) {
		  r = (RegisterArray) lit.next();
		  rf = new RegArrayFrame(r);
		  regarrayFrames.put(r, rf);
	  }
  }

  public void disposeMemoryFrames() {
    Enumeration e = memoryFrames.keys();
    MemoryFrame mf;
    while(e.hasMoreElements()) {
      mf = (MemoryFrame) memoryFrames.get((Memory) e.nextElement());
      mf.dispose();
      desktop.remove(mf);
    }
  }
  
  public void disposeRegArrayFrames() {
	  Enumeration e = regarrayFrames.keys();
	  RegArrayFrame rf;
	  while(e.hasMoreElements()) {
		  rf = (RegArrayFrame) regarrayFrames.get((RegisterArray) e.nextElement());
		  rf.dispose();
		  desktop.remove(rf);
	  }
  }

  SimObjectsFrame(JDesktopPane d, LinkedList registerOrder, LinkedList busOrder, LinkedList memoryOrder,
		  LinkedList regArrOrder) {
    super(IUI.get("TITLE_SIMOBJFRAME"),true,false,true,true);
    desktop = d;
    table = new JTable();
   
    setNewData(registerOrder,busOrder,memoryOrder,regArrOrder);
    virtualPCLabel = new JLabel("0");
    cycleCountLabel = new JLabel("0");
    JPanel headPanel = new JPanel();
    stateCaptionLabel = new JLabel(IUI.get("STATE")+":");
    headPanel.add(stateCaptionLabel);
    headPanel.add(virtualPCLabel);
    cycleCaptionLabel = new JLabel(IUI.get("LABEL_CYCLECOUNT")+":");
    headPanel.add(cycleCaptionLabel);
    headPanel.add(cycleCountLabel);
    JComboBox baseBox = new JComboBox();
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_BIN));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_HEX));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_DEC2));
    baseBox.addItem(new SimObjectsBase(RTSimGlobals.BASE_HEX2));
    table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(baseBox)); 
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setToolTipText(IUI.get("TOOLTIP_BASE"));
    table.getColumnModel().getColumn(2).setCellRenderer(renderer);
    memoryCellRenderer = new MemoryCellRenderer();
    table.getColumnModel().getColumn(1).setCellRenderer(memoryCellRenderer);
    TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer(); 
    int headerWidth, cellWidth;
    for(int i=0;i<3;i++) {
//      headerWidth = headerRenderer.getTableCellRendererComponent(
//        null, table.getColumnModel().getColumn(i).getHeaderValue(),
//        false, false, 0, 0).getPreferredSize().width + 20;
    	headerWidth=renderer.getWidth();
      cellWidth = table.getDefaultRenderer(model.getColumnClass(i)).
        getTableCellRendererComponent(table,model.getLongestColumnValue(i),
        false, false, 0, i).getPreferredSize().width + 20;
      if(headerWidth > cellWidth) cellWidth = headerWidth;
      table.getColumnModel().getColumn(i).setPreferredWidth(cellWidth);
    }
    setUpMRCellEditor();
    table.setCellSelectionEnabled(true);
    getContentPane().add(headPanel,BorderLayout.NORTH);
    getContentPane().add(new JScrollPane(table),BorderLayout.CENTER);
    //pack();
    int x = registerOrder.size()+busOrder.size()+memoryOrder.size()+regArrOrder.size();
    if (x>18) { x=18; }
    setSize(330, (200+(18*x)));
    setUpMemoryFrames(memoryOrder);
    setUpRegArrayFrames(regArrOrder);
  }
 
  public void setCycleCount(int cc) { cycleCountLabel.setText(Integer.toString(cc)); }
  public void setVirtualPC(int pc) { virtualPCLabel.setText(Integer.toString(pc)); }

  public void setNewData(LinkedList registerOrder, LinkedList busOrder, LinkedList memoryOrder,
		  LinkedList regArrOrder) {
    model = new SimObjectsTableModel(registerOrder,busOrder,memoryOrder, regArrOrder, this);
    table.setModel(model);
    table.setPreferredScrollableViewportSize(table.getPreferredSize());
    model.fireTableDataChanged();
  }

  public void updateCaptions() {
    setTitle(IUI.get("TITLE_SIMOBJFRAME"));
    stateCaptionLabel.setText(IUI.get("STATE")+":");
    cycleCaptionLabel.setText(IUI.get("LABEL_CYCLECOUNT")+":");
    memoryCellRenderer.button.setText(IUI.get("BUTTON_CONTENT"));
    for(Enumeration e=memoryFrames.elements();e.hasMoreElements();)
      ((MemoryFrame) e.nextElement()).updateCaptions();
    model.fireTableDataChanged();
    //for(int i=0;i<3;i++) table.getColumnModel().getColumn(i).getHeaderValue()
    pack();
    if(isShowing()) {hide(); show();}
  }

  public void dispose() {
    super.dispose();
    disposeMemoryFrames();
    disposeRegArrayFrames();
  }
}  
