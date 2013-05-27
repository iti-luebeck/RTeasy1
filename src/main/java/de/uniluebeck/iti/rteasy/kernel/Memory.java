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


package de.uniluebeck.iti.rteasy.kernel;


import java.util.*;
import java.io.PrintWriter;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;

public class Memory extends SimulationObject {
  private Hashtable mem = new Hashtable();
  private Register addrReg, dataReg;
  private boolean written = false;
  private int readControlSignal = -1;
  private int writeControlSignal = -1;

  class BoolArrayComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      return RTSimGlobals.boolArrayCompare((boolean[]) o1, (boolean[]) o2);
    }
  }

  Memory(String s, PositionRange tpr, Register taddr, Register tdata) {
    super(s,tpr);
    addrReg = taddr;
    dataReg = tdata;
  }

  public Register getAddr() {return addrReg;}
  public String getVHDLName() { return "mem_"+getIdStr(); }
  public String getDataIdStr() { return dataReg.getIdStr(); }
  public String getAddrIdStr() { return addrReg.getIdStr(); }
  public int getDataWidth() { return dataReg.getWidth(); }
  public int getAddrWidth() { return addrReg.getWidth(); }
  public Register getDataReg() { return dataReg; }
  public String getPrettyDecl() { return getIdStr()+" ("+addrReg.getIdStr()
    +","+dataReg.getIdStr()+")"; }

  public void insertDrivers(SignalsData signalsData, int controlSignal) {
    int i, dri = dataReg.leftBound();
    for(i=getDataWidth()-1;i>=0;i--,dri=dataReg.nextRightIndex(dri))
      signalsData.insertDriver(dataReg,dri,controlSignal,i);
  }

  public void emitVHDLSignalDeclarations(String indent, PrintWriter out) {
    out.println(indent+"SIGNAL mem_"
		+getIdStr()+"_data_out, mem_"+getIdStr()+"_data_in_trans : "+ dataReg.getVHDLType()+";");
    out.println(indent+"SIGNAL mem_"+getIdStr()+"_CS, mem_"
		+getIdStr()+"_WE, mem_"+getIdStr()+"_SELECT_ALL : std_logic;");
  }

  /*
  public void emitVHDLSignalAssignments(String indent, PrintWriter out) {
    out.println(indent+"mem_"+getIdStr()+"_CE <= 'L';");
  }*/

  public int getCellCount() {
    return 1 << getAddrWidth();
  }

  public void emitInstantiation(String indent, PrintWriter out) {
    out.println(indent+"-- memory "+getIdStr());
    out.println(indent+getVHDLName()+"_control: sram_control");
    out.println(indent+"  GENERIC MAP(data_width => "+getDataWidth()+")");
    out.println(indent+"  PORT MAP(");
    out.println(indent+"    CLK => CLK_SIG, RESET => RESET_SIG,");
    out.println(indent+"    C_WRITE => "+(writeControlSignal==-1?"'0'":"C_SIG("+writeControlSignal+")")+",");
    out.println(indent+"    C_READ => "+(readControlSignal==-1?"'0'":"C_SIG("+readControlSignal+")")+",");
    out.println(indent+"    DATA_IN => "+dataReg.getVHDLName()+"_out,");
    out.println(indent+"    TO_DATA_IN => "+getVHDLName()+"_data_in_trans,");
    out.println(indent+"    CS => "+getVHDLName()+"_CS,");
    out.println(indent+"    WE => "+getVHDLName()+"_WE,");
    out.println(indent+"    SELECT_ALL => "+getVHDLName()+"_SELECT_ALL);");
    out.println();
    out.println(indent+getVHDLName()+"_array: sram_array");
    out.println(indent+"  GENERIC MAP(addr_width => "+getAddrWidth()+", data_width => "+getDataWidth()+")");
    out.println(indent+"  PORT MAP(");
    out.println(indent+"    CS => "+getVHDLName()+"_CS,");
    out.println(indent+"    WE => "+getVHDLName()+"_WE,");
    out.println(indent+"    SELECT_ALL => "+getVHDLName()+"_SELECT_ALL,");
    out.println(indent+"    ADDR => "+addrReg.getVHDLName()+"_out,");
    out.println(indent+"    DATA_IN => "+getVHDLName()+"_data_in_trans,");
    out.println(indent+"    DATA_OUT => "+getVHDLName()+"_data_out);");
  }

  /*
  public void emitVHDLProcess(String indent, PrintWriter out) {
    out.println(indent+"-- memory logic for "+getIdStr());
    out.println(indent+"mem_"+getIdStr()+" : PROCESS (CLK, "
+ "mem_"+getIdStr()+"_CE, mem_"
		+getIdStr()+"_WE, mem_"+getIdStr()+"_addr, mem_"
		+getIdStr()+"_data_in)");
    out.println(indent+"  TYPE mem_"+getIdStr()+"_table IS ARRAY (0 TO "
		+(getCellCount()-1)+") OF "+dataReg.getVHDLType()+";");
    out.println(indent+"  VARIABLE table : mem_"+getIdStr()+"_table := "
		+ "(OTHERS => (OTHERS => '0'));");
    out.println(indent+"BEGIN");
    out.println(indent+"  IF rising_edge(CLK) THEN");
    out.println(indent+"    IF mem_"+getIdStr()+"_CE='1' THEN");
    out.println(indent+"      IF mem_"+getIdStr()+"_WE='1' THEN");
    out.println(indent+"        table(slv2n(mem_"+getIdStr()+"_addr)) := "
		+ "mem_"+getIdStr()+"_data_in;");
    out.println(indent+"        mem_"+getIdStr()+"_data_out <= (OTHERS => 'Z'"+
		");");
    out.println(indent+"      ELSE");
    out.println(indent+"        mem_"+getIdStr()+"_data_out <= table(slv2n("
		+ "mem_"+getIdStr()+"_addr));");
    out.println(indent+"      END IF;");
    out.println(indent+"    ELSE");
    out.println(indent+"      mem_"+getIdStr()+"_data_out <= (OTHERS => 'Z');"
		);
    out.println(indent+"    END IF;");
    out.println(indent+"  END IF;");
    out.println(indent+"END PROCESS;");
    out.println(indent+"-- incoming connections from addr and data register");
    out.println(indent+getVHDLName()+"_addr <= "
                +addrReg.getVHDLName()+"_out;");
    out.println(indent+getVHDLName()+"_data_in <= "
		+dataReg.getVHDLName()+"_out;");
    out.println(indent+"-- triggering by control signals");
    String CE_trigger = null;
    if(readControlSignal != -1) {
      CE_trigger = "C("+readControlSignal+")='1'";
      out.println(indent+getVHDLName()+"_WE <= '0' WHEN C("
		  + readControlSignal + ")='1' ELSE '1';");
    }
    if(writeControlSignal != -1) {
      if(CE_trigger == null) {
	CE_trigger = "C("+writeControlSignal+")='1'";
	out.println(indent+getVHDLName()+"_WE <= '1' WHEN C("
		    + writeControlSignal + ")='1' ELSE '0';");
      }
      else CE_trigger += " OR C("+writeControlSignal+")='1'";
    }
    if(CE_trigger != null)
      out.println(indent+getVHDLName()+"_CE <= '1' WHEN "
		  + CE_trigger + " ELSE '0';");
  }*/

  public boolean read() {
    if(dataReg.written()) return false;
    if(written) return false;
    if(mem.containsKey(addrReg.getContent()))
      dataReg.setContent(new BitVector((BitVector) mem.get(addrReg.getContent())));
    else
      dataReg.setContent(new BitVector("0"));
    return true;
  }

  public boolean write() {
    if(!written) {
      mem.put(new BitVector(addrReg.getContent()),new BitVector(dataReg.getContent()));
      written = true;
      return true;
    }
    else return false;
  }

  public ArrayList getUsedCellsSorted() {
    ArrayList cells = new ArrayList(mem.keySet());
    int aw = getAddrWidth();
    for(int i=0;i<cells.size();i++) cells.set(i,((BitVector) cells.get(i)).toBoolArray(aw));
    Collections.sort(cells, new BoolArrayComparator());
    return cells;
  }
 
  public boolean[] getDataAt(boolean address[]) {
    int dw = getDataWidth();
    int aw = getAddrWidth();
    boolean back[] = new boolean[dw];
    int width = address.length<=aw?address.length:aw;
    String s = "";
    for(int i=0;i<width;i++) s = (address[i]?"1":"0") + s;
    BitVector abv = new BitVector(s);
    if(mem.containsKey(abv)) {
      BitVector bv = (BitVector) mem.get(abv);
      for(int i=0;i<dw;i++) back[i] = bv.get(i);
    }
    else {
      for(int i=0;i<dw;i++) back[i] = false;
    }
    return back;
  } 

  public void setDataAt(boolean address[], boolean data[]) {
    int dw = getDataWidth();
    int aw = getAddrWidth();
    boolean back[] = new boolean[dw];
    int width = address.length<=aw?address.length:aw;
    String s = "";
    for(int i=0;i<width;i++) s = (address[i]?"1":"0") + s;
    BitVector abv = new BitVector(s);
    width = data.length<=dw?data.length:dw;
    s = ""; 
    for(int i=0;i<width;i++) s = (data[i]?"1":"0") + s;
    BitVector dbv = new BitVector(s);
    mem.put(abv,dbv);
  }

  public void clear() {
    mem.clear();
    written = false;
  }

  public boolean written() { return written; }
  public void clearWritten() { written = false; }

  public void setReadControlSignal(int cs) {
    readControlSignal = cs;
  }

  public void setWriteControlSignal(int cs) {
    writeControlSignal = cs;
  }
} 
    
