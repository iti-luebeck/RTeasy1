package de.uniluebeck.iti.rteasy.kernel;
import de.uniluebeck.iti.rteasy.CircuitPortReference;

public class RegBusReference extends CircuitPortReference {
    public RegBus regBus;

  RegBusReference(RegBus rb, BitRange br) {
    super(rb.getVHDLName(),(rb instanceof Bus?"":"out"),rb.getRegBusId(),br);
    regBus = rb;
  }

  RegBusReference(RegBus rb, int begin, int end) {
    super(rb.getVHDLName(),(rb instanceof Bus?"":"out"),rb.getRegBusId(),new BitRange(begin,end,rb.getDirection()));
    regBus = rb;
  }

  RegBusReference(Object[] entry) {
    super(((RegBus) entry[0]).getVHDLName(),(entry[0] instanceof Bus?"":"out"),((RegBus) entry[0]).getRegBusId(),
      new BitRange(((Integer) entry[1]).intValue(),
		   ((Integer) entry[2]).intValue(),((RegBus) entry[0]).getDirection()));
    regBus = (RegBus) entry[0];
  }

  public String toVHDL() {
    return regBus.getVHDLName()+bitRange.toVHDL();
  }

  public String getVHDLPortMap(String port) {
    if(regBus instanceof Bus) return super.getVHDLPortMap();
    else return super.getVHDLPortMap(port);
  }
}


