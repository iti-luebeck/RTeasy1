package de.uniluebeck.iti.rteasy;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.uniluebeck.iti.rteasy.gui.RTLog;
import de.uniluebeck.iti.rteasy.kernel.Expression;
import de.uniluebeck.iti.rteasy.kernel.RTProgram;
import de.uniluebeck.iti.rteasy.kernel.RegBus;
import de.uniluebeck.iti.rteasy.kernel.Register;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;
import de.uniluebeck.iti.rteasy.kernel.Statement;

public class SignalsData {
  // nutzt die hashkey-Faehigkeit von Expression und Statement
  private Hashtable statement2controlSignal, expression2inputSignal;
  private ArrayList controlSignal2statements, inputSignal2expressions;
  private RTProgram rtprog;
  private HashSet driver[][];

  public class DriverEntry {
    public int controlSignal, netBitIndex;

    DriverEntry(int toControlSignal, int toNetBitIndex) {
      controlSignal = toControlSignal;
      netBitIndex = toNetBitIndex;
    }

    public boolean equals(Object o) {
      if(o instanceof DriverEntry) {
	DriverEntry de = (DriverEntry) o;
        return controlSignal == de.controlSignal
            && netBitIndex == de.netBitIndex;
      }
      else return false;
    }

    public int hashCode() {
      return controlSignal << 16 + (netBitIndex << 16) >> 16;
    }

    public String toString() {
      return "C"+controlSignal+":"+netBitIndex;
    }
  }

  public void printDriverTable(PrintStream out) {
    if(driver == null) return;
    RegBus rb;
    int j;
    HashSet driverSet;
    Iterator it;
    for(int i=0;i<driver.length;i++) if(driver[i] != null) {
      rb = rtprog.getRegBusById(i);
      out.println(rb.getIdStr()+" ("+i+")");
      for(j=0;j<driver[i].length;j++) if(driver[i][j] != null) {
	driverSet = driver[i][j];
	out.print("bit "+rb.array2BitIndex(j)+":");
	for(it=driverSet.iterator();it.hasNext();)
	  out.print(" "+((DriverEntry) it.next()).toString());
	out.println();
      }
      out.println();
    }
  }

  public SignalsData(RTProgram toRtprog) {
    rtprog = toRtprog;
    statement2controlSignal = new Hashtable();
    expression2inputSignal = new Hashtable();
    controlSignal2statements = new ArrayList();
    inputSignal2expressions = new ArrayList();
    driver = new HashSet[rtprog.getRegBusCount()][];
    Enumeration en = rtprog.getRegisters().elements();
    RegBus rb;
    RegisterArray ra;
    for(en=rtprog.getRegisters().elements();en.hasMoreElements();) {
      rb = (RegBus) en.nextElement();
      driver[rb.getRegBusId()] = new HashSet[rb.getWidth()];
    }
    for(en=rtprog.getBuses().elements();en.hasMoreElements();) {
      rb = (RegBus) en.nextElement();
      driver[rb.getRegBusId()] = new HashSet[rb.getWidth()];
    }
    for(en=rtprog.getRegArrays().elements();en.hasMoreElements();) {
        ra = (RegisterArray) en.nextElement();
        driver[ra.getRegBusId()] = new HashSet[ra.getWidth()];
    }
  }

  public void insertDriver(RegBus rb, int bitIndex, int controlSignal,
			    int netBitIndex) {
    int rbid = rb.getRegBusId();
    if(rbid >= driver.length || rbid < 0) {
      RTLog.log("SignalsData.insertDriver: rbid out of range: "+rbid
		+ " ("+rb.getIdStr()+")");
      return;
    }
    int ai = rb.bitIndex2Array(bitIndex);
    if(ai >= rb.getWidth() || ai < 0) {
      RTLog.log("SignalsData.insertDriver: bitIndex out of range: "+bitIndex
		+ " ("+rb.getIdStr()+")");
      return;
    }
    // RTLog.log("SignalsData.insertDriver("+rb.getIdStr()+","+bitIndex
    //	      +","+controlSignal+","+netBitIndex+")");
    //RTLog.log("rbid = "+rbid+"/"+driver.length+", ai = "+ai+"/"+rb.getWidth());
    HashSet driverSet = driver[rbid][ai];
    if(driverSet == null) {
      driverSet = new HashSet();
      driver[rbid][ai] = driverSet;
    }
    driverSet.add(new DriverEntry(controlSignal,netBitIndex));
  }

