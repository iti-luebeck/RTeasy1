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

import java.io.PrintWriter;
import java.util.BitSet;

import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.frontend.ASTRegBusDecl;

public class Register extends RegBus {
  private boolean written[];
  private boolean content_old[];
  private boolean content_new[];
  private boolean valueChanged = false;
  private boolean isReference = false;

  Register(ASTRegBusDecl regdecl) {
    super(regdecl.getName(),regdecl.getPositionRange(),regdecl.getWidth(),
	  regdecl.getOffset(),regdecl.getDirection());
    written = new boolean[width];
    content_old = new boolean[width];
    content_new = new boolean[width];
    clear();
  }

  public String getVHDLName() { return "reg_"+getIdStr(); }

  public void emitVHDLSignalDeclarations(String indent, PrintWriter out) {
    out.println(indent+"SIGNAL reg_"+getIdStr()+"_in : "
		+getVHDLType()+" := (OTHERS => 'L');");
    out.println(indent+"SIGNAL reg_"+getIdStr()
		+"_out : "+getVHDLType()+" := (OTHERS => '0');");
  }
  
  public void emitVHDLSignalDeclarationsArray(String indent, PrintWriter out, String id) {
	    out.println(indent+"SIGNAL reg_"+id+"_in : "
			+getVHDLType()+" := (OTHERS => 'L');");
	    out.println(indent+"SIGNAL reg_"+id
			+"_out : "+getVHDLType()+" := (OTHERS => '0');");
  }

  public void emitVHDLProcess(String indent, PrintWriter out) {
    int i = getOffset();
    int bound = i+getWidth()-1;
    out.println(indent+"-- state logic");
    out.println(indent+"reg_"+getIdStr()+": PROCESS(CLK)");
    out.println(indent+"BEGIN");
    out.println(indent+"  IF rising_edge(CLK) THEN");
    out.println(indent+"    "+getVHDLName()+"_out <= "+getVHDLName()+"_in;");
    out.println(indent+"  END IF;");
    out.println(indent+"END PROCESS;");
  }

  public void emitVHDLInstantiation(String indent, PrintWriter out) {
    out.println(indent+"-- component instantiation for register "+getIdStr());
    out.println(indent+getVHDLName()+": dff_reg");
    out.println(indent+"  GENERIC MAP(triggering_edge => '1', width => "+getWidth()+")");
    out.println(indent+"  PORT MAP(CLK => CLK_SIG, RESET => RESET_SIG,");
    out.println(indent+"           INPUT => "+getVHDLName()+"_in,");
    out.println(indent+"           OUTPUT => "+getVHDLName()+"_out);");
  }

  public BitVector getContent() {
    String nval = "";
    for(int i=0;i<width;i++) nval = (content_old[i]?"1":"0")+nval;
    return new BitVector(nval);
  }

  public BitSet getContentBitSet() {
    BitSet bs = new BitSet();
    for(int i=0;i<width;i++) if(content_old[i]) bs.set(i);
    return bs;
  }

  /** checks if value of register has changed compared to last cycle
   * @return true/false
   */
  public boolean valueChanged() {
    boolean bk = valueChanged;
    valueChanged = false;
    return bk;
  }

  /** set new value of register
   * @param bv new value
   */
  public void setContent(BitVector bv) {
    valueChanged = true;
    for(int i=0;i<width;i++) {
      content_new[i] = bv.get(i);
      written[i] = true;
    }
  }

  public void setContent(BitSet bs) {
    valueChanged = true;
    for(int i=0;i<width;i++) {
      content_new[i] = bs.get(i);
      written[i] = true;
    }
  }

  public void editContent(BitVector bv) {
    valueChanged = true;
    for(int i=0;i<width;i++) {
      content_new[i] = bv.get(i);
      content_old[i] = content_new[i];
    }
  }

  public void editContent(BitSet bs) {
    valueChanged = true;
    for(int i=0;i<width;i++) {
      content_new[i] = bs.get(i);
      content_old[i] = content_new[i];
    }
  }

  public void editContent(boolean bits[]) {
    valueChanged = true;
    if(bits.length != width) return; 
    for(int i=0;i<width;i++) {
      content_new[i] = bits[i];
      content_old[i] = content_new[i];
    }
  }

  public boolean get(int i) {
    i -= offset;
    if(i<width&&i>=0) 
      if(direction) return content_old[i];
      else return content_old[width-i-1];
    else return false;
  }

  public boolean get_new(int i) {
    i -= offset;
    if(i<width&&i>=0)
      if(direction) return content_new[i];
      else return content_new[width-i-1];
    else return false;
  }

  public boolean get(int edgeType, int i) {
    switch(edgeType) {
      case RTSimGlobals.OSTAT_TYPE_2EDGE_2: return get_new(i);
      default: return get(i);
    }
  }

  public boolean set(int i, boolean b) {
    valueChanged = true;
    i -= offset;
    if(!direction) i = width-i-1;
    if(!written[i]) {
      if(i<width&&i>=0) {
        content_new[i] = b;
        written[i] = true;
      }
      return true;
    }
    else return false;
  }

  public void clear() {
    for(int i=0;i<width;i++) {
      written[i] = false;
      content_old[i] = false;
      content_new[i] = false;
    }
    valueChanged = false;
  } 

  public void clearWritten() {
    for(int i=0;i<width;i++) written[i] = false;
  }

  public void commit() {
    for(int i=0;i<width;i++) {
      content_old[i] = content_new[i];
      written[i] = false; 
    }
  }

  public boolean written() {
    for(int i=0;i<width;i++) if(written[i]) return true;
    return false;
  }

  public String getContentStr(int base) {
    return RTSimGlobals.boolArray2String(content_new,base);
  }
  
  public boolean[] getBoolArray() {
	  return content_new;
  }
  
  public boolean[] getBoolArrayOld() {
	  return content_old;
  }
  
  public void setReference(boolean b) {
	  this.isReference = b;
  }
  
  public boolean getIsReference() {
	  return isReference;
  }
}
