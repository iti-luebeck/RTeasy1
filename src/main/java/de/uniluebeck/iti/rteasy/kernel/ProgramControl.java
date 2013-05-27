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


package de.uniluebeck.iti.rteasy.kernel;
import de.uniluebeck.iti.rteasy.PositionRange;

public class ProgramControl {
  private int statSeqIndex = 0;
  private int parStatsIndex = 0;
  private PositionRange errorPos = null;
  private String errorMsg = "no error";
  private boolean hasError = false;
  private boolean changed = false;
  private int cycleCount = 0;
  private RTProgram rtprog;

  ProgramControl(RTProgram rt) { rtprog = rt; }

  public RTProgram getRTProgram(){return rtprog;}
  public void reset() {
    cycleCount = 0;
    statSeqIndex = 0;
    parStatsIndex = 0;
    errorPos = null;
    errorMsg = "no error";
    hasError = false;
    changed = false;
  }

  public boolean setPosition(int seqI, int statsI) {
    if(!changed) {
      statSeqIndex = seqI;
      parStatsIndex = statsI;
      changed = true;
      return true;
    }
    else return false;
  }
  public boolean performGoto(Label l) {
    return setPosition(l.getStatSeqEntry(),l.getParStatsEntry());
  }
  public void clearChanged() { changed = false; }
  public int getStatSeqIndex() { return statSeqIndex; }
  public int getParStatsIndex() { return parStatsIndex; }
  public void inc() {
    statSeqIndex++;
    parStatsIndex = 0;
  }
  public void raiseRuntimeError(String msg, PositionRange tpr) {
    errorMsg = msg;
    errorPos = tpr;
    hasError = true;
  }
  public PositionRange getErrorPosition() { return errorPos; }
  public String getErrorMessage() { return errorMsg; }
  public boolean hasRuntimeError() { return hasError; } 
  public void commit() {
    if(!changed) inc();
    else changed = false;
    cycleCount++;
  }
  public int getCycleCount() { return cycleCount; }

  /**
   * reicht das EndLabel von RTProgram weiter
   */
  public Label getEndLabel() { return rtprog.getEndLabel(); }
}

