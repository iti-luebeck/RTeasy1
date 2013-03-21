package de.uniluebeck.iti.rteasy.frontend;

public class ASTStat_Seq extends RTSimNode {
  private boolean hasNext = false;
  private boolean has2Edges = false;

  public ASTStat_Seq(int id) {super(id);}

  public void setHasNext(boolean b) { hasNext = b; }
  public boolean hasNext() { return hasNext; }
  public void setHas2Edges(boolean b) { has2Edges = b; }
  public boolean has2Edges() { return has2Edges; }
  public ASTOuter_ParStats getStatements() { return (ASTOuter_ParStats) jjtGetChild(0); }
  public ASTOuter_ParStats getStatements2() { return (ASTOuter_ParStats) jjtGetChild(1); }
  public ASTStat_Seq next() {
    if(hasNext)
      if(has2Edges) return (ASTStat_Seq) jjtGetChild(2);
      else return (ASTStat_Seq) jjtGetChild(1);
    else return null;
  }
  public String toString() {
    return "Stat_Seq: "+rangeStr;
  }
}
