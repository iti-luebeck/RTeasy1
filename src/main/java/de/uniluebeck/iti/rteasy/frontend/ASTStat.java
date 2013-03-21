package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.Label;
import de.uniluebeck.iti.rteasy.kernel.Memory;

public class ASTStat extends RTSimNode {
  private int statement_type = RTSimGlobals.ERR;
  private String idStr = null;
  private Label label = null;
  private Memory memory = null;
  private boolean busOnLeftSide = false;
  private boolean isGotoEnd = false;
 
  public ASTStat(int id) { super(id); }

  public void setStatementType(int i) { statement_type = i; }
  public int getStatementType() { return statement_type; }
  public void setBusOnLeftSide(boolean b) { busOnLeftSide = b; }
  public boolean hasBusOnLeftSide() { return busOnLeftSide; }
  public void setIdStr(String s) { idStr = s; }
  public String getLabelId() { return idStr; }
  public String getMemoryId() { return idStr; }
  public ASTBit_Seq getBitSequence() { return (ASTBit_Seq) jjtGetChild(0); }
  public ASTExpr getExpression() { return (ASTExpr) jjtGetChild(1); }
  public void setLabel(Label l) { label = l; }
  public Label getLabel() { return label; }
  public void setMemory(Memory m) { memory = m; }
  public Memory getMemory() { return memory; }
  public void setGotoEnd() { isGotoEnd = true; }
  public boolean isGotoEnd() { return isGotoEnd; }
  public String toString() {
    String back =  "Stat: type=" + RTSimGlobals.typeToString(statement_type);
    if(statement_type == RTSimGlobals.GOTO) back+= " label="+idStr;
    else if(statement_type == RTSimGlobals.READ || statement_type == RTSimGlobals.WRITE)
      back += " memory="+idStr;
    return back;
  }
}
