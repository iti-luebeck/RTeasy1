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
import java.util.LinkedList;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTBit_Seq;
import de.uniluebeck.iti.rteasy.frontend.ASTExpr;
import de.uniluebeck.iti.rteasy.gui.RTOptions;

public class Expression {
  private int type, width;
  private PositionRange pr = null;
  private ProgramControl pc = null;
  private Expression left = null;
  private Expression right = null;
  private Expression operand = null;
  private BitSequence bitSequence = null;
  private BitVector numConst = null;
  private int inputSignal = -1;
  private int base = RTSimGlobals.BASE_BIN;
  private boolean containsBus = false;
  private boolean containsRegArray = false;

  /**
   * Expression aus BitVector erzeugen
   */
  Expression(BitVector bv) {
    width = bv.getWidth();
    numConst = bv;
    type = RTSimGlobals.NUM_CONST;
  }

  /**
   * Expression aus durch BitString gegebenen BitVector erzeugen
   */
  Expression(String bs) {
    numConst = new BitVector(bs);
    width = numConst.getWidth();
    type = RTSimGlobals.NUM_CONST;
  }
  
  Expression(ASTBit_Seq bs) {
	  bitSequence = new BitSequence(pc,bs);
      width = bitSequence.getWidth();
      containsBus = bitSequence.containsBus();
      containsRegArray = bitSequence.containsRegArray();
      type = RTSimGlobals.BIT_SEQ;
  }

  /**
   * Expression aus Expression und monadischem Operator erzeugen.
   * Falls optype falsch, wird NOT genommen
   * @param optype Operator-Typ aus RTSimGlobals, 
   * RTSimGlobals.SIGN oder RTSimGlobals.NOT
   * @param expr Operand
   */
  Expression(int optype, Expression expr) {
    if(optype != RTSimGlobals.SIGN) optype = RTSimGlobals.NOT;
    type = optype;
    operand = expr;
    width = operand.getWidth();
    if(optype == RTSimGlobals.SIGN) width++;
    containsBus = operand.containsBus();
    containsRegArray = operand.containsRegArray();
  }

  /**
   * Expression aus zwei Expressions und bin&auml;rem Operator erzeugen.
   * @param optype Operator-Typ aus RTSimGlobals
   * @param expr1 linker Operand
   * @param expr2 rechter Operand
   */
  Expression(int optype, Expression expr1, Expression expr2) {
    type = optype;
    left = expr1;
    right = expr2;
    containsBus = expr1.containsBus() || expr2.containsBus();
    containsRegArray = expr1.containsRegArray() || expr2.containsRegArray();
    if(left.getWidth() > right.getWidth()) width = left.getWidth();
    else width = right.getWidth();
    if(type == RTSimGlobals.PLUS) width++;
    else if(type == RTSimGlobals.MINUS) width += 1; // changed from +2
  }

