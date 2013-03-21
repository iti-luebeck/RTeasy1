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

