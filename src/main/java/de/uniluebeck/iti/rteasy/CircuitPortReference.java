package de.uniluebeck.iti.rteasy;
import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.Statement;


public class CircuitPortReference {
  public BitRange bitRange;
  public CircuitPort circuitPort;

  public CircuitPortReference(String toCircuitName,
                       String toCircuitPortName, int toId, BitRange br) {
    circuitPort = new CircuitPort(toCircuitName, toCircuitPortName, toId);
    bitRange = br;
  }

  public CircuitPortReference(String toCircuitName,
                       String toCircuitPortName, int toId, int begin, int end) {
    circuitPort = new CircuitPort(toCircuitName, toCircuitPortName, toId);
    bitRange = new BitRange(begin,end);
  }

  public CircuitPortReference(CircuitPort toCircuitPort, BitRange br) {
    circuitPort = toCircuitPort; bitRange = br;
  }

  public CircuitPortReference(CircuitPort toCircuitPort, int begin, int end) {
    circuitPort = toCircuitPort; bitRange = new BitRange(begin,end);
  }

  public String toVHDLPortName() {
    String back = circuitPort.circuitName;
    if(!circuitPort.portName.equals("")) back += "_"+circuitPort.portName;
    back += "_"+bitRange.begin+"_"+bitRange.end;
    return back;
  }

  public String toVHDLPortDecl(String inout, boolean ra, BitRange b) {
    return toVHDLPortName() + " : " + inout + " " + bitRange.toVHDLType(ra,b);
  }

  public String toVHDLPortRval() {
    return toVHDLPortName()+bitRange.toVHDL();
  }

  public String getVHDLPortMap() {
    return toVHDLPortName() + " => " + circuitPort.toSignalName()+bitRange.toVHDL();
  }

  public String getVHDLPortMap(String port) {
    return toVHDLPortName() + " => " + circuitPort.toSignalName(port)+bitRange.toVHDL();
  }

  public String getVHDLPortMap(SignalsData signalsData) {
    Statement st = signalsData.getStatementByControlSignal(circuitPort.circuitId);
    if(st.getStatementType() == RTSimGlobals.READ)
      return toVHDLPortName() + " => "+st.getMemory().getVHDLName()+"_data_out"+bitRange.toVHDL();
    else
      return toVHDLPortName() + " => " + circuitPort.toSignalName()+bitRange.toVHDL();
  }
}







