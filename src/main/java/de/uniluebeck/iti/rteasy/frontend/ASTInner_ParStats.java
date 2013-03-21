package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.RTSimGlobals;

public class ASTInner_ParStats extends RTSimNode {
  private int statement_node_type = RTSimGlobals.ERR;
  private boolean hasNext = false;

  public ASTInner_ParStats(int id) {super(id); }

  public void setStatNodeType(int nt) { statement_node_type = nt; }
  public int getStatNodeType() { return statement_node_type; }
  public void setHasNext(boolean b) { hasNext = b; }
  public boolean hasNext() { return hasNext; }
  public ASTIf_Stat getIfStatement() { return (ASTIf_Stat) jjtGetChild(0); }
  public ASTStat getStatement() { return (ASTStat) jjtGetChild(0); }
  public ASTSwitch_Case_Stat getSwitchStatement() { return (ASTSwitch_Case_Stat) jjtGetChild(0); }
  public ASTInner_ParStats next() {
    if(hasNext) return (ASTInner_ParStats) jjtGetChild(1);
    else return null;
  }
}