  /**
   * Expression aus AST-Expression erzeugen
   * @param tpc ProgramControl-Struktur des umfassenden RTProgram-Objektes
   * @param expr AST-Expression
   */
  Expression(ProgramControl tpc, ASTExpr expr) {
    pc = tpc;
    width = 0;
    type = expr.getType();
    pr = expr.getPositionRange();
    switch(type) {
      case RTSimGlobals.NUM_CONST:
        numConst = expr.getNumConst();
        base = expr.getBase();
        width = numConst.getWidth();
        break;
      case RTSimGlobals.BIT_SEQ:
        bitSequence = new BitSequence(pc,expr.getBitSeq());
        width = bitSequence.getWidth();
        containsBus = bitSequence.containsBus();
        containsRegArray = bitSequence.containsRegArray();
        break;
      case RTSimGlobals.SIGN:
        width = 1;
      case RTSimGlobals.NOT:
        operand = new Expression(pc,expr.getOperand());
        width += operand.getWidth();
        containsBus = operand.containsBus();
        containsRegArray = operand.containsRegArray();
        break;
      case RTSimGlobals.MINUS:
        width = 0;  // changed from 1
      case RTSimGlobals.PLUS:
        left = new Expression(pc,expr.getLeft());
        right = new Expression(pc,expr.getRight());
        containsBus = left.containsBus() || right.containsBus();
        containsRegArray = left.containsRegArray() || right.containsRegArray();
        if(left.getWidth() > right.getWidth()) width = left.getWidth();
        else width = right.getWidth();
        width++;
        break;
      case RTSimGlobals.LT:
      case RTSimGlobals.LE:
      case RTSimGlobals.GT:
      case RTSimGlobals.GE:
      case RTSimGlobals.EQ:
      case RTSimGlobals.NE:
        left = new Expression(pc,expr.getLeft());
        right = new Expression(pc,expr.getRight());
        containsBus = left.containsBus() || right.containsBus();
        containsRegArray = left.containsRegArray() || right.containsRegArray();
        width = 1;
        break;
      case RTSimGlobals.AND:
      case RTSimGlobals.NAND:
      case RTSimGlobals.OR:
      case RTSimGlobals.NOR:
      case RTSimGlobals.XOR:
        left = new Expression(pc,expr.getLeft());
        right = new Expression(pc,expr.getRight());
        containsBus = left.containsBus() || right.containsBus();
        containsRegArray = left.containsRegArray() || right.containsRegArray();
        if(left.getWidth() > right.getWidth()) width = left.getWidth();
        else width = right.getWidth();

    }
  }

  public void dumpSimTree(PrintWriter out, String indent) {
    out.println(indent+"width = "+width);
    if(RTSimGlobals.isBinOp(type)) {
      out.println(indent+"binary operator "+RTSimGlobals.typeToString(type));
      out.println(indent+"left : Expression = ");
      left.dumpSimTree(out,indent+"  ");
      out.println(indent+"right : Expression = ");
      right.dumpSimTree(out,indent+"  ");
    }
    else {
      switch(type) {
      case RTSimGlobals.SIGN:
      case RTSimGlobals.NOT:
        out.println(indent+"monadic operator "+RTSimGlobals.typeToString(type));
        out.println(indent+"operand : Expression = ");
        operand.dumpSimTree(out,indent+"  ");
        break;
      case RTSimGlobals.NUM_CONST:
        out.println(indent+"numerical constant");
        out.println(indent+"numConst : BitVector = "+numConst.toString());
        break;
      case RTSimGlobals.BIT_SEQ:
        out.println(indent+"combined bitword");
        out.println(indent+"bitSequence : BitSequence = "+bitSequence.toString());
        break;
      default:
        out.println(indent+"unknown Expression type "+type);
      }
    }
  }
      
  /**
   * passes width to branches
   * @param toWidth needed bit width (mostly derived from BitSequence in RT-Operation)
   */
  public void sinkWidth(int toWidth) {
    // System.err.println("sinkWidth for "+this.toString());
    if(toWidth > width) width = toWidth;
    int maxwidth;
    switch(type) {
    case RTSimGlobals.NOT: operand.sinkWidth(width); break;
    case RTSimGlobals.SIGN: operand.sinkWidth(width); break;
    default:
      if(RTSimGlobals.isBinOp(type)) {
        maxwidth = left.getWidth();
        if(maxwidth < right.getWidth()) maxwidth = right.getWidth();
        if(RTSimGlobals.isCompOp(type)) {
          // System.err.println("  maxwidth="+maxwidth);
          // behaviour: use only needed width of operands
          // plus 1 for signing
          left.sinkWidth(maxwidth+1);
          right.sinkWidth(maxwidth+1);
        }
        else { // arithmetic and boolean word operators (binary)
          // behaviour: just pass width
          // reason: 1. arithmetic precision
          //         2. correct application of boolean word operators
          //            especially nor and nand
          left.sinkWidth(width);
          right.sinkWidth(width);
        }
      }
    }
  }
          
  public boolean containsBus() { return containsBus; }
  
  public boolean containsRegArray() { return containsRegArray; }

  /**
   * gibt die Anzeige-Basis als in RTSimGlobals definierte Konstante
   * zur&uuml;ck
   * @return RTSimGlobals-Konstante f&uuml;r Anzeige-Basis
   */
  public int getBase() { return base; }