  public Statement getStatementByControlSignal(int cs) {
    if(cs < 0 || cs >= controlSignal2statements.size()) return null;
    Object al = controlSignal2statements.get(cs);
    if(al == null) return null;
    else return (Statement) ((ArrayList) al).get(0);
  }

  public Expression getExpressionByConditionSignal(int condSig) {
    if(condSig < 0 || condSig >= inputSignal2expressions.size()) return null;
    Object al = inputSignal2expressions.get(condSig);
    if(al == null) return null;
    else return (Expression) ((ArrayList) al).get(0);
  }

  public int lookupStatement(Statement st) {
    if(statement2controlSignal.containsKey(st))
      return ((Integer) statement2controlSignal.get(st)).intValue();
    else
      return -1;
  }

  public int lookupExpression(Expression e) {
    if(expression2inputSignal.containsKey(e)) 
      return ((Integer) expression2inputSignal.get(e)).intValue();
    else
      return -1;
  }

  public CircuitInputBundle getDriverBundle(RegBus rb) {
    CircuitInputBundle bundle = new CircuitInputBundle(getControlSignalCount());
    int rbid = rb.getRegBusId();
    HashSet bits[] = driver[rbid];
    HashSet driverSet;
    DriverEntry de;
    Iterator it;
    int bitIndex;
    for(int i=0;i<bits.length;i++) if(bits[i] != null) {
      bitIndex = rb.array2BitIndex(i);
      driverSet = bits[i];
      for(it=driverSet.iterator();it.hasNext();) {
	de = (DriverEntry) it.next();
        bundle.include(de.controlSignal,new CircuitPortReference("rtop_C"+de.controlSignal,"out",de.controlSignal,de.netBitIndex,de.netBitIndex));
      }
    }
    return bundle;
  }

  /**
   // to be implemented for grouped switching
  public Hashtable getControlSignalSwitchTable(RegBus rb) {
    Hashtable back = new Hashtable();
    int rbid = rb.getRegBusId();
    HashSet bits = driver[rbid];
    HashSet driverSet;
    DriverEntry de;
    Iterator it;
    int bitIndex;
    for(int i=0;i<bits.length;i++) if(bits[i] != null) {
      bitIndex = r.array2BitIndex(i);
      driverSet = bits[i];
      for(it=driverSet.iterator();it.hasNext();) {
	de = (DriverEntry) it.next();
        bundle.include(new CircuitPortReference("rtop_C"+de.controlSignal,"out",de.controlSignal,de.nextBitIndex,de.nextBitIndex));
      }
    }
  }
  */

  public void emitRegisterLogicCircuit(String indent, PrintWriter out, Register r) {
    int rbid = r.getRegBusId();
    HashSet bits[] = driver[rbid];
    HashSet driverSet;
    DriverEntry de;
    Iterator it;
    int bitIndex;
    Statement s;
    CircuitInputBundle bundle = getDriverBundle(r);
    CircuitPortReference cpr;
    out.println(indent+"-- register logic for "+r.getIdStr());
    out.println();
    out.println(indent+"LIBRARY ieee;");
    out.println(indent+"USE ieee.std_logic_1164.ALL;");
    out.println();
    out.println(indent+"ENTITY "+r.getVHDLName()+"_logic_circuit IS");
    out.println(indent+"  PORT(");
    bundle.emitPortDeclarations(indent+"    ",out);
    out.println(indent+"    FROM_reg : IN  "+r.getVHDLType()+";");
    out.println(indent+"    TO_reg : OUT "+r.getVHDLType());
    out.println(indent+"  );");
    out.println(indent+"END "+r.getVHDLName()+"_logic_circuit;");
    out.println();
    out.println(indent+"ARCHITECTURE primitive OF "+r.getVHDLName()+"_logic_circuit IS");
    out.println(indent+"BEGIN"); 
    for(int i=0;i<bits.length;i++) if(bits[i] != null) {
      bitIndex = r.array2BitIndex(i);
      driverSet = bits[i];
      out.print(indent+"  TO_reg("+bitIndex+") <= ");
      for(it=driverSet.iterator();it.hasNext();) {
	de = (DriverEntry) it.next();
        cpr = bundle.map(new CircuitPortReference("rtop_C"+de.controlSignal,"out",de.controlSignal,de.netBitIndex,de.netBitIndex));
        out.println((cpr==null?"<CP null>":cpr.toVHDLPortName())+"("+de.netBitIndex+") WHEN C"
		    +de.controlSignal+" = '1'");
	out.print(indent+"  ELSE ");
      }
      out.println("FROM_reg("+bitIndex+");");
      out.println();
    }
    //bundle.dumpPortTable();
    out.println(indent+"END primitive;");
  }
  
