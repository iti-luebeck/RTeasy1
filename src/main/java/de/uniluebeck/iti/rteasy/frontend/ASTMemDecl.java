package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.kernel.Memory;

public class ASTMemDecl extends RTSimNode {
  private String name, addrReg, dataReg;
  private Memory memory = null;  

  public ASTMemDecl(int id) {super(id);}

  public void setName(String s) {name = s;}
  public String getName() { return name;}
  public void setAddrReg(String s) { addrReg = s;}
  public String getAddrReg() { return addrReg; }
  public void setDataReg(String s) { dataReg = s; }
  public String getDataReg() { return dataReg; }
  public void setMemory(Memory m) { memory = m; }
  public Memory getMemory() { return memory; }

  public String toString() {
    return "memory "+name+" AddrReg: "+addrReg+" DataReg: "+dataReg;
  }
}
