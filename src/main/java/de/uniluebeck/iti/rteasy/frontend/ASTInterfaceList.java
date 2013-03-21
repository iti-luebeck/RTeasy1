package de.uniluebeck.iti.rteasy.frontend;

public class ASTInterfaceList extends RTSimNode {
  private boolean hasNext = false;
  private int signalDirection = -1;

  public ASTInterfaceList(int id) { super(id); }

  public void setHasNext(boolean b) { hasNext = b;}
  public boolean hasNext() { return hasNext; }
  public void setSignalDirection(int i) { signalDirection = i; }
  public int getSignalDirection() { return signalDirection; }
  public ASTRegBusDeclList getDeclList() { return
					     (ASTRegBusDeclList)
					     jjtGetChild(0); }
  public ASTInterfaceList next() { return (ASTInterfaceList)
				     jjtGetChild(1); }
 
}


  