  public BitVector eval() { return eval(RTSimGlobals.OSTAT_TYPE_MEALY); }

  private String zeroVector(int n) {
    String bk = "";
    for(int i=0;i<n;i++) bk += "0";
    return bk;
  }

  private String concatToWidth(String op, int opWidth) {
    if(opWidth < width)
      return "\""+zeroVector(width-opWidth)+"\" & ("+op+")";
    else
      return op;
  }

  private String oneWidth() {
    if(width == 0) return "";
    else return "\""+zeroVector(width-1)+"1\"";
  }

  private String binOpVHDL(String operator) {
    return "("+left.toVHDL()+") "
      +operator+" ("+right.toVHDL()+")";
  }

  private String binFuncVHDL(String funcName) {
    return funcName+"(("+left.toVHDL()+"), ("+right.toVHDL()+"), "+(left.getWidth()-1)+")";
  }

    public boolean containsComparison() {
	switch(type) {
	case RTSimGlobals.NUM_CONST:
	case RTSimGlobals.BIT_SEQ:
	    return false;
	case RTSimGlobals.NOT: 
	case RTSimGlobals.SIGN:
            return operand.containsComparison();
	case RTSimGlobals.LT:
	case RTSimGlobals.LE:
	case RTSimGlobals.GT:
	case RTSimGlobals.GE:
	case RTSimGlobals.EQ:
	case RTSimGlobals.NE:
	    return true;
        case RTSimGlobals.MINUS:
	case RTSimGlobals.PLUS:
	case RTSimGlobals.AND:
	case RTSimGlobals.NAND:
	case RTSimGlobals.OR:
	case RTSimGlobals.NOR:
	case RTSimGlobals.XOR:
	    return left.containsComparison() || right.containsComparison();
	default:
	    return false;
	}
    }
  
 /**  
  * @param bundle contains the ports to use
  * @return Expression in VHDL using the ports from bundle
  */
  public String toVHDL() {
    // System.err.println("toVHDL "+toString()+", width="+width);
    switch(type) {
    case RTSimGlobals.NUM_CONST: return numConst.toVHDL(width);
    case RTSimGlobals.BIT_SEQ: return bitSequence.toVHDLrval(width);
    case RTSimGlobals.NOT: return "not ("+operand.toVHDL()+")";
    case RTSimGlobals.SIGN: // 2er-Komplement
      return "(not ("+operand.toVHDL()
	+")) + "+oneWidth();
    case RTSimGlobals.MINUS: // Addition mit 2er-Komplement
      return "("+left.toVHDL()+") + ((NOT ("+right.toVHDL()+") + "+oneWidth()+")";
    case RTSimGlobals.PLUS: return "("+left.toVHDL()+") + ("+right.toVHDL()+")";
    case RTSimGlobals.LT: return binFuncVHDL("signed_lt");
    case RTSimGlobals.LE: return binFuncVHDL("signed_le");
    case RTSimGlobals.GT: return binFuncVHDL("signed_gt");
    case RTSimGlobals.GE: return binFuncVHDL("signed_ge");
    case RTSimGlobals.EQ: return binFuncVHDL("signed_eq");
    case RTSimGlobals.NE: return binFuncVHDL("signed_ne");
    case RTSimGlobals.AND: return binOpVHDL("AND");
    case RTSimGlobals.NAND: return binOpVHDL("NAND");
    case RTSimGlobals.OR: return binOpVHDL("OR");
    case RTSimGlobals.NOR: return binOpVHDL("NOR");
    case RTSimGlobals.XOR: return binOpVHDL("XOR");
    default: return "<RTeasy error: wrong expression type>";
    }
  }

  private String binOpVHDL(String operator, ExpressionInputBundle bundle) {
    return "("+left.toVHDL(bundle)+") "
      +operator+" ("+right.toVHDL(bundle)+")";
  }

