package de.uniluebeck.iti.rteasy.frontend;

public class ASTRegArrayDeclList extends RTSimNode {
	private boolean hasNext = false;
	
	public ASTRegArrayDeclList(int id) {
    super(id);
  }

  public void setHasNext(boolean b) {
	  hasNext = b;
  }
  public boolean hasNext() {
	  return hasNext;
  }
  public ASTRegArrayDeclList next() {
	  if(hasNext) return (ASTRegArrayDeclList) jjtGetChild(1);
	    else return null;
  }
  public ASTRegArrayDecl getRegArDecl() { return (ASTRegArrayDecl) jjtGetChild(0); }
}