  public void emitRegisterLogicCircuit(String indent, PrintWriter out, RegisterArray r) {
	    int rbid = r.getRegBusId();
	    HashSet bits[] = driver[rbid];
	    HashSet driverSet;
	    DriverEntry de;
	    Iterator it;
	    int bitIndex;
	    Statement s;
	    CircuitInputBundle bundle = getDriverBundle(r);
	    CircuitPortReference cpr;
	    out.println(indent+"-- register array logic for "+r.getIdStr());
	    out.println();
	    out.println(indent+"LIBRARY ieee;");
	    out.println(indent+"USE ieee.std_logic_1164.ALL;");
	    out.println();
	    out.println(indent+"ENTITY "+r.getVHDLName()+"_logic_circuit IS");
	    out.println(indent+"  PORT(");
	    bundle.emitPortDeclarations(indent+"    ",out);
	    out.println(indent+"    FROM_reg : IN  "+r.getVHDLType()+";");
	    out.println(indent+"    TO_reg : OUT "+r.getVHDLType());
	    out.println(indent+"  );");
	    out.println(indent+"END "+r.getVHDLName()+"_logic_circuit;");
	    out.println();
	    out.println(indent+"ARCHITECTURE primitive OF "+r.getVHDLName()+"_logic_circuit IS");
	    out.println(indent+"BEGIN"); 
	    for(int i=0;i<bits.length;i++) if(bits[i] != null) {
	      bitIndex = r.array2BitIndex(i);
	      driverSet = bits[i];
	      out.print(indent+"  TO_reg("+bitIndex+") <= ");
	      for(it=driverSet.iterator();it.hasNext();) {
		de = (DriverEntry) it.next();
	        cpr = bundle.map(new CircuitPortReference("rtop_C"+de.controlSignal,"out",de.controlSignal,de.netBitIndex,de.netBitIndex));
	        out.println((cpr==null?"<CP null>":cpr.toVHDLPortName())+"("+de.netBitIndex+") WHEN C"
			    +de.controlSignal+" = '1'");
		out.print(indent+"  ELSE ");
	      }
	      out.println("FROM_reg("+bitIndex+");");
	      out.println();
	    }
	    //bundle.dumpPortTable();
	    out.println(indent+"END primitive;");
	  }

  public void emitRegisterLogicCircuitComp(String indent, PrintWriter out, Register r) {
    CircuitInputBundle bundle = getDriverBundle(r);
    out.println(indent+"COMPONENT "+r.getVHDLName()+"_logic_circuit");
    out.println(indent+"  PORT(");
    bundle.emitPortDeclarations(indent+"    ",out);
    out.println(indent+"    FROM_reg : IN  "+r.getVHDLType()+";");
    out.println(indent+"    TO_reg : OUT "+r.getVHDLType());
    out.println(indent+"  );");
    out.println(indent+"END COMPONENT;");
    out.println();
    out.println(indent+"FOR ALL : "+r.getVHDLName()+"_logic_circuit USE ENTITY WORK."
		+ r.getVHDLName()+"_logic_circuit(primitive);");
  }
  
