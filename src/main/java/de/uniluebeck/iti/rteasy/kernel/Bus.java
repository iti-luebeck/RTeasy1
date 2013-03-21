package de.uniluebeck.iti.rteasy.kernel;

import java.io.PrintWriter;
import java.util.*;


import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.SignalsData.DriverEntry;
import de.uniluebeck.iti.rteasy.frontend.ASTRegBusDecl;
import de.uniluebeck.iti.rteasy.gui.RTOptions;

public class Bus extends RegBus {
  private boolean content[];
  private boolean written[];
  private boolean show_content[];
  private boolean incoming = false;
  private boolean outgoing = false;
  private String vhdlId;
  private HashSet driversByBit[];
  private boolean controlSignalDrivesBus[];
  private boolean driversSet = false;
  private String vhdlZeroDriverCondition[];
  private String driverAssignment[][];
  private boolean hasDrivers = false; // true if RT op drive bus

  public Bus(ASTRegBusDecl busdecl) {
    super(busdecl.getName(),busdecl.getPositionRange(),busdecl.getWidth(),
	  busdecl.getOffset(),busdecl.getDirection());
    vhdlId = busdecl.getName();
    if(RTSimGlobals.isReservedWord(vhdlId))
      vhdlId = "x_"+vhdlId;
    content = new boolean[width];
    written = new boolean[width];
    show_content = new boolean[width];
    for(int i=0;i<width;i++) {
      content[i] = false;
      written[i] = false;
      show_content[i] = false;
    }
  }

  public void setIncoming(boolean b) { incoming = b; }
  public boolean incoming() { return incoming; }
  public void setOutgoing(boolean b) { outgoing = b; }
  public boolean outgoing() { return outgoing; }

  public String toString() { return getPrettyDecl(); }

  public BitVector getContent() {
    String nval = "";
    for(int i=0;i<width;i++) nval = (content[i]?"1":"0")+nval;
    return new BitVector(nval);
  }

  public BitSet getContentBitSet() {
    BitSet bs = new BitSet();
    for(int i=0;i<width;i++) if(content[i]) bs.set(i);
    return bs;
  }

  public void setContent(BitVector bv) {
    for(int i=0;i<width;i++) {
      content[i] = bv.get(i);
      written[i] = true;
    } 
  }

  public void setContent(BitSet bs) {
    for(int i=0;i<width;i++) {
      content[i] = bs.get(i);
      written[i] = true;
    } 
  }

  public void editContent(BitVector bv) {
    for(int i=0;i<width;i++) {
      content[i] = bv.get(i);
      show_content[i] =content[i];
    }
  }

  public void editContent(BitSet bs) {
    for(int i=0;i<width;i++) {
      content[i] = bs.get(i);
      show_content[i] = content[i];
    }
  }

  public void editContent(boolean bits[]) {
    if(bits.length != width) return;
    for(int i=0;i<width;i++) {
      content[i] = bits[i];
      show_content[i] = content[i];
    }
  }

  public boolean get(int i) {
    i -= offset;
    if(i<width&&i>=0) 
      if(direction) return content[i];
      else return content[width-i-1];
    else return false;
  }

  public boolean set(int i, boolean b) {
    i -= offset;
    if(!direction) i = width-i-1;
    if(!written[i]) { 
      if(i<width&&i>=0) {
        content[i] = b; 
        written[i] = true;
      }
      return true;
    }
    else return false;
  }

  public void clear() {
    for(int i=0;i<width;i++) {
      show_content[i] = content[i];
      content[i] = false; 
      written[i] = false;
    }
  }

  public String getVHDLPortName() {
    return vhdlId;
  }

  public void emitVHDLPortDeclaration(String indent, PrintWriter out) {
    if(incoming) 
      out.print(indent+getVHDLPortName()+" : IN  "+getVHDLType());
    else if(outgoing)
      out.print(indent+getVHDLPortName()+" : OUT "+getVHDLType());
  }

  public void emitVHDLPortSignalDeclaration(String indent, PrintWriter out) {
    if(! (incoming || outgoing)) return;
    out.println(indent+"SIGNAL "+getVHDLPortName()+" : " + getVHDLType()
		+ " := (OTHERS => 'L');");
  }

