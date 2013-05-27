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

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTStat;

public class Statement {

  private PositionRange pr = null;
  private PositionRange casepr = null;
  private ProgramControl pc = null;
  private int statement_type = RTSimGlobals.ERR;
  public BitSequence left = null;
  private Expression right = null;
  private Label label = null;
  private Memory memory = null;
  private boolean busOnLeftSide = false;
  private int controlSignal = -1;
  private boolean ifexpr = false;

  public void dumpSimTree(PrintWriter out, String indent) {
    switch(statement_type) {
    case RTSimGlobals.NOP:
      out.println(indent+"nop");
      break;
    case RTSimGlobals.GOTO:
      out.println(indent+"goto");
      out.println(indent+"label : Label = "+label.getIdStr());
      break;
    case RTSimGlobals.WRITE:
      out.println(indent+"write");
      out.println(indent+"memory : Memory = "+memory.getIdStr());
      break;
    case RTSimGlobals.READ:
      out.println(indent+"read");
      out.println(indent+"memory : Memory = "+memory.getIdStr());
      break;
    case RTSimGlobals.ASSIGN:
      out.println(indent+"RT operation");
      out.println(indent+"left : BitSequence = "+left.toString());
      out.println(indent+"right : Expression = ");
      right.dumpSimTree(out,indent+"  ");
      break;
    case RTSimGlobals.ERR:
      out.println(indent+"error statement");
      break;
    default:
      out.println(indent+"unknown statement type "+statement_type);
    }
  }

  public Statement copy() {
    return new Statement(pr,pc,statement_type,left,right,label,memory,
			 busOnLeftSide,controlSignal);
  }

  Statement(PositionRange tpr, ProgramControl tpc, int tst,
	    BitSequence toLeft, Expression toRight, Label toLabel,
	    Memory toMemory, boolean tBLS, int tcS) {
    pr = tpr; pc = tpc; statement_type = tst; left = toLeft; right = toRight;
    label = toLabel; memory = toMemory; busOnLeftSide = tBLS;
    controlSignal = tcS;
    if(tst == RTSimGlobals.ASSIGN) right.sinkWidth(left.getWidth());
  }

  Statement() {
    statement_type = RTSimGlobals.NOP;
  }

  Statement(Label l) {
    if(l != null) {
      label = l;
      statement_type = RTSimGlobals.GOTO;
    }
  }

  /**
   * @return Breite der rechten Seite bei Zuweisung, sonst 0
   */
  public int getRValWidth() {
    if(statement_type == RTSimGlobals.ASSIGN) return right.getWidth();
    else return 0;
  }