  public void emitRegisterLogicCircuitComp(String indent, PrintWriter out, RegisterArray r) {
	    CircuitInputBundle bundle = getDriverBundle(r);
	    out.println(indent+"COMPONENT "+r.getVHDLName()+"_logic_circuit");
	    out.println(indent+"  PORT(");
	    bundle.emitPortDeclarations(indent+"    ",out);
	    out.println(indent+"    FROM_reg : IN  "+r.getVHDLType()+";");
	    out.println(indent+"    TO_reg : OUT "+r.getVHDLType());
	    out.println(indent+"  );");
	    out.println(indent+"END COMPONENT;");
	    out.println();
	    out.println(indent+"FOR ALL : "+r.getVHDLName()+"_logic_circuit USE ENTITY WORK."
			+ r.getVHDLName()+"_logic_circuit(primitive);");
	  }

  public void emitRegisterLogicInstantiation(String indent, PrintWriter out,
					     Register r) {
    CircuitInputBundle bundle = getDriverBundle(r);
    out.println(indent+r.getVHDLName()+"_logic: "+r.getVHDLName()
		+"_logic_circuit");
    out.println(indent+"  PORT MAP(");
    bundle.emitPortMap(indent+"    ",out,this);
    out.println(indent+"    FROM_reg => "+r.getVHDLName()+"_out,");
    out.println(indent+"    TO_reg => "+r.getVHDLName()+"_in);");
  }
  
  public void emitRegisterLogicInstantiation(String indent, PrintWriter out,
		     RegisterArray r) {
CircuitInputBundle bundle = getDriverBundle(r);
out.println(indent+r.getVHDLName()+"_logic: "+r.getVHDLName()
+"_logic_circuit");
out.println(indent+"  PORT MAP(");
bundle.emitPortMap(indent+"    ",out,this);
out.println(indent+"    FROM_reg => "+r.getVHDLName()+"_out,");
out.println(indent+"    TO_reg => "+r.getVHDLName()+"_in);");
}

  /*
  public void emitBusLogic(String indent, PrintWriter out, Bus b) {
    out.println(indent+"-- bus logic for "+b.getIdStr());
    if(b.incoming()) {
      out.println(indent+"-- incoming bus: connection to port");
      b.emitVHDLInBusAssignment(indent,out);
      return;
    }
    int controlSignalCount = controlSignal2statements.size();
    int bwidth = b.getWidth();
    String bname = b.getVHDLName();
    int rbid = b.getRegBusId();
    HashSet bits[] = driver[rbid];
    HashSet driverSet;
    DriverEntry de;
    Iterator it;
    Statement s;
    // caches all "true" bit indexes
    int bitIndex[] = new int[bwidth];
    // NAND all control signals driving to a bus bit
    // (needed to drive 0 if true)
    String zeroDriverCondition[] = new String[bwidth];
    // store all bit assignments by control signal
    // (this yields ordering them by source net)
    String driverAssignment[][] =
                      new String[controlSignalCount][bwidth];
    // value at control signal index is true if control signal causes
    // bus write op
    // Java initializes boolean to false
    boolean controlSignalDrivesBus[] = new boolean[controlSignalCount];
    int i, j; 
    for(i=0;i<bwidth;i++) if(bits[i] != null) {
      bitIndex[i] = b.array2BitIndex(i);  // fill bit index cache
      driverSet = bits[i];
      for(it=driverSet.iterator();it.hasNext();) {
	de = (DriverEntry) it.next();
	controlSignalDrivesBus[de.controlSignal] = true; // write occurs
	s = (Statement)
          ((List) controlSignal2statements.get(de.controlSignal)).get(0);
	driverAssignment[de.controlSignal][i] = s.getVHDLRightSideForBit
	  (de.netBitIndex);
	if(zeroDriverCondition[i] == null)
	  zeroDriverCondition[i] = "NOT C("+de.controlSignal+")='1'";
	else {
	  if(zeroDriverCondition[i].startsWith("NOT"))
	    zeroDriverCondition[i] = zeroDriverCondition[i].substring(4);
	  zeroDriverCondition[i] += " NAND C("+de.controlSignal+")='1'";
	}
      }
    }
    // now print out zero assignment for all bits
    out.println(indent+"-- drive zero if bus is unused (determined by control"
		+ " signals)");
    for(i=0;i<bwidth;i++)
      out.println(indent+bname+"("+bitIndex[i]+") <= '0' WHEN "
		  + zeroDriverCondition[i] + " ELSE 'Z';");
    out.println(indent+"-- assignments ordered by control signals");
    for(i=0;i<controlSignalCount;i++) if(controlSignalDrivesBus[i]) {
      out.println(indent+"-- C("+i+")   " +
		  ((Statement) ((List) controlSignal2statements.get(i)).get(0))
		  .toString());
      for(j=0;j<bwidth;j++)
	out.println(indent+bname+"("+bitIndex[j]+") <= "
		    + driverAssignment[i][j] + " WHEN C("+i+")='1' ELSE 'Z';");
    }
    if(b.outgoing()) {
      out.println(indent+"-- outgoing bus: connection with port");
      b.emitVHDLOutBusAssignment(indent,out);
    }
  }
  */