  private String binFuncVHDL(String funcName, ExpressionInputBundle bundle) {
    return funcName+"(("+left.toVHDL(bundle)+"), ("+right.toVHDL(bundle)+"), "+(left.getWidth()-1)+")";
  }

  public String toVHDL(ExpressionInputBundle bundle, int uwidth) {
    if(uwidth>width) return "\""+zeroVector(uwidth-width)+"\" & "+toVHDL(bundle);
    else return toVHDL(bundle);
  }
   
  public String toVHDL(ExpressionInputBundle bundle) {
    // System.err.println("toVHDL_bundle "+toString()+", width="+width);
	 
    switch(type) {
    case RTSimGlobals.NUM_CONST: return numConst.toVHDL(width);
    case RTSimGlobals.BIT_SEQ: return bitSequence.toVHDLrval(bundle,width);
    case RTSimGlobals.NOT: return "not ("+operand.toVHDL(bundle)+")";
    case RTSimGlobals.SIGN: // 2er-Komplement
      return "(not ("+operand.toVHDL(bundle)
	+")) + "+oneWidth();
    case RTSimGlobals.MINUS: // Addition mit 2er-Komplement
      return "("+left.toVHDL(bundle)+") + ((not ("+
	right.toVHDL(bundle)+")) + "+oneWidth()+")";
    case RTSimGlobals.PLUS: return binOpVHDL("+",bundle);
    case RTSimGlobals.LT: return binFuncVHDL("signed_lt",bundle);
    case RTSimGlobals.LE: return binFuncVHDL("signed_le",bundle);
    case RTSimGlobals.GT: return binFuncVHDL("signed_gt",bundle);
    case RTSimGlobals.GE: return binFuncVHDL("signed_ge",bundle);
    case RTSimGlobals.EQ: return binFuncVHDL("signed_eq",bundle);
    case RTSimGlobals.NE: return binFuncVHDL("signed_ne",bundle);
    case RTSimGlobals.AND: return binOpVHDL("AND",bundle);
    case RTSimGlobals.NAND: return binOpVHDL("NAND",bundle);
    case RTSimGlobals.OR: return binOpVHDL("OR",bundle);
    case RTSimGlobals.NOR: return binOpVHDL("NOR",bundle);
    case RTSimGlobals.XOR: return binOpVHDL("XOR");
    default: return "<RTeasy error: wrong expression type>";
    }
  }

  public String binOpCondition(SignalsData signalsData,String operator) {
    return "("+left.getVHDLcondition(signalsData)+" "+operator
      +" "+right.getVHDLcondition(signalsData)+")";
  }

  public String getVHDLcondition(SignalsData signalsData) {
    switch(type) {
    case RTSimGlobals.NOT:
      return "NOT ("+operand.getVHDLcondition(signalsData)+")";
    case RTSimGlobals.AND:
      return binOpCondition(signalsData,"AND");
    case RTSimGlobals.NAND:
      return binOpCondition(signalsData,"NAND");
    case RTSimGlobals.OR:
      return binOpCondition(signalsData,"OR");
    case RTSimGlobals.NOR:
      return binOpCondition(signalsData,"NOR");
    case RTSimGlobals.XOR:
      return binOpCondition(signalsData,"XOR");
    default:
      int I = signalsData.lookupExpression(this);
      return "I("+I+")";
      //return "forceSL(I("+I+"))='1'";
    }
  }

