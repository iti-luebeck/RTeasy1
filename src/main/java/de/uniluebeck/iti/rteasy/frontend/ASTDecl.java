package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.RTSimGlobals;

public class ASTDecl extends RTSimNode {
  private int declaration_type = RTSimGlobals.ERR;
  private int signalDirection = RTSimGlobals.UNUSED;

  public ASTDecl(int id) {super(id);}
  
  public void setDeclType(int dt) { declaration_type = dt; }
  public int getDeclType() { return declaration_type;}
  public void setSignalDirection(int i) { signalDirection = i; }
  public int getSignalDirection() { return signalDirection; }
  public ASTRegBusDeclList getRegisterDecls() { return (ASTRegBusDeclList) jjtGetChild(0); }
  public ASTRegBusDeclList getBusDecls() { return (ASTRegBusDeclList) jjtGetChild(0); }
  public ASTMemDeclList getMemoryDecls() { return (ASTMemDeclList) jjtGetChild(0); }
  public ASTRegArrayDeclList getRegArrayDecls() { return (ASTRegArrayDeclList) jjtGetChild(0);}
}