  Statement(ProgramControl tpc, ASTStat stat) {
    pc = tpc;
    pr = stat.getPositionRange(); 
    statement_type = stat.getStatementType();
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
        busOnLeftSide = stat.hasBusOnLeftSide();
        left = new BitSequence(pc,stat.getBitSequence());
        right = new Expression(pc,stat.getExpression());
        right.sinkWidth(left.getWidth());
        break;
      case RTSimGlobals.READ:
      case RTSimGlobals.WRITE:
        memory = stat.getMemory();
        break;
      case RTSimGlobals.GOTO:
        if(stat.isGotoEnd()) label = pc.getEndLabel();
        else label = stat.getLabel();
    }
  }

  Statement(ProgramControl tpc, PositionRange tpr, boolean ifexp) { 
    pc = tpc;
    pr = tpr;
    ifexpr=ifexp;
    statement_type = RTSimGlobals.IFBAILOUT;
  }
  
  Statement(ProgramControl tpc, PositionRange tpr, boolean ifexp,
		  PositionRange cpr) { 
	 pc = tpc;
	 pr = tpr;
	 casepr = cpr;
	 ifexpr=ifexp;
	 statement_type = RTSimGlobals.SWITCHBAILOUT;
  }

  /**
   * gibt in RTSimGlobals definierten Statement-Typ zur&uuml;cl
   */
  public int getStatementType() { return statement_type; }

  public Label getLabel() { return label; }

  public boolean getIfExpr() {
	  return ifexpr;
  }
  
  public boolean hasBusOnLeftSide() { return busOnLeftSide; }

  public void emitDeltaOperation(String indent, PrintWriter out,
				 SignalsData signalsData) {
    switch(statement_type) {
    case RTSimGlobals.ASSIGN:
    case RTSimGlobals.READ:
    case RTSimGlobals.WRITE:
      int C = signalsData.lookupStatement(this);
      out.println(indent+"C_calc("+C+") := '1';");
      return;
    case RTSimGlobals.GOTO:
      out.println(indent+"goto_calc := "+label.getStatSeqEntry()+";");
    }
  }

  private String fitOutputWidth(String e, int fromWidth, int toWidth) {
    if(fromWidth < toWidth) {
      String back = "(\"";
      for(int i=fromWidth;i<toWidth;i++) back += "0";
      return back + "\" & "+e+")";
    }
    else return e;
  }

  public int getLeftWidth() { return left.getWidth(); }

  private int getNetWidth() {
    int rw = right.getWidth();
    int lw = left.getWidth();
    return (rw>lw)?rw:lw;
  }

  public void emitVHDLSignalDeclaration(int c,String indent, PrintWriter out) {
    if(statement_type != RTSimGlobals.ASSIGN) return;
    int nw = getNetWidth();
    out.println(indent+"SIGNAL net_C"+c+" : std_logic_vector ("+
		(nw-1)+" DOWNTO 0);");
  }

  public String getVHDLRightSideForBit(int netBitIndex) {
    switch(statement_type) {
    case RTSimGlobals.ASSIGN: return "rtop_C"+controlSignal+"_out("+netBitIndex+")";
    case RTSimGlobals.READ: return memory.getVHDLName()+"_data_out("
			      +netBitIndex+")";
    default: return "";
    }
  }

  public void emitVHDLSignalAssignments(int c,String indent, PrintWriter out) {
    switch(statement_type) {
    case RTSimGlobals.ASSIGN:
      int nw = getNetWidth();
      out.println(indent+"-- C"+c+"   "+toString());
      out.println(indent+"net_C"+c+" <= "+fitOutputWidth(right.toVHDL(),
	       					      right.getWidth(),nw)
   		+";");
      // left.emitVHDLSignalAssignments(c,indent,out);
      return;
      /*
    case RTSimGlobals.READ:
      out.println(indent+"mem_"+memory.getIdStr()+"_CE <= "
		  + "'1' WHEN C("+c+") = '1' ELSE 'Z';");
      out.println(indent+"mem_"+memory.getIdStr()+"_WE <= "
		  + "'0' WHEN C("+c+") = '1' ELSE 'Z';");
      out.println(indent+"mem_"+memory.getIdStr()+"_addr <= "
		  + "reg_"+memory.getAddrIdStr()+"_out WHEN C("
		  + c+") = '1' ELSE (OTHERS => 'Z');");
      out.println(indent+"reg_"+memory.getDataIdStr()+"_in <= "
		  + "mem_"+memory.getIdStr()+"_data_out WHEN C("
		  + c+") = '1' ELSE (OTHERS => 'Z');");
      return;
    case RTSimGlobals.WRITE:
      out.println(indent+"mem_"+memory.getIdStr()+"_CE <= "
		  + "'1' WHEN C("+c+") = '1' ELSE 'Z';");
      out.println(indent+"mem_"+memory.getIdStr()+"_WE <= "
		  + "'1' WHEN C("+c+") = '1' ELSE 'Z';");
      out.println(indent+"mem_"+memory.getIdStr()+"_addr <= "
		  + "reg_"+memory.getAddrIdStr()+"_out WHEN C("
		  + c+") = '1' ELSE (OTHERS => 'Z');");
      out.println(indent+"mem_"+memory.getIdStr()+"_data_in <= "
		  + "reg_"+memory.getDataIdStr()+"_out WHEN C("
		  + c+") = '1' ELSE (OTHERS => 'Z');");
		  return;*/
    default:
      return;
    }
  }

  public void emitVHDL(String indent,PrintWriter out) {
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
        out.println(indent+"temp := (OTHERS => '0');");
        out.println(indent+"temp("+(right.getWidth()-1)+" DOWNTO 0) := "
		    +right.toVHDL()+";");
        left.emitVHDLassign(indent, out);
        return;
      case RTSimGlobals.READ:
        out.println(indent+"-- read "+memory.getIdStr());
        return;
      case RTSimGlobals.WRITE:
        out.println(indent+"-- write "+memory.getIdStr());
        return;
      default:
        out.println(indent+"-- "+toString());
        return;
    }
  }

  public boolean busOnLeftSide() {
    if(statement_type == RTSimGlobals.ASSIGN) return left.containsBus();
    else return false;
  }

  public boolean busOnRightSide() {
    if(statement_type == RTSimGlobals.ASSIGN) return right.containsBus();
    else return false;
  }

  public boolean usefull() {
    return statement_type == RTSimGlobals.ASSIGN
      || statement_type == RTSimGlobals.READ
      || statement_type == RTSimGlobals.WRITE
      || statement_type == RTSimGlobals.GOTO;
  }

  public boolean exec() {
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
        BitVector bv = right.eval();
        return left.assign(bv);
      case RTSimGlobals.READ:
        if(!memory.read()) {
          if(memory.written()) 
            pc.raiseRuntimeError("Wiederholter Zugriff im gleichen Takt auf Speicher \""+memory.getIdStr()+"\" w�hrend read-Anweisung",pr);
          else
            pc.raiseRuntimeError("Versucht zum wiederholten Mal waehrend des gleichen Takt auf das Datenregister \""+memory.getDataIdStr()+"\" des Speichers \""+memory.getIdStr()+"\" zuzugreifen!",pr);
          return false;
        }
        else return true;
      case RTSimGlobals.WRITE:
        if(!memory.write()) {
          pc.raiseRuntimeError("Wiederholter Zugriff im gleichen Takt auf Speicher \""+memory.getIdStr()+"\" waehrend write-Anweisung",pr);
          return false;
        }
        else return true;
      case RTSimGlobals.GOTO:
        if(!pc.performGoto(label)) {
          pc.raiseRuntimeError("Mehr als eine goto-Anweisung wurden im gleichen Takt ausgefuehrt",pr);
          return false;
        } 
        else return true;
      case RTSimGlobals.IFBAILOUT:
      case RTSimGlobals.SWITCHBAILOUT:
      case RTSimGlobals.NOP: return true;
      default:
        pc.raiseRuntimeError("interner Fehler: falscher Typ mit Nr. "+statement_type+" in Statement",pr);
       return false;
    }
  }

  public PositionRange getPositionRange() { return pr; }
  
  public PositionRange getCasePosition() { return casepr; }

  public String toString() {
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
        return left.toString() + " " + RTSimGlobals.typeToLiteral(RTSimGlobals.ASSIGN) + " " + right.toString();
      case RTSimGlobals.WRITE:
      case RTSimGlobals.READ:
        return RTSimGlobals.typeToLiteral(statement_type) + " " + memory.getIdStr();
      case RTSimGlobals.GOTO:
        return RTSimGlobals.typeToLiteral(RTSimGlobals.GOTO) + " "
             + label.getIdStr();
      case RTSimGlobals.NOP:
        return RTSimGlobals.typeToLiteral(RTSimGlobals.NOP);
      default: return "<falscher Statement-Typ>";
    }
  }

  public boolean equals(Object o) {
    if(!(o instanceof Statement)) return false;
    Statement s = (Statement) o;
    if(statement_type != s.statement_type) return false;
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
        return left.equals(s.left) && right.equals(s.right);
      case RTSimGlobals.WRITE:
      case RTSimGlobals.READ:
        return memory.equals(s.memory);
      case RTSimGlobals.GOTO:
        return label.equals(s.label);
      case RTSimGlobals.NOP:
        return true;
      default:
        return false;
    }
  }

  public int hashCode() {
    int b;
    if(statement_type == RTSimGlobals.ASSIGN) b = right.hashCode();
    else b = 0;
    return b * 1000 + statement_type;
  }
      
  public void calculateSignals(SignalsData signalsData) {
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
      case RTSimGlobals.WRITE:
      case RTSimGlobals.READ:
        signalsData.insertStatement(this);
      default:
    }
  }

  public void insertDrivers(SignalsData signalsData) {
    switch(statement_type) {
    case RTSimGlobals.ASSIGN:
      if(left != null) left.insertDrivers(signalsData,controlSignal);
      return;
    case RTSimGlobals.READ:
      if(memory != null) memory.insertDrivers(signalsData,controlSignal);
    }
  }

  public void setControlSignal(int ts) {
    controlSignal = ts;
    switch(statement_type) {
    case RTSimGlobals.READ: memory.setReadControlSignal(ts); break;
    case RTSimGlobals.WRITE: memory.setWriteControlSignal(ts); break;
    }
  }

  public Memory getMemory() { return memory; }

  public int getControlSignal() { return controlSignal; }

  public boolean isTransferOperation() {
    switch(statement_type) {
      case RTSimGlobals.ASSIGN:
      case RTSimGlobals.READ:
      case RTSimGlobals.WRITE:
        return true;
      default:
        return false;
    }
  }

  public Expression getRight() {
    if(statement_type == RTSimGlobals.ASSIGN) return right;
    else return null;
  }

  /**
   * emits tristate drivers for bus writes
   */
  public void emitTristateDrivers(String indent, PrintWriter out, int cs) {
    if(statement_type == RTSimGlobals.ASSIGN)
      left.emitTristateDrivers(indent,out,cs);
  }
}