  public void emitVHDLPortMap(String indent, PrintWriter out) {
    if(! (incoming || outgoing)) return;
    out.print(indent+getVHDLPortName()+" => "+getVHDLPortName());
  }

  public void emitVHDLSignalDeclaration(String indent, PrintWriter out) {
      out.println(indent+"SIGNAL "+getVHDLName()+" : "+getVHDLType()+";");
      if(incoming && RTOptions.forceInputs && RTOptions.timing == RTOptions.TIMING_1EDGE)
        out.println(indent+"SIGNAL "+getVHDLName()+"_buf : "+getVHDLType()+";");
  }

  public void emitVHDLSignalAssignment(String indent, PrintWriter out) {
    out.println(indent+getVHDLName()+" <= (OTHERS => 'L');");
  }

  public String getVHDLName() {
    return "bus_"+getIdStr();
  }

  public void setDrivers(SignalsData signalsData) {
    int i,j;
    Iterator it;
    SignalsData.DriverEntry de;
    int controlSignalCount = signalsData.getControlSignalCount();
    HashSet driverSet;
    Statement s;
    HashSet toDriversByBit[] = signalsData.getDriversByRbid(getRegBusId());

    // check if toDriversByBit[] matches bus width
    if(toDriversByBit.length != width) {
      System.err.println("INTERNAL ERROR -- <bus "+getIdStr()+">.setDrivers: toDriversByBit.length ("+toDriversByBit.length+") does not match bus width ("+width+") !");
      return;
    }
    driversSet = true;
    // Java initializes arrays (boolean = false)
    controlSignalDrivesBus = new boolean[controlSignalCount];
    driversByBit = toDriversByBit;
    vhdlZeroDriverCondition = new String[width];
    driverAssignment = new String[controlSignalCount][width];
    for(i=0;i<width;i++) if(driversByBit[i] != null) {
      hasDrivers = true;   // set flag
      driverSet = driversByBit[i];
      for(it=driverSet.iterator();it.hasNext();) {
	de = (SignalsData.DriverEntry) it.next();
	controlSignalDrivesBus[de.controlSignal] = true; // write occurs
	s = (Statement) signalsData.getStatementByControlSignal(de.controlSignal);
	driverAssignment[de.controlSignal][i] = s.getVHDLRightSideForBit
	  (de.netBitIndex);
	if(vhdlZeroDriverCondition[i] == null)
	  vhdlZeroDriverCondition[i] = "NOT C"+de.controlSignal+"='1'";
	else {
	  if(vhdlZeroDriverCondition[i].startsWith("NOT"))
	    vhdlZeroDriverCondition[i] = vhdlZeroDriverCondition[i].substring(4);
	  vhdlZeroDriverCondition[i] += " NAND C("+de.controlSignal+")='1'";
	}
      }
    }
  }
  
  /**
   * used by construction of zero driver logic
   * emit incoming ports for control signals in the form Ci of type
   * std_logic
   */
  private void emitZeroDriverInPorts(String indent, PrintWriter out) {
    boolean flag;
    if(hasDrivers) { // check flag if there are drivers
      flag = false;
      out.print(indent);
      for(int i=0;i<controlSignalDrivesBus.length;i++)
        if(controlSignalDrivesBus[i]) {
          out.print((flag?", ":"")+"C"+i);
          flag = true;
        }
      out.println(" : IN  std_logic; -- driving control signals");
    }
  }

  /**
   * emit all ports of zero driver logic
   */
  private void emitZeroDriverPorts(String indent, PrintWriter out) {
    out.println(indent+"PORT(");
    emitZeroDriverInPorts(indent+"  ",out);
    out.println(indent+"  TO_bus : OUT "+getVHDLType());
    out.println(indent+");");
  }

