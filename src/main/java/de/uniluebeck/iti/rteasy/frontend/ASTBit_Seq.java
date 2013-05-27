/*
 * Copyright (c) 2003-2013, University of Luebeck, Institute of Computer Engineering
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Luebeck, the Institute of Computer
 *       Engineering nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE UNIVERSITY OF LUEBECK OR THE INSTITUTE OF COMPUTER
 * ENGINEERING BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


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
