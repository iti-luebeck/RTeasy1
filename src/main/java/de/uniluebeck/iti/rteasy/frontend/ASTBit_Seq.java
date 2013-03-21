package de.uniluebeck.iti.rteasy.frontend;

import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.Bus;
import de.uniluebeck.iti.rteasy.kernel.SimulationObject;

public class ASTBit_Seq extends RTSimNode {
  private String targetId;
  private BitRange br = null;
  private boolean hasNext = false;
  private SimulationObject targetRef = null;
  private int registerNumber = 0;
  //Enth�lt die Position des Zeigers 
  private boolean hasReferenceRegister;

  public ASTBit_Seq(int id) {
    super(id);
    br = null;
    hasNext = false;
    registerNumber = 0;
  }

 

public boolean containsBus() {
    if(targetRef != null && targetRef instanceof Bus) return true;
    if(hasNext) return next().containsBus();
    else return false;
  }

 

public boolean containsInBus() {
    if(targetRef != null && targetRef instanceof Bus
       && ((Bus) targetRef).incoming()) return true;
    if(hasNext) return next().containsInBus();
    else return false;
  }

 

public void setRef(SimulationObject so) { targetRef = so; }
public SimulationObject getRef() { return targetRef; }
public void setTargetId(String s) { targetId = s; }
public void setBitRange(BitRange tbr) { br = tbr; }
public void setHasNext(boolean b) { hasNext = b; } 
public String getTargetId() { return targetId; }
public BitRange getBitRange() { return br; }
public boolean allBits() { return br == null; }
public boolean hasNext() { return hasNext; }
public ASTBit_Seq next() { if(hasNext) return (ASTBit_Seq) jjtGetChild(0); else return null; }
public void setRegisterNumber(int regnum){ registerNumber = regnum;}
public int getRegisterNumber(){return registerNumber;}
public void setHasReferenceRegister(boolean b){hasReferenceRegister = b;}
public boolean hasReferenceRegister(){return hasReferenceRegister;}
public ASTBit_Seq getReferenceRegister(){
	if(hasReferenceRegister) return (ASTBit_Seq) jjtGetChild(0); 
	else return null;}
}
