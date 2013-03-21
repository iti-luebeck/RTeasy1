package de.uniluebeck.iti.rteasy.frontend;

public class ASTRtProg extends RTSimNode {
  private String name = null;
  private boolean nameSet = false;
  
  public ASTRtProg(int id) {super(id);}

  public void setName(String s) { name = s; nameSet = true; }
  public String getName() { return name; }
  
  /**
   * @return true, if a name was set using setName() as result
   * of a "component <NAME>;" directive
   */
  public boolean nameSet() { return nameSet; }

  public ASTDecls getDeclarations() {
    return (ASTDecls) jjtGetChild(0);
  }
  public ASTStat_Seq getStatementSequence() {
    return (ASTStat_Seq) jjtGetChild(1);
  }
}
