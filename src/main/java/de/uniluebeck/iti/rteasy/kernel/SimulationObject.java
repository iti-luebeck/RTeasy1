package de.uniluebeck.iti.rteasy.kernel;
import de.uniluebeck.iti.rteasy.PositionRange;

public abstract class SimulationObject {
  protected String idStr;
  protected PositionRange pr;

  SimulationObject(String s, PositionRange tpr) {
    idStr = s;
    pr = tpr;
  }

  public String getIdStr() { return idStr; }
  public abstract String getVHDLName();
  public PositionRange getPositionRange() { return pr; }
  public boolean equals(Object o) { return o == this; }
}
