package de.uniluebeck.iti.rteasy.frontend;

public class ASTIf_Stat extends RTSimNode {
  private boolean hasElse = false;

  public ASTIf_Stat(int id) { super(id); }
  
  public void setHasElse(boolean b) { hasElse = b; }
  public boolean hasElse() { return hasElse; }
  public ASTExpr getExpression() { return (ASTExpr) jjtGetChild(0); }
  public ASTInner_ParStats getThen() { return (ASTInner_ParStats) jjtGetChild(1); }
  public ASTInner_ParStats getElse() {
    if(hasElse) return (ASTInner_ParStats) jjtGetChild(2);
    else return null;
  }
}
