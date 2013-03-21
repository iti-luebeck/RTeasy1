package de.uniluebeck.iti.rteasy.frontend;

public class ASTDecls extends RTSimNode {
  private boolean hasNext;

  public ASTDecls(int id) {super(id);}

  public void setHasNext(boolean b) { hasNext = b;}
  public boolean hasNext() { return hasNext; }
  public ASTDecls next() { return (ASTDecls) jjtGetChild(1); }
  public ASTDecl getDeclaration() { return (ASTDecl) jjtGetChild(0); }
  public String toString() {
    return "Decls: "+rangeStr;
  }
}