  public BitVector eval(int edgeType) {
    BitVector b;
    switch(type) {
      case RTSimGlobals.NUM_CONST: b = new BitVector(numConst); break;
      case RTSimGlobals.BIT_SEQ: b = bitSequence.eval(edgeType); break;
      case RTSimGlobals.NOT: b = operand.eval(edgeType).not(width); break;
      case RTSimGlobals.SIGN: b = operand.eval(edgeType).sign(width); break;
      case RTSimGlobals.PLUS: b = left.eval(edgeType).plus(right.eval(edgeType),width); break;
      case RTSimGlobals.MINUS: b = left.eval(edgeType).minus(right.eval(edgeType),width); break;
      case RTSimGlobals.LT: b= left.eval(edgeType).lt(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.LE: b= left.eval(edgeType).le(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.GT: b= left.eval(edgeType).gt(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.GE: b= left.eval(edgeType).ge(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.EQ: b= left.eval(edgeType).eq(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.NE: b= left.eval(edgeType).ne(right.eval(edgeType),left.getWidth()); break;
      case RTSimGlobals.AND: b= left.eval(edgeType).and(right.eval(edgeType),width); break;
      case RTSimGlobals.NAND: b= left.eval(edgeType).nand(right.eval(edgeType),width); break;
      case RTSimGlobals.OR: b= left.eval(edgeType).or(right.eval(edgeType),width); break;
      case RTSimGlobals.NOR: b= left.eval(edgeType).nor(right.eval(edgeType),width); break;
      case RTSimGlobals.XOR: b= left.eval(edgeType).xor(right.eval(edgeType),width); break;
      default: b= new BitVector();
    }
    return b;
  }

  public int getWidth() { return width; }

  /**
   * gibt den Typ der Expression zur&uuml;ck
   * @return Typ der Expression als RTSimGlobals-Konstante
   */
  public int getType() { return type; }

  public PositionRange getPositionRange() { return pr; }

  private String braceIfNecessary(String operandStr, int operandType) {
    if(RTSimGlobals.getOperatorPrecedence(operandType) >
       RTSimGlobals.getOperatorPrecedence(type))
      return "(" + operandStr + ")";
    else return operandStr;
  }

  public String toString() {
    switch(type) {
      case RTSimGlobals.NUM_CONST:
        return (base==RTSimGlobals.BASE_BIN?"%":(base==RTSimGlobals.BASE_HEX?
                  "$":""))
            + RTSimGlobals.boolArray2String(numConst.toBoolArray(width),base);
      case RTSimGlobals.BIT_SEQ:
        return bitSequence.toString();
      case RTSimGlobals.NOT:
        return RTSimGlobals.typeToLiteral(type) + " "
          + braceIfNecessary(operand.toString(),operand.getType());
      case RTSimGlobals.SIGN:
        return RTSimGlobals.typeToLiteral(type)
          + braceIfNecessary(operand.toString(),operand.getType());
      default:
        return braceIfNecessary(left.toString(),left.getType()) + " "
          + RTSimGlobals.typeToLiteral(type) + " "
          + braceIfNecessary(right.toString(),right.getType());
    }
  }
  
  public String toString2(int size) {
	  switch(type) {
      case RTSimGlobals.NUM_CONST:
        return (base==RTSimGlobals.BASE_BIN?"%":(base==RTSimGlobals.BASE_HEX?
                  "$":""))
            + RTSimGlobals.boolArray2String(numConst.toBoolArray(size),base);
      case RTSimGlobals.BIT_SEQ:
        return bitSequence.toString();
      case RTSimGlobals.NOT:
        return RTSimGlobals.typeToLiteral(type) + " "
          + braceIfNecessary(operand.toString(),operand.getType());
      case RTSimGlobals.SIGN:
        return RTSimGlobals.typeToLiteral(type)
          + braceIfNecessary(operand.toString(),operand.getType());
      default:
        return braceIfNecessary(left.toString(),left.getType()) + " "
          + RTSimGlobals.typeToLiteral(type) + " "
          + braceIfNecessary(right.toString(),right.getType());
    }
  }

  public boolean equals(Object o) {
    if(!(o instanceof Expression)) return false;
    Expression e = (Expression) o;
    if(type != e.type) return false;      
    switch(type) {
      case RTSimGlobals.NUM_CONST:
        return numConst.equals(e.numConst);
      case RTSimGlobals.BIT_SEQ:
        return bitSequence.equals(e.bitSequence);
      case RTSimGlobals.NOT:
      case RTSimGlobals.SIGN:
        return operand.equals(e.operand);
      default:
        return left.equals(e.left) && right.equals(e.right);
    }
  }

  public int hashCode() { return width*100 + type; }

  public void calculateSignals(SignalsData signalsData) {
    switch(type) {
    case RTSimGlobals.NOT:
      operand.calculateSignals(signalsData);
      return;
    case RTSimGlobals.AND:
    case RTSimGlobals.NAND:
    case RTSimGlobals.OR:
    case RTSimGlobals.NOR:
    case RTSimGlobals.XOR:
      left.calculateSignals(signalsData);
      right.calculateSignals(signalsData);
      return;
    default:
      signalsData.insertExpression(this);
    }
  }

  public Expression getMinimizedExpression() {
    // not really implemented yet
    return this;
  }

  public void setInputSignal(int ti) { inputSignal = ti; }

  public int getInputSignal() { return inputSignal; }

  public ExpressionInputBundle getInputBundle() {
    ExpressionInputBundle back = new ExpressionInputBundle();
    bundleInputs(back);
    return back;
  }

  public void bundleInputs(ExpressionInputBundle bundle) {
    switch(type) {
      case RTSimGlobals.BIT_SEQ: bitSequence.bundleInputs(bundle); break;
      case RTSimGlobals.NOT: 
      case RTSimGlobals.SIGN: operand.bundleInputs(bundle); break;
      case RTSimGlobals.PLUS: 
      case RTSimGlobals.MINUS:
      case RTSimGlobals.LT:
      case RTSimGlobals.LE:
      case RTSimGlobals.GT:
      case RTSimGlobals.GE:
      case RTSimGlobals.EQ:
      case RTSimGlobals.NE:
      case RTSimGlobals.AND:
      case RTSimGlobals.NAND:
      case RTSimGlobals.OR:
      case RTSimGlobals.NOR:
      case RTSimGlobals.XOR: left.bundleInputs(bundle); right.bundleInputs(bundle); break;
      default: break;
    }
  }

    public void emitCircuit(String indent, PrintWriter out, String circuitName,Statement st) {
	emitCircuit(indent,out,circuitName,0,st);
    }

  public void emitCircuit(String indent, PrintWriter out, String circuitName, int toWidth, Statement st) {
    ExpressionInputBundle bundle = getInputBundle();
    int uwidth = width>toWidth?width:toWidth;
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println("USE ieee.std_logic_unsigned.ALL;");
    out.println("USE work.rteasy_functions.ALL;");
    out.println();
    out.println(indent+"ENTITY "+circuitName+" IS");
    out.println(indent+"  PORT(");
    bundle.emitPortDeclarations(indent+"    ",out);
    if(st !=null && st.left.containsRegArray() && !st.getRight().containsRegArray()){
    		RegisterArray ra = (RegisterArray) st.left.bitReferences.get(1);
//    		Register r = ra.getReference();
//    		RegBusReference f = new RegBusReference(r,r.offset,r.offset+r.width-1);
//    		String s = f.bitRange.toVHDLArray();
//      	    out.println(indent+"    "+f.toVHDLPortDecl("IN",false,null)+";");
//    		out.println(indent+"    OUTPUT : OUT reg_array"+s);
    } else {
    	out.println(indent+"    OUTPUT : OUT std_logic_vector("+(uwidth-1)+" DOWNTO 0)");
    }
    out.println(indent+"  );");
    out.println(indent+"END "+circuitName+";");
    out.println();
    out.println(indent+"ARCHITECTURE primitive OF "+circuitName+" IS");
    /*
    if(containsComparison()) {
	out.println(indent+"  FUNCTION bool2slv (b : boolean) RETURN std_logic_vector IS");
	out.println(indent+"  BEGIN IF b THEN RETURN \"1\"; ELSE RETURN \"0\"; END IF; END bool2slv;");
    }*/
    if(bundle.hasRegArrays()){
    	LinkedList<RegBus> l = bundle.getRefReg();
    	for(int i=0;i<l.size();i++){
    		RegBusReference r = new RegBusReference(l.get(i),l.get(i).offset,l.get(i).offset+l.get(i).width-1);
    		String s = r.bitRange.toVHDLArray();
    		out.println(indent+"TYPE reg_array"+s+" IS ARRAY(0 TO "+(int)(Math.pow(2,(l.get(i).width))-1)+") OF "+bundle.getArrayWidth(i).toVHDLType(false,null)+";");
    	}
    } else if(st != null && st.left.containsRegArray()){
//    	RegisterArray ra = (RegisterArray) st.left.bitReferences.get(1);
//		Register r = ra.getReference();
//		RegBusReference f = new RegBusReference(r,r.offset,r.offset+r.width-1);
//		String s = f.bitRange.toVHDLArray();
//		out.println(indent+"TYPE reg_array"+s+" IS ARRAY(0 TO "+(int)(Math.pow(2,(r.width))-1)+") OF "+ra.getBitRange().toVHDLType(false,null)+";");
    }
    out.println(indent+"BEGIN");
    out.println(indent+"  -- "+toString());
    if(st != null && st.left.containsRegArray() && !st.getRight().containsRegArray){
    		out.println(indent+"  OUTPUT"+st.left.toVHDLlval()+" <= "+toVHDL(bundle,uwidth)+";");
    } else {
    	out.println(indent+"  OUTPUT <= "+toVHDL(bundle,uwidth)+";");
    }
    out.println(indent+"END primitive;");
  }

    public void emitCircuitComponent(String indent, PrintWriter out, String circuitName) {
	emitCircuitComponent(indent,out,circuitName,0,null);
    }

  public void emitCircuitComponent(String indent, PrintWriter out, String circuitName, int toWidth, Statement st) {
    ExpressionInputBundle bundle = getInputBundle();
    int uwidth = width>toWidth?width:toWidth;
    out.println(indent+"COMPONENT "+circuitName);
    out.println(indent+"  PORT(");
    bundle.emitPortDeclarations(indent+"    ",out);
    if(st !=null && st.left.containsRegArray() && !st.getRight().containsRegArray()){
		RegisterArray ra = (RegisterArray) st.left.bitReferences.get(1);
//		Register r = ra.getReference();
//		RegBusReference f = new RegBusReference(r,r.offset,r.offset+r.width-1);
//		String s = f.bitRange.toVHDLArray();
//  	    out.println(indent+"    "+f.toVHDLPortDecl("IN",false,null)+";");
//		out.println(indent+"    OUTPUT : OUT reg_array"+s);
} else {
	out.println(indent+"    OUTPUT : OUT std_logic_vector("+(uwidth-1)+" DOWNTO 0)");
}
    out.println(indent+"  );");
    out.println(indent+"END COMPONENT;");
    out.println();
    out.println(indent+"FOR ALL : "+circuitName+" USE ENTITY WORK."+circuitName+"(primitive);");
  }

    public void emitOutputSignalDeclaration(String indent, PrintWriter out,
					    String outputSignalName) {
	emitOutputSignalDeclaration(indent,out,outputSignalName,0);
    }

  public void emitOutputSignalDeclaration(String indent, PrintWriter out,
                                          String outputSignalName, int toWidth) {
    int uwidth = width>toWidth?width:toWidth;
    out.println(indent+"SIGNAL "+outputSignalName+" : std_logic_vector("+(uwidth-1)+" DOWNTO 0);");
  }

  public void emitInstantiation(String indent, PrintWriter out,
           String circuitName, String instanceName, String outputSignalName) {
    ExpressionInputBundle bundle = getInputBundle();
    out.println(indent+instanceName+": "+circuitName);
    out.println(indent+"  PORT MAP(");
    bundle.emitPortMap(indent+"    ",out);
    out.println(indent+"    OUTPUT => "+outputSignalName+");");
  }
  
  public void emitConditionInstantiation(String indent, PrintWriter out,
           String circuitName, String instanceName, int conditionSignal) {
    ExpressionInputBundle bundle = getInputBundle();
    out.println(indent+instanceName+": "+circuitName);
    out.println(indent+"  PORT MAP(");
    if(RTOptions.timing == RTOptions.TIMING_1EDGE)
      bundle.emitPortMap(indent+"    ",out,"in");
    else
      bundle.emitPortMap(indent+"    ",out);
    out.println(indent+"    OUTPUT => I"+conditionSignal+");");
  } 
}














