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
