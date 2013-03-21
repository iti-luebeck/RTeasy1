package de.uniluebeck.iti.rteasy;

public class CircuitPort {
  public String circuitName, portName;
  public int circuitId;

  CircuitPort(String toName, String toPortName, int toId) {
    circuitName = toName;
    portName = toPortName;
    circuitId = toId;
  }

  public String toSignalName() {
    if(portName.equals("")) return circuitName;
    else return circuitName+"_"+portName;
  }

  public String toSignalName(String port) {
    if(port == null || port.equals("")) return toSignalName();
    else return circuitName+"_"+port;
  }

  public boolean equals(Object o) {
    CircuitPort cp;
    if(o instanceof CircuitPort) {
      cp = (CircuitPort) o;
      return circuitName.equals(cp.circuitName) && portName.equals(cp.portName) && circuitId == cp.circuitId;
    }
    else return false;
  }

  public int hashCode() {
    return toSignalName().hashCode();
  }
}