  /**
   * emit bus zero driver logic
   * component gets name getVHDLName()+"_zero_driver_logic_circuit"
   */
  public void emitZeroDriverCircuit(String indent, PrintWriter out) {
    int i;
    boolean flag;
    out.println(indent+"LIBRARY ieee;");
    out.println(indent+"USE ieee.std_logic_1164.ALL;");
    out.println();
    out.println(indent+"-- bus zero driver logic for "+getIdStr());
    out.println(indent+"ENTITY "+getVHDLName()+"_zero_driver_logic_circuit IS");
    emitZeroDriverPorts(indent+"  ",out); // emit Ci and TO_bus port decls
    out.println(indent+"END "+getVHDLName()+"_zero_driver_logic_circuit;");
    out.println();
    out.println(indent+"ARCHITECTURE primitive OF "+getVHDLName()+"_zero_driver_logic_circuit IS");
    out.println(indent+"BEGIN");
    // emit signal assignment for each bit of bus
    for(i=0;i<width;i++)
      out.println(indent+"  TO_bus("+array2BitIndex(i)+") <= '0'"
        +(vhdlZeroDriverCondition[i] == null?";":
	 (" WHEN "+vhdlZeroDriverCondition[i])+" ELSE 'Z';"));
    out.println(indent+"END primitive;");
  }

  /**
   * emit VHDL COMPONENT declaration for bus zero driver logic
   * (used in operation unit architecture)
   */
  public void emitZeroDriverComponent(String indent, PrintWriter out) {
    out.println(indent+"COMPONENT "+getVHDLName()+"_zero_driver_logic_circuit");
    emitZeroDriverPorts(indent+"  ",out);
    out.println(indent+"END COMPONENT;");
    out.println();
    out.println(indent+"FOR ALL : "+getVHDLName()+"_zero_driver_logic_circuit"
		+ " USE ENTITY WORK."+getVHDLName()+"_zero_driver_logic_circuit"
		+ "(primitive);");
  }

  /**
   * emit VHDL instantiation for bus zero driver logic in operation unit
   */
  public void emitZeroDriverInstantiation(String indent, PrintWriter out) {
    out.println(indent+getVHDLName()+"_zero_driver_logic: "
		+getVHDLName()+"_zero_driver_logic_circuit");
    out.println(indent+"  PORT MAP(");
    for(int i=0;i<controlSignalDrivesBus.length;i++)
      if(controlSignalDrivesBus[i])
        out.println(indent+"    C"+i+" => C_SIG("+i+"),");
    out.println(indent+"    TO_bus => "+getVHDLName()+");");
  }

  public void emitVHDLOutBusAssignment(String indent, PrintWriter out) {
    if(!outgoing) return;
    out.println(indent+getVHDLPortName()+" <= "+getVHDLName()+";");
  }

  public void emitVHDLInBusAssignment(String indent, PrintWriter out) {
    if(!incoming) return;
    if(RTOptions.forceInputs) {
      int i = getOffset();
      int bound = i+getWidth()-1;
      for(;i<=bound;i++)
      out.println(indent+getVHDLName()+
       (RTOptions.timing==RTOptions.TIMING_1EDGE?"_buf":"")
       +"("+i+") <= forceSL("+getVHDLPortName()
		  +"("+i+"));");
    }
    else if(RTOptions.timing!=RTOptions.TIMING_1EDGE)
      out.println(indent+getVHDLName()+" <= "+getVHDLPortName()+";");

    if(RTOptions.timing==RTOptions.TIMING_1EDGE) {
      out.println(indent+getVHDLName()+"_stable_driver : dff_reg");
      out.println(indent+"  GENERIC MAP(width => "+getWidth()+", triggering_edge => '1')");
      out.println(indent+"  PORT MAP(CLK => CLK_SIG, RESET => RESET_SIG,");
      out.println(indent+"    INPUT => "+
        (RTOptions.forceInputs?(getVHDLName()+"_buf"):getVHDLPortName())
        +", OUTPUT => "+getVHDLName()+");");
    }
  }

  public void updateShowContent() {
    for(int i=0;i<width;i++) show_content[i] = content[i];
  }

  public boolean written() {
    for(int i=0;i<width;i++) if(written[i]) return true;
    return false;
  } 

  public String getContentStr(int base) {
    if(written()) return RTSimGlobals.boolArray2String(content,base);
    else return RTSimGlobals.boolArray2String(show_content,base);
  } 
}
