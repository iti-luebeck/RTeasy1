package de.uniluebeck.iti.rteasy.frontend;

public class ASTRegBusDeclList extends RTSimNode {
  private boolean hasNext = false;

  public ASTRegBusDeclList(int id) {super(id);}

  public void setHasNext(boolean b) { hasNext = b; }
  public boolean hasNext() { return hasNext; }
  public ASTRegBusDeclList next() {
    if(hasNext) return (ASTRegBusDeclList) jjtGetChild(1);
    else return null;
  }
  public ASTRegBusDecl getRegBusDecl() { return (ASTRegBusDecl) jjtGetChild(0);}

}