  public void emitBusAssignments(String indent, PrintWriter out) {
    int i;
    int s = controlSignal2statements.size();
    Statement st;
    for(i=0;i<s;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      if(st.busOnLeftSide()) {
        out.println(indent+"IF C_in("+i+")='1' THEN");
        st.emitVHDL(indent+"  ",out);
        out.println(indent+"END IF;");
      }
    }
  }

  public void emitRTOperations(String indent, PrintWriter out) {
    int i;
    int s = controlSignal2statements.size();
    Statement st;
    for(i=0;i<s;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      if(st.busOnLeftSide() == false) {
        out.println(indent+"IF C_in("+i+")='1' THEN");
        st.emitVHDL(indent+"  ",out);
        out.println(indent+"END IF;");
      }
    }
  }

  public void emitControlUnitComponent(String indent, PrintWriter out,
				       String componentName) {
    out.println(indent+"COMPONENT "+componentName+"_cu");
    out.println(indent+"  PORT (");
    out.println(indent+"    CLK, RESET : IN  std_logic;");
    out.println(indent+"    C          : OUT std_logic_vector(0 to "
		+ (getControlSignalCount()-1)+");");
    out.println(indent+"    I          : IN  std_logic_vector(0 to "
		+ (getInputSignalCount()-1)+")");
    out.println(indent+"  );");
    out.println(indent+"END COMPONENT;");
    out.println();
    out.println(indent+"FOR ALL : "+componentName+"_cu USE ENTITY WORK."
		+componentName+"_cu(struct);");
  }

  public void emitOperationUnitComponentPrefix(String indent, PrintWriter out,
				       String componentName) {
    out.println(indent+"COMPONENT "+componentName+"_ou");
    out.println(indent+"  PORT (");
    out.println(indent+"    CLK, RESET : IN  std_logic;");
    out.println(indent+"    C          : IN  std_logic_vector(0 to "
		+ (getControlSignalCount()-1)+");");
    out.print(indent+"    I          : OUT std_logic_vector(0 to "
		+ (getInputSignalCount()-1)+")");
  }

  public void emitConnectionSignals(String indent, PrintWriter out) {
    out.println(indent+"SIGNAL C : std_logic_vector(0 to "
		+ (getControlSignalCount()-1)+");");
    out.println(indent+"SIGNAL I : std_logic_vector(0 to "
		+ (getInputSignalCount()-1)+");");
  }

  public void emitInputSignalsCalculation(String indent, PrintWriter out) {
    int i;
    int s = inputSignal2expressions.size();
    Expression e;
    for(i=0;i<s;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println(indent+"I("+i+") <= evalLast("+e.toVHDL()+");");
    }
  }

