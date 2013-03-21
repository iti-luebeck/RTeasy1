package de.uniluebeck.iti.rteasy.frontend;

public class ASTMemDeclList extends RTSimNode {
  private boolean hasNext = false;

  public ASTMemDeclList(int id) {super(id);}

  public void setHasNext(boolean b) {hasNext = b;}
  public boolean hasNext() { return hasNext;}
  public ASTMemDeclList next() {
    if(hasNext) return (ASTMemDeclList) jjtGetChild(1);
    else return null;
  }
  public ASTMemDecl getMemDecl() { return (ASTMemDecl) jjtGetChild(0); }
}
