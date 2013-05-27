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


package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.BitVector;

public class ASTExpr extends RTSimNode {
  private int operator_type;
  private int no_operands;
  private BitVector bv;
  private int bitSeqType = 0;
  private int width = 0;

  public ASTExpr(int id) {
    super(id);
  }

  public ASTExpr getLeft() { return (ASTExpr) jjtGetChild(0); }
  public ASTExpr getRight() { return (ASTExpr) jjtGetChild(1); }
  public ASTExpr getOperand() { return (ASTExpr) jjtGetChild(0); }
  public ASTBit_Seq getBitSeq() { return (ASTBit_Seq) jjtGetChild(0); }
  public BitVector getNumConst() { return ((ASTNum_Const)jjtGetChild(0)).getBitVector(); }
  public int getBase() { return ((ASTNum_Const) jjtGetChild(0)).getBase(); }
  public int getWidth() { return width;}
  public void setWidth(int w) { width = w; }
  public int getType() { return operator_type; }
  public int getNoOperands() { return no_operands; }
  public void setBitSeqType(int t) { bitSeqType = t; }
  public int getBitSeqType() { return bitSeqType; }  
  public boolean containsBus() {
    switch(operator_type) {
    case RTSimGlobals.NUM_CONST: return false;
    case RTSimGlobals.BIT_SEQ: return getBitSeq().containsBus();
    default: if(no_operands == 1) return getOperand().containsBus();
    else if(no_operands == 2) return getLeft().containsBus() ||
				getRight().containsBus();
    else return false;
    }
  }
  public void setOp(int ot) {
    switch(ot) {
      case RTSimGlobals.NUM_CONST:
      case RTSimGlobals.BIT_SEQ:
	no_operands = 1;
	operator_type = ot;
	break;
      case RTSimGlobals.SIGN:
      case RTSimGlobals.NOT:
        no_operands = 1;
	operator_type = ot;
	break;
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
      case RTSimGlobals.XOR:
        no_operands = 2;
	operator_type = ot;
	break;
      default:
        System.out.println("ASTExpr.setOp - wrong operator type with no. "+ot);
	no_operands = 0;
	operator_type = RTSimGlobals.ERR;
    }
  }

  public String toString() {
    return "Expr: no_operands=" + no_operands + " operator_type=" + RTSimGlobals.typeToString(operator_type);
  }
}
