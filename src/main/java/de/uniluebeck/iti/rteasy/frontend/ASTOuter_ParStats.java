package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.Label;

public class ASTOuter_ParStats extends RTSimNode {
  private int statement_node_type = RTSimGlobals.ERR;
  private boolean hasNext = false;
  private boolean hasLabel = false;
  private String labelId = null;
  private Label label = null;

  public ASTOuter_ParStats(int id) {super(id); }

  public void setLabel(Label l) { label = l; }
  public Label getLabel() { return label; }
  public void setLabelId(String s) { labelId = s; hasLabel = true; }
  public boolean hasLabel() { return hasLabel; }
  public String getLabelId() { return labelId; }
  public void setStatNodeType(int nt) { statement_node_type = nt; }
  public int getStatNodeType() { return statement_node_type; }
  public void setHasNext(boolean b) { hasNext = b; }
  public boolean hasNext() { return hasNext; }
  public ASTIf_Stat getIfStatement() { return (ASTIf_Stat) jjtGetChild(0); }
  public ASTStat getStatement() { return (ASTStat) jjtGetChild(0); }
  public ASTSwitch_Case_Stat getSwitchStatement() {return (ASTSwitch_Case_Stat) jjtGetChild(0); }
  public ASTOuter_ParStats next() {
    if(hasNext) return (ASTOuter_ParStats) jjtGetChild(1);
    else return null;
  }
}
