package de.uniluebeck.iti.rteasy.kernel;
import de.uniluebeck.iti.rteasy.PositionRange;

public class Label extends SimulationObject {
  private int statSeqEntry, parStatsEntry;

  Label(String s, PositionRange tpr) {
    super(s,tpr);
  }

  public void setEntry(int seqE, int statsE) {
    statSeqEntry = seqE;
    parStatsEntry = statsE;
  }

  public void insertUpdate(int insertIndex) {
    if(insertIndex <= statSeqEntry) statSeqEntry++;
  }
  public int getStatSeqEntry() { return statSeqEntry; }
  public int getParStatsEntry() { return parStatsEntry; }
  public String toString() { return getIdStr()+" => ("+statSeqEntry+
                             ","+parStatsEntry+")"; }
  public String infoStr() {
    return "Label "+getIdStr()+" statSeqIdx="+statSeqEntry
      +" parStatsIdx="+parStatsEntry+" Pos: "+getPositionRange().toString();
  }

  public boolean equals(Object o) {
    if(o instanceof Label) return statSeqEntry == ((Label) o).getStatSeqEntry()
			    && parStatsEntry == ((Label) o).getParStatsEntry();
    else return false;
  }

  /**
   * @return statSeqEntry (is only used after RTProgram.performTransformations() was called)
   */
  public int hashCode() { return statSeqEntry; }

  public String getVHDLName() { return "label_"+getIdStr(); }
}