  public void emitVHDLSignalDeclarations(String indent, PrintWriter out) {
    int i;
    int s = inputSignal2expressions.size();
    Expression e;
    Statement st;
    for(i=0;i<s;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println(indent+"SIGNAL net_I"+i+" : std_logic_vector ("
		  +(e.getWidth()-1)+" DOWNTO 0);");
    }
    s = controlSignal2statements.size();
    for(i=0;i<s;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      st.emitVHDLSignalDeclaration(i,"  ",out);
    }
  }

  public void emitVHDL(String indent, PrintWriter out) {
    int i;
    int s = controlSignal2statements.size();
    Statement st;
    Expression e;
    out.println("  -- circuits belonging to RT operations");
    for(i=0;i<s;i++) { 
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      st.emitVHDLSignalAssignments(i,"  ",out);
      out.println();
    }
    s = inputSignal2expressions.size();
    out.println("  -- circuits calculating control unit input");
    for(i=0;i<s;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println(indent+"net_I"+i+" <= "+e.toVHDL()+";");
      out.println(indent+"I("+i+") <= net_I"+i+"(0);");
    }
  }

  /**
   * @return gibt die maximale in RT-Operationen vorkommende Bitbreite
   * der rechten Seite zur&uuml;ck
   */
  public int getMaximumExpressionWidth() {
    int i;
    int s = controlSignal2statements.size();
    int maxWidth = 0;
    int tmp;
    for(i=0;i<s;i++) {
      tmp = ((Statement) ((ArrayList) controlSignal2statements.get(i)).get(0))
            .getRValWidth();
      if(tmp > maxWidth) maxWidth = tmp;
    }
    return maxWidth;
  }

  synchronized public void insertStatement(Statement s) {
    Integer ib = (Integer) statement2controlSignal.get(s);
    if(ib != null) {
      s.setControlSignal(ib.intValue());
      ((List) controlSignal2statements.get(ib.intValue())).add(s);
      return;
    }
    int idx = controlSignal2statements.size();
    ArrayList nstl = new ArrayList();
    nstl.add(s);
    controlSignal2statements.add(nstl);
    s.setControlSignal(idx);
    statement2controlSignal.put(s,new Integer(idx));
    s.insertDrivers(this);
  }

  synchronized public void insertExpression(Expression e) {
    Integer ib = (Integer) expression2inputSignal.get(e);
    if(ib != null) {
      e.setInputSignal(ib.intValue());
      ((List) inputSignal2expressions.get(ib.intValue())).add(e);
      return;
    }
    int idx = inputSignal2expressions.size();
    ArrayList nel = new ArrayList();
    nel.add(e);
    inputSignal2expressions.add(nel);
    e.setInputSignal(idx);
    expression2inputSignal.put(e,new Integer(idx));
  }

  public int getControlSignalCount() { return controlSignal2statements.size(); }

  public int getInputSignalCount() { return inputSignal2expressions.size(); }

  public Hashtable getControlSignalsByLine() {
    Hashtable lines = new Hashtable();
    HashSet sigs;
    Statement s;
    Integer line;
    for(int i = getControlSignalCount()-1;i>=0;i--) {
      s = (Statement) controlSignal2statements.get(i);
      line = new Integer(s.getPositionRange().endLine);
      sigs = (HashSet) lines.get(line);
      if(line == null) {
        sigs = new HashSet(); 
        lines.put(line,sigs);
      }
      sigs.add(new Integer(i));
    }
    return lines;
  }

  public void emitRTOpCircuits(String indent, PrintWriter out, String componentName) {
    int i;
    int csCount = controlSignal2statements.size();
    Statement st;
    Expression e;
    out.println(indent+"-- circuits realizing register-transfer operations");
    for(i=0;i<csCount;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      if(st.getStatementType() == RTSimGlobals.ASSIGN) {
        e = st.getRight();
        out.println();
        out.println(indent+"-- realization of RT operation "+st.toString());
        out.println(indent+"-- triggered by control signal C("+i+")");
        e.emitCircuit(indent,out,componentName+"_rtop_C"+i+"_circuit",st.getLeftWidth(),st);
      }
    }
  }
  
  public void emitRTOpDecls(String indent, PrintWriter out, String componentName) {
    int i;
    int csCount = controlSignal2statements.size();
    Statement st;
    Expression e;
    out.println(indent+"-- declarations for register-transfer circuits and signals");
    for(i=0;i<csCount;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      if(st.getStatementType() == RTSimGlobals.ASSIGN) {
        e = st.getRight();
        out.println();
        out.println(indent+"-- RT operation "+st.toString());
        out.println(indent+"-- triggered by control signal C("+i+")");
        e.emitOutputSignalDeclaration(indent,out,"rtop_C"+i+"_out",st.getLeftWidth());
        e.emitCircuitComponent(indent,out,componentName+"_rtop_C"+i+"_circuit",st.getLeftWidth(),st);
      }
    }
  }
  
  public void emitRTOpInstantiations(String indent, PrintWriter out, String componentName) {
    int i;
    int csCount = controlSignal2statements.size();
    Statement st;
    Expression e;
    out.println(indent+"-- instantiations for register-transfer circuits");
    for(i=0;i<csCount;i++) {
      st = (Statement) ((ArrayList) controlSignal2statements.get(i)).get(0);
      if(st.getStatementType() == RTSimGlobals.ASSIGN) {
        e = st.getRight();
        out.println();
        out.println(indent+"-- RT operation "+st.toString());
        out.println(indent+"-- triggered by control signal C("+i+")");
        e.emitInstantiation(indent,out,componentName+"_rtop_C"+i+"_circuit","rtop_C"+i,
                            "rtop_C"+i+"_out");
        st.emitTristateDrivers(indent,out,i);
      }
    }
  }
 
  public void emitConditionCircuits(String indent, PrintWriter out, String componentName) {
    int i;
    int isCount = inputSignal2expressions.size();
    Expression e;
    out.println(indent+"-- circuits realizing conditions");
    for(i=0;i<isCount;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println();
      out.println(indent+"-- realization of condition "+e.toString());
      out.println(indent+"-- driving condition signal I("+i+")");
      e.emitCircuit(indent,out,componentName+"_cond_I"+i+"_circuit",null);
    }
  }

  public void emitConditionDecls(String indent, PrintWriter out, String componentName) {
    int i;
    int isCount = inputSignal2expressions.size();
    Expression e;
    out.println(indent+"-- COMPONENT declarations for condition circuits");
    for(i=0;i<isCount;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println();
      out.println(indent+"-- condition "+e.toString());
      out.println(indent+"-- driving condition signal I("+i+")");
      e.emitCircuitComponent(indent,out,componentName+"_cond_I"+i+"_circuit");
    }
  }

  public void emitConditionInstantiations(String indent, PrintWriter out, String componentName) {
    int i;
    int isCount = inputSignal2expressions.size();
    Expression e;
    out.println(indent+"-- instantiations of condition circuits");
    for(i=0;i<isCount;i++) {
      e = (Expression) ((ArrayList) inputSignal2expressions.get(i)).get(0);
      out.println();
      out.println(indent+"-- condition "+e.toString());
      out.println(indent+"-- driving condition signal I("+i+")");
      out.println(indent+"I("+i+") <= I"+i+"(0);");
      e.emitConditionInstantiation(indent,out,componentName+"_cond_I"+i+"_circuit","cond_I"+i,i);   
    }
  }

  public void emitCircuits(String indent, PrintWriter out, String componentName) {
    emitRTOpCircuits(indent,out,componentName);
    out.println();
    emitConditionCircuits(indent,out,componentName);
  }

  public void emitDecls(String indent, PrintWriter out, String componentName) {
    emitRTOpDecls(indent,out,componentName);
    out.println();
    emitConditionDecls(indent,out,componentName);
  }

  public void emitInstantiations(String indent, PrintWriter out, String componentName) {
    emitRTOpInstantiations(indent,out,componentName);
    out.println();
    emitConditionInstantiations(indent,out,componentName);
  }

  public ArrayList getInputSignals() { return inputSignal2expressions; }

  public ArrayList getControlSignals() { return controlSignal2statements; }

  public HashSet[] getDriversByRbid(int rbid) {
    return driver[rbid];
  }
}
