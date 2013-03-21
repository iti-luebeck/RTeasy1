package de.uniluebeck.iti.rteasy.kernel;

import java.util.*;
import java.io.*;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTRtProg;
import de.uniluebeck.iti.rteasy.gui.RTOptions;

public class RTProgram {
     
  private ProgramControl pc;
  private Hashtable registers;
  private Hashtable buses;
  private Hashtable memories;
  private Hashtable regarrays;
  private Hashtable labels;
  private HashSet inBuses, outBuses;
  private SignalsData signalsData;
  private StatementSequence statSeq;
  private int statSeqLength;
  private int edgeType;
  private int regBusCount;
  private ArrayList stats;
  private ListIterator statsIt;
  private PositionRange statPos;
  private Statement currentStatement = null;
  private Statement helpStatement = null;
  private int newLabelCount = 1;
  private Label endLabel = new Label("end",null);
  private Label labelForNextInsertion = null;
  private boolean hasLabelForNextInsertion = false;
  private boolean nameSet;
  private boolean ifStat = false;
  private String componentName = "UNNAMED";
  private LinkedList registerOrder, busOrder, memoryOrder, regarOrder;
  private RegBus[] regBusList;

  public void dumpSimTree(PrintWriter out, String indent) {
    out.println(indent+"statSeq : StatementSequence = ");
    statSeq.dumpSimTree(out,indent+"  ");
  }

  public RTProgram(ASTRtProg rtprog, RTSim_SemAna semAna) {
    pc = new ProgramControl(this);
    nameSet = rtprog.nameSet();
    if(nameSet) componentName = rtprog.getName();
    registers = semAna.getRegisters();
    statSeq = new StatementSequence(pc,rtprog.getStatementSequence());
    statSeqLength = statSeq.getLength();
    endLabel.setEntry(statSeqLength,0);
    //registers = semAna.getRegisters();
    buses = semAna.getBuses();
    memories = semAna.getMemories();
    regarrays = semAna.getRegArrays();
    labels = semAna.getLabels();
    inBuses = semAna.getInBuses();
    outBuses = semAna.getOutBuses();
    regBusCount = semAna.getRegBusCount();
    if(RTOptions.calculateSignals) {
      signalsData = new SignalsData(this);
      statSeq.calculateSignals(signalsData);
    }
    registerOrder = semAna.getRegisterOrder();
    busOrder = semAna.getBusOrder();
    memoryOrder = semAna.getMemoryOrder();
    regarOrder = semAna.getRegArrayOrder();
    regBusList = semAna.getRegBusList();
    initStats();
  }
    
  public Hashtable getRegisters() { return registers; }
  public Hashtable getBuses() { return buses; }
  public Hashtable getMemories() { return memories; }
  public Hashtable getRegArrays() {return regarrays;}
  public HashSet getInBuses() { return inBuses; }
  public HashSet getOutBuses() { return outBuses; }
  public int getRegBusCount() { return regBusCount; }
  public RegBus getRegBusById(int rbid) { return regBusList[rbid]; }

  /**
   *  generiert ein neues, bisher noch nicht verwendetes Label
   */
  private String getNextLabelCaption() {
    String nkey = "L" + Integer.toString(newLabelCount);
    newLabelCount++;
    while(labels.contains(nkey)) nkey = "L"+nkey;
    return nkey;
  }

  public Label getLabelForNextInsertion() {
    if(hasLabelForNextInsertion) return labelForNextInsertion;
    else {
      labelForNextInsertion = new Label(getNextLabelCaption(),null);
      hasLabelForNextInsertion = true;
      return labelForNextInsertion;
    }
  }

  /**
   * gibt ein Label auf einen Zustand zur&uuml;ck. Existiert ein solches
   * Label, wird es zur&uuml;ckgegeben, andernfalls wird ein neues erzeugt.
   * Die Laufzeit ist linear zur Anzahl der vorhandenen Labels!
   */
  public Label getLabelForIndex(int statSeqIndex) {
    Label l;
    if(statSeqIndex >= statSeqLength) return endLabel;
    for(Enumeration en=labels.elements();en.hasMoreElements();) {
      l = (Label) en.nextElement();
      if(l.getStatSeqEntry() == statSeqIndex && l.getParStatsEntry() == 0)
	return l;
    }
    l = new Label(getNextLabelCaption(),null);
    l.setEntry(statSeqIndex,0);
    labels.put(l.getIdStr(),l);
    statSeq.getParStatsAt(statSeqIndex).putLabel(0,l);
    return l;
  }

  public String getComponentName() { return componentName; }

  /**
   * gibt das endLabel zur&uuml;ck
   */
  public Label getEndLabel() { return endLabel; }

  public ProgramControl getProgramControl() { return pc; }

  /**
   * f&uuml;gt einen neuen Zustand ein und aktualisiert alle Bez&uuml;ge
   * auf Zustandsnummern
   */
  public boolean insertParallelStatements(int statSeqIndex, ParallelStatements
					  ps) {
    if(statSeq.insertParallelStatements(statSeqIndex,ps)) {
      if(hasLabelForNextInsertion) {
        ps.putLabel(0,labelForNextInsertion);
        labelForNextInsertion = null;
        hasLabelForNextInsertion = false;
      }
      for(Enumeration en=labels.elements();en.hasMoreElements();)
        ((Label) en.nextElement()).insertUpdate(statSeqIndex);
      statSeqLength++;
      endLabel.setEntry(statSeqLength,0);
      return true;
    }
    else return false;
  }

  /**
   * f&uuml;hrt alle zur &Uuml;bersetzung in VHDL notwendigen Transformationen
   * durch
   */
  public void performTransformations() {
	  statSeq.transformSwitch();
    statSeqLength = statSeq.expandPipeOps(this);
    statSeqLength = statSeq.expandLabelStates(this);
    statSeq.cleanUp();
    statSeq.eleminateElse();
    statSeq.deNest();
    
  }

  /**
   * derives low level model without pipe ops and states containing inner labels
   */
  public void deriveLowLevelModel() {
	statSeq.transformSwitch();
    statSeqLength = statSeq.expandPipeOps(this);
    statSeqLength = statSeq.expandLabelStates(this);
    statSeq.cleanUp();
  }

  /**
   * eleminiert alle Zust&auml;nde mit |-Operator
   */
  public void expandPipeOps() {
    statSeqLength = statSeq.expandPipeOps(this);
  }

  /**
   * gibt die zur Zeit ausgef&uuml;hrte Flanke zur&uuml;ck
   * @return RTSimGlobals.OSTAT_TYPE_2EDGE1 bei erster Flanke in Zweiflankensteuerung,
   * RTSimGlobals.OSTAT_TYPE_2EDGE2 bei zweiter Flanke in Zweiflankensteuerung,
   * RTSimGlobals.OSTAT_TYPE_MEALY bei Einflankensteuerung
   */
  public int getEdgeType() { return edgeType; }

  public void emitAllInOne(PrintWriter out) {
    performTransformations();
    //emitLibraryInclusions(out);
    out.println("-- VHDL model of "+componentName);
    out.println("-- generated by RTeasy");
    out.println();
    insertVHDLTemplate(out,"rteasy_functions");
    out.println();
    out.println("-- generic components");
    out.println();
    insertVHDLTemplate(out,"dff_reg");
    out.println();
    insertVHDLTemplate(out,"tristate");
    out.println();
    emitControlUnit(out);
    out.println();
    emitOperationUnit(out);
    out.println();
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println();
    emitEntity(out);
    out.println();
    emitArchitecture(out);
  }

  public void emitStruct(PrintWriter out) {
    emitLibraryInclusions(out);
    out.println();
    emitEntity(out);
    out.println();
    emitArchitecture(out);
  }
    
  public void emitTestBenchFrame(PrintWriter out) {
    emitLibraryInclusions(out);
    out.println();
    out.println("ENTITY "+componentName+"_tb IS");
    out.println("  PORT(");
    emitPortsReverse(out);
    out.println();
    out.println("  );");
    out.println("END "+componentName+"_tb;");
    out.println("-- hds interface_end");
  }

  public void emitLibraryInclusions(PrintWriter out) {
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println("USE ieee.std_logic_unsigned.ALL;");
  }

  public void emitEntity(PrintWriter out) {
    Iterator it;
    Bus b;
    out.println("ENTITY "+componentName+" IS");
    out.println("  PORT(");
    out.print("    CLK, RESET : IN std_logic");
    for(it=inBuses.iterator();it.hasNext();) {
      out.println(";");
      b = (Bus) it.next();
      b.emitVHDLPortDeclaration("    ",out);
    }
    for(it=outBuses.iterator();it.hasNext();) {
      out.println(";");
      b = (Bus) it.next();
      b.emitVHDLPortDeclaration("    ",out);
    }
    out.println();
    out.println("  );");
    out.println("END "+componentName+";");
  }

  public void emitArchitecture(PrintWriter out) {
    Iterator it;
    Bus b;
    String lib = "WORK";
    out.println("ARCHITECTURE struct OF "+componentName+" IS");
    out.println("  SIGNAL CLK_SIGNAL, RESET_SIGNAL : std_logic;");
    // Verbindungsleitungen zwischen Steuer- und Operationswerk
    signalsData.emitConnectionSignals("  ",out);
    out.println();
    signalsData.emitControlUnitComponent("  ",out,componentName);
    out.println();
    signalsData.emitOperationUnitComponentPrefix("  ",out,componentName);
    for(it=inBuses.iterator();it.hasNext();) {
      out.println(";");
      b = (Bus) it.next();
      b.emitVHDLPortDeclaration("      ",out);
    }
    for(it=outBuses.iterator();it.hasNext();) {
      out.println(";");
      b = (Bus) it.next();
      b.emitVHDLPortDeclaration("      ",out);
    }
    out.println();
    out.println("    );");
    out.println("  END COMPONENT;");
    out.println();
    out.println("  FOR ALL : "+componentName+"_ou USE ENTITY WORK."+componentName+"_ou(struct);");
    out.println();
    out.println("BEGIN");
    out.println("  CLK_SIGNAL <= CLK;");
    out.println("  RESET_SIGNAL <= RESET;");
    out.println();
    // Definition des Steuerwerks
    out.println("  Control_Unit: "+componentName
		+"_cu");
    out.println("    PORT MAP(");
    out.println("      CLK => CLK_SIGNAL,");
    out.println("      RESET => RESET_SIGNAL,");
    out.println("      C => C,");
    out.println("      I => I");
    out.println("    );");
    out.println();
    out.println("  Operation_Unit: "+componentName
		+"_ou");
    out.println("    PORT MAP(");
    out.println("      CLK => CLK_SIGNAL,");
    out.println("      RESET => RESET_SIGNAL,");
    out.println("      C => C,");
    out.print("      I => I");
    for(it=inBuses.iterator();it.hasNext();) {
      out.println(",");
      b = (Bus) it.next();
      b.emitVHDLPortMap("      ",out);
    }
    for(it=outBuses.iterator();it.hasNext();) {
      out.println(",");
      b = (Bus) it.next();
      b.emitVHDLPortMap("      ",out);
    }
    out.println();
    out.println("    );");
    out.println("END struct;");
  }

  public void emitControlUnit(PrintWriter out) {
    String i_max = Integer.toString(signalsData.getInputSignalCount()-1);
    String c_max = Integer.toString(signalsData.getControlSignalCount()-1);
    String statewidth = Integer.toString(RTSimGlobals.ld(statSeq.getLength()));
    String statewidth_m1 = Integer.toString(RTSimGlobals.ld(statSeq.getLength())-1);
    String replaceMap[][] = new String[][] {  // order does matter !!!
      new String[] {"%%COMPONENT_NAME",componentName}, 
      new String[] {"%%C_MAX",c_max},
      new String[] {"%%I_MAX",i_max},
      new String[] {"%%I_WIDTH",Integer.toString(signalsData.getInputSignalCount())},
      new String[] {"%%STATEWIDTH_M1",statewidth_m1},
      new String[] {"%%STATEWIDTH",statewidth},
      new String[] {"%%EDGE",(RTOptions.timing==RTOptions.TIMING_2EDGES)?"0":"1"}
    };
    out.println("-- CONTROL UNIT");
    out.println();
    out.println("-- combinatorial circuit for state transition function");
    emitStateTransitionNet(out);
    out.println();
    out.println("-- combinatorial circuit for output function");
    emitStateOutputNet(out);
    out.println();
    insertVHDLTemplate(out,"cu_entity", replaceMap);
    out.println();
    out.println("ARCHITECTURE struct OF "+componentName+"_cu IS");
    insertVHDLTemplate("  ",out,"cu_architecture_signals",replaceMap);
    out.println();
    insertVHDLTemplate("  ",out,"dff_reg_comp");
    out.println();
    insertVHDLTemplate("  ",out,"cu_statetrans_net_comp",replaceMap);
    out.println();
    insertVHDLTemplate("  ",out,"cu_output_net_comp",replaceMap);
    out.println("BEGIN");
    insertVHDLTemplate("  ",out,"cu_architecture_body",replaceMap);
    out.println("END struct;");
  }

  /**
   * emit combinatorial circuit for state transition function
   * (assumes RTProgram do be low level)
   */
  public void emitStateTransitionNet(PrintWriter out) {
    int stateWidth = RTSimGlobals.ld(statSeq.getLength());
    int condSigWidth = signalsData.getInputSignalCount();
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println();
    out.println("ENTITY "+componentName+"_cu_statetrans_net IS");
    out.println("  PORT(");
    out.println("    I         : IN  std_logic_vector(0 TO "+(condSigWidth-1)+");");
    out.println("    STATE     : IN  std_logic_vector("+(stateWidth-1)+" DOWNTO 0);");
    out.println("    NEXTSTATE : OUT std_logic_vector("+(stateWidth-1)+" DOWNTO 0)");
    out.println("  );");
    out.println("  CONSTANT endstate : std_logic_vector("+(stateWidth-1)+" DOWNTO 0) := \""
                + RTSimGlobals.int2bitVectorString(statSeq.getLength(),stateWidth)
                + "\";");
    out.println("END "+componentName+"_cu_statetrans_net;");
    out.println();
    out.println("ARCHITECTURE behavioural OF "+componentName+"_cu_statetrans_net IS");
    out.println("BEGIN");
    statSeq.emitStateTransitionProcess("  ",out,signalsData,stateWidth);
    out.println("END behavioural;");
  }
   
  /**
   * emit combinatorial circuit for output function
   * (assumes RTProgram do be low level)
   */
  public void emitStateOutputNet(PrintWriter out) {
    int stateWidth = RTSimGlobals.ld(statSeq.getLength());
    int condSigWidth = signalsData.getInputSignalCount();
    int cntrSigWidth = signalsData.getControlSignalCount();
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println();
    out.println("ENTITY "+componentName+"_cu_output_net IS");
    out.println("  PORT(");
    out.println("    I     : IN  std_logic_vector(0 TO "+(condSigWidth-1)+");");
    out.println("    STATE : IN  std_logic_vector("+(stateWidth-1)+" DOWNTO 0);");
    out.println("    C     : OUT std_logic_vector(0 TO "+(cntrSigWidth-1)+")");
    out.println("  );");
    out.println("END "+componentName+"_cu_output_net;");
    out.println();
    out.println("ARCHITECTURE behavioural OF "+componentName+"_cu_output_net IS");
    out.println("BEGIN");
    statSeq.emitStateOutputProcess("  ",out,signalsData,stateWidth);
    out.println("END behavioural;");
  }
 
  public void emitControlUnitEntity(PrintWriter out) {
    Bus b;
    int i;
    out.println("ENTITY "+componentName+"_cu IS");
    out.println("  PORT(");
    out.print("    CLK : IN std_logic");
    i = signalsData.getControlSignalCount();
    if(i > 0) out.println(";");
      out.print("    C : OUT std_logic_vector(0 TO "+(i-1)+")");
    i = signalsData.getInputSignalCount();
    if(i > 0) out.println(";");
      out.print("    I : IN  std_logic_vector(0 TO "+(i-1)+")");
    out.println(); out.println("  );");
    out.println("END "+componentName+"_cu;");
  }

  private void emitPorts(PrintWriter out) {
    emitPorts(out,false);
  }

  private void emitPortsReverse(PrintWriter out) {
    emitPorts(out,true);
  }

  private void emitPorts(PrintWriter out, boolean reverse) {
    Bus b;
    String s_in = reverse?"OUT":"IN ";
    String s_out = reverse?"IN ":"OUT";
    out.print("    CLK, RESET : IN std_logic");
    for(Iterator it=inBuses.iterator();it.hasNext();) {
      b = (Bus) it.next();
      out.println(";");
      out.print("    "+b.getVHDLPortName()+" : "+s_in+" "+b.getVHDLType());
    }
    for(Iterator it=outBuses.iterator();it.hasNext();) {
      b = (Bus) it.next();
      out.println(";");
      out.print("    "+b.getVHDLPortName()+" : "+s_out+" "+b.getVHDLType());
    }
  }

  private void emitVHDLFunctionForceSL(String indent, PrintWriter out) {
    out.println(indent+"FUNCTION forceSL (b : std_logic) RETURN std_logic IS");
    out.println(indent+"BEGIN");
    out.println(indent+"  CASE b IS");
    out.println(indent+"    WHEN '1'|'H' => RETURN '1';");
    out.println(indent+"    WHEN OTHERS => RETURN '0';");
    out.println(indent+"  END CASE;");
    out.println(indent+"END forceSL;");
  }

  private void emitVHDLFunction_slv2n(String indent, PrintWriter out) {
    out.println(indent+"FUNCTION slv2n(slv : IN std_logic_vector) "
		+"RETURN natural IS");
    out.println(indent+"  VARIABLE back : natural := 0;");
    out.println(indent+"BEGIN");
    out.println(indent+"  FOR i IN slv'RANGE LOOP");
    out.println(indent+"    back := back * 2;");
    out.println(indent+"    IF To_Bit(slv(i))='1' THEN back := back + 1; "
		+ "END IF;");
    out.println(indent+"  END LOOP;");
    out.println(indent+"  RETURN back;");
    out.println(indent+"END slv2n;");
  }

  public void emitOperationUnit(PrintWriter out) {
    Enumeration e;
    Iterator it;
    Register r;
    Bus b;
    RegisterArray ra;
    Memory m;
    int i, condSigCount, output_max;
    out.println("-- OPERATION UNIT");
    out.println();
    signalsData.emitCircuits("",out,componentName);
    out.println();
    out.println("-- register logic circuits");
    out.println();
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      signalsData.emitRegisterLogicCircuit("",out,r);
    }    
    out.println();
    for(e=regarrays.elements();e.hasMoreElements();) {
        ra = (RegisterArray) e.nextElement();
        signalsData.emitRegisterLogicCircuit("",out,ra);
      }    
      out.println();
    for(e=buses.elements();e.hasMoreElements();) {
      b = (Bus) e.nextElement();
      b.setDrivers(signalsData);
    }
    if(RTOptions.emitZeroDriver) {
      out.println("-- bus zero driver logic circuits");
      out.println();
      for(e=buses.elements();e.hasMoreElements();) {
        b = (Bus) e.nextElement();
        if(!b.incoming()) b.emitZeroDriverCircuit("",out);
      }
      out.println();
    }
    if(!memories.isEmpty()) {
      out.println("-- components for internal SRAM memory");
      out.println();
      insertVHDLTemplate(out,"sram_cell");
      out.println();
      insertVHDLTemplate(out,"mux");
      out.println();
      insertVHDLTemplate(out,"demux");
      out.println();
      insertVHDLTemplate(out,"sram_array");
      out.println();
      insertVHDLTemplate(out,"sram_control");
      out.println();
    }
    out.println("LIBRARY ieee;");
    out.println("USE ieee.std_logic_1164.ALL;");
    out.println();
    out.println("ENTITY "+componentName+"_ou IS");
    out.println("  PORT(");
    emitPorts(out);
    i = signalsData.getControlSignalCount();
    if(i > 0) {
      out.println(";");
      out.print("    C : IN  std_logic_vector(0 TO "+(i-1)+")");
    }
    condSigCount = signalsData.getInputSignalCount();
    if(condSigCount > 0) {
      out.println(";");
      out.print("    I : OUT std_logic_vector(0 TO "+(condSigCount-1)+")");
    }
    out.println(); out.println("  );");
    out.println("END "+componentName+"_ou;");
    out.println();
    out.println("ARCHITECTURE struct OF "+componentName+"_ou IS");
    out.println("  -- signal declarations");
    out.println("  SIGNAL CLK_SIG, RESET_SIG : std_logic;");
    out.println("  SIGNAL C_SIG : std_logic_vector(0 TO "+(signalsData.getControlSignalCount()-1)+");");
    if(condSigCount > 0) {
      for(i=0;i<condSigCount;i++) {
        output_max = signalsData.getExpressionByConditionSignal(i).getWidth()-1;
        out.println("  SIGNAL I"+i+" : std_logic_vector("+output_max+" DOWNTO 0);");
      }
    }
    out.println();
    emitArrayStructure(out,"  ");
    out.println();
    for(e=buses.elements();e.hasMoreElements();) {
      b = (Bus) e.nextElement();
      b.emitVHDLSignalDeclaration("  ",out);
    }
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      r.emitVHDLSignalDeclarations("  ",out);
    }
    for(e=regarrays.elements();e.hasMoreElements();) {
    	ra = (RegisterArray) e.nextElement();
    	ra.emitVHDLSignalDeclarations("  ",out);
    }
    for(e=memories.elements();e.hasMoreElements();) {
      m = (Memory) e.nextElement();
      m.emitVHDLSignalDeclarations("  ",out);
    }
    out.println();
    out.println("  -- D-flipflop register component declaration");
    insertVHDLTemplate("  ",out,"dff_reg_comp");
    out.println();
    out.println("  -- register logic component declarations");
    out.println();
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      signalsData.emitRegisterLogicCircuitComp("  ",out,r);
      out.println();
    }
    out.println();
    out.println("  -- register array logic component declarations");
    out.println();
    for(e=regarrays.elements();e.hasMoreElements();) {
      ra = (RegisterArray) e.nextElement();
      signalsData.emitRegisterLogicCircuitComp("  ",out,ra);
      out.println();
    }
    out.println();
    if(RTOptions.emitZeroDriver) {
      out.println("  -- bus zero driver logic component declarations");
      out.println();
      for(e=buses.elements();e.hasMoreElements();) {
        b = (Bus) e.nextElement();
        if(!b.incoming()) {
          b.emitZeroDriverComponent("  ",out);
          out.println();
        }
      }
      out.println();
    }
    if(!memories.isEmpty()) {
      out.println("  -- memory component declarations");
      insertVHDLTemplate("  ",out,"sram_array_comp");
      out.println();
      insertVHDLTemplate("  ",out,"sram_control_comp");
      out.println();
    }
    insertVHDLTemplate("  ",out,"tristate_comp");
    out.println();
    if(RTOptions.forceInputs) {
      out.println("  -- function for input forcing (to 0 and 1)");
      emitVHDLFunctionForceSL("  ",out);
      out.println();
    }
    signalsData.emitDecls("  ",out,componentName);
    out.println();
    out.println("BEGIN");
    out.println();
    out.println("  CLK_SIG <= CLK; RESET_SIG <= RESET; C_SIG <= C;");
    out.println();
    out.println("  -- register logic instantiations");
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      out.println("  -- register "+r.getIdStr());
      r.emitVHDLInstantiation("  ",out);
      out.println();
      signalsData.emitRegisterLogicInstantiation("  ",out,r);
      out.println();
    }
    for(e=regarrays.elements();e.hasMoreElements();) {
        ra = (RegisterArray) e.nextElement();
        out.println("  -- register array "+ra.getIdStr());
        ra.emitVHDLInstantiation("  ",out);
        out.println();
        signalsData.emitRegisterLogicInstantiation("  ",out,ra);
        out.println();
      }
    if(!inBuses.isEmpty()) {
      out.println("  -- forwarding of incoming buses");
      for(e=buses.elements();e.hasMoreElements();) {
        b = (Bus) e.nextElement();
        b.emitVHDLInBusAssignment("  ",out);
      }
      out.println();
    }
    if(!outBuses.isEmpty()) {
      out.println("  -- switching of outgoing buses");
      for(e=buses.elements();e.hasMoreElements();) {
        b = (Bus) e.nextElement();
        b.emitVHDLOutBusAssignment("  ",out);
      }
    }
    if(!buses.isEmpty()) {
      out.println("  -- bus zero driver logic logic instantiations");
      for(e=buses.elements();e.hasMoreElements();) {
        b = (Bus) e.nextElement();
        if(!b.incoming()) b.emitZeroDriverInstantiation("  ",out);
      }
      out.println();
    }
    if(!memories.isEmpty()) {
      out.println("  -- memory instantiations");
      for(e=memories.elements();e.hasMoreElements();) {
        m = (Memory) e.nextElement();
        m.emitInstantiation("  ",out);
        out.println();
      }
      out.println();
    }
    signalsData.emitInstantiations("  ",out,componentName);
    out.println("END struct;");
  }

  public void emitArrayStructure(PrintWriter out, String indent){
	  RegisterArray ra;
	  for(Enumeration e=regarrays.elements();e.hasMoreElements();){
		  ra = (RegisterArray) e.nextElement();
//		  Register r = ra.getReference();
//		  RegBusReference f = new RegBusReference(r,r.offset,r.offset+r.width-1);
//		  String s = f.bitRange.toVHDLArray();
//  		  out.println(indent+"TYPE reg_array"+s+" IS ARRAY(0 TO "+(int)(Math.pow(2,(r.width))-1)+") OF "
//  				+ra.getBitRange().toVHDLType(false,null)+";");
	  }
  }
  
  public void emitOperationUnitArchitectureBehavioural(PrintWriter out) {
    Enumeration e;
    Iterator it;
    Register r;
    Bus b;
    int maxWidth = signalsData.getMaximumExpressionWidth();
    out.println("ARCHITECTURE behavioural OF "+componentName+"_ou IS");
    out.println("BEGIN");
    out.println("  PROCESS");
    out.println("    FUNCTION bool2slv (b : boolean) RETURN std_logic_vector"+
		" IS");
    out.println("    BEGIN");
    out.println("      IF b THEN RETURN \"1\"; ELSE RETURN \"0\"; END IF;");
    out.println("    END bool2slv;");
    out.println("    FUNCTION evalLast (v : std_logic_vector) RETURN "+
		"std_logic IS");
    out.println("    BEGIN");
    out.println("      IF (v and \"1\") = \"1\" THEN RETURN '1'; "+
		"ELSE RETURN '0'; END IF;");
    out.println("    END evalLast;");
    out.println("    VARIABLE C_in : std_logic_vector(0 TO "
		+(signalsData.getControlSignalCount()-1)+");");
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      out.println("    VARIABLE reg_"+r.getIdStr()+", reg_"+r.getIdStr()+
		  "_new : " + r.getVHDLType() + " := (OTHERS => '0');");
    }
    for(e=buses.elements();e.hasMoreElements();) {
      b = (Bus) e.nextElement();
      out.println("    VARIABLE bus_"+b.getIdStr()+" : "+b.getVHDLType()
		  + " := (OTHERS => '0');");
    }
    out.println("    VARIABLE temp : std_logic_vector("+(maxWidth-1)
		+" DOWNTO 0) := (OTHERS => '0');");
    out.println("  BEGIN");
    out.println("    LOOP");
    out.println("      WAIT UNTIL CLK='0';");
    out.println("      WAIT ON C;");
    out.println("      C_in := C;");
    // alle nicht eingehenden Busse auf 0
    for(e=buses.elements();e.hasMoreElements();) {
      b = (Bus) e.nextElement();
      if(!inBuses.contains(b))
	out.println("      bus_"+b.getIdStr()+" := (OTHERS => '0');");
    }
    // durch Kontrollsignale bedingte Zuweisungen an Busse
    signalsData.emitBusAssignments("      ",out);
    // ausgehende Busse schreiben
    for(it=outBuses.iterator();it.hasNext();) {
      b = (Bus) it.next();
      out.println("      "+b.getIdStr()+" <= bus_"+b.getIdStr()+";");
    }
    out.println("      WAIT UNTIL CLK='1';");
    // Eingehende Busse lesen
    for(it=inBuses.iterator();it.hasNext();) {
      b = (Bus) it.next();
      out.println("      bus_"+b.getIdStr()+" := "+b.getIdStr()+";");
    }
    // Alle anderen Operationen
    signalsData.emitRTOperations("      ",out);
    // Wertuebernahme Register und Ruecksetzen der Eingaenge
    for(e=registers.elements();e.hasMoreElements();) {
      r = (Register) e.nextElement();
      out.println("      reg_"+r.getIdStr()+" := reg_"+r.getIdStr()+"_new;");
      out.println("      reg_"+r.getIdStr()+"_new := (OTHERS => '0');");
    }
    // Berechnung der I-Signale
    signalsData.emitInputSignalsCalculation("      ",out);
    out.println("    END LOOP;");
    out.println("  END PROCESS;");
    out.println("END behavioural;");
  }

  /**
   * setzt das RT-Program zur&uuml;ck. Der Speicher wird nicht gel&ouml;scht.
   */
  public void reset() {
    pc.reset();
    for(Enumeration regElems = registers.elements();regElems.hasMoreElements();)
      ((Register) regElems.nextElement()).clear();
    for(Enumeration busElems = buses.elements();busElems.hasMoreElements();)
      ((Bus) busElems.nextElement()).clear();
    for(Enumeration regArrayElems = regarrays.elements();regArrayElems.hasMoreElements();)
    	((RegisterArray) regArrayElems.nextElement()).clear();
    initStats();
    currentStatement = null;
  }

  /**
   * setzt alle Speicher zur&uuml;ck
   */
  public void memoryReset() {
    for(Enumeration memElems = memories.elements();memElems.hasMoreElements();)
          ((Memory) memElems.nextElement()).clear();
  }

  /**
   * @return true, falls das Programmende erreicht wurde, false sonst
   */
  public boolean terminated() { return pc.getStatSeqIndex() == statSeqLength; }

  private void initStats() {
    if(!terminated()) {
      if(statSeq.has2Edges(pc.getStatSeqIndex())) 
        edgeType = RTSimGlobals.OSTAT_TYPE_2EDGE_1; 
      else
        edgeType = RTSimGlobals.OSTAT_TYPE_MEALY;
      stats = statSeq.getStatementsOrderedAt(edgeType,pc.getStatSeqIndex(),pc.getParStatsIndex(), registers, regarrays);
      statsIt = stats.listIterator();
    }
    else {
      stats = null;
      statsIt = null;
    }
  }

  private void fetchStatements2() {
    if(edgeType != RTSimGlobals.OSTAT_TYPE_2EDGE_1) return;
    edgeType = RTSimGlobals.OSTAT_TYPE_2EDGE_2;
    stats = statSeq.getStatementsOrderedAt(edgeType,pc.getStatSeqIndex(),0, registers, regarrays);
    statsIt = stats.listIterator();
  }

  /**
   * F&uuml;hrt den Taktwechsel aus
   */
  private void cycleChange() {
    for(Enumeration regElems = registers.elements();regElems.hasMoreElements();)
        ((Register) regElems.nextElement()).commit();
    for(Enumeration memElems = memories.elements();memElems.hasMoreElements();)
        ((Memory) memElems.nextElement()).clearWritten();
    pc.commit();
    initStats();
    ifStat=false;
    // erst nach dem initialisieren der Statements Bus-Inhalte loeschen, da
    // Werte in If-Anweisungen abgefragt werden (Signale)
    for(Enumeration busElems = buses.elements();busElems.hasMoreElements();)
        ((Bus) busElems.nextElement()).clear();
  }

  /**
   * aktualisiert den angezeigten Inhalt der Busse (f&uuml;r Microstep)
   */
  private void busesUpdate() {
    for(Enumeration busElems = buses.elements();busElems.hasMoreElements();)
        ((Bus) busElems.nextElement()).updateShowContent();
  }

  /**
   * F&uuml;hrt einen Taktzyklus aus
   * @return true bei korrekter Ausf&uuml;hrung, false bei Fehler
   */
  public boolean step() {
    if(terminated()) return true;
    /*if(statSeq.execAt(pc.getStatSeqIndex(),pc.getParStatsIndex())) {
      cycleChange(); 
      return true;
    }
    else return false; */
    Statement st;
    while(statsIt.hasNext()) {
      st = (Statement) statsIt.next();
      statPos = st.getPositionRange();
      if(!st.exec()) return false;
      currentStatement = st;
    }
    if(edgeType == RTSimGlobals.OSTAT_TYPE_2EDGE_1) {
      fetchStatements2();
      while(statsIt.hasNext()) {
        st = (Statement) statsIt.next();
        statPos = st.getPositionRange();
        if(!st.exec()) return false;
        currentStatement = st;
      }
    }
    cycleChange();
    return true;
  }

  /**
   * @return gibt die StatementSequence zur&uuml;ck
   */
  public StatementSequence getStatementSequence() { return statSeq; }

  /**
   * @return gibt den aktuellen "Programmz&auml;hler" zur&uuml;ck
   */
  public int getStatSeqIndex() { return pc.getStatSeqIndex(); }

  public PositionRange getCurrentPositionRange() {
    return statSeq.getPositionRangeAt(pc.getStatSeqIndex(),pc.getParStatsIndex());
  }

  public Statement getCurrentStatement() { 
	  return currentStatement; 
  }

  public PositionRange getPositionRangeAt(int si, int pi) {
    return statSeq.getPositionRangeAt(si,pi);
  }

  public boolean microStep() {
	  if(!ifStat) {
	  initStats();
	  ifStat=true;
	  }
    if(terminated()) return true;
    if(!statsIt.hasNext()) {
      if(edgeType == RTSimGlobals.OSTAT_TYPE_2EDGE_1) fetchStatements2();
      if(!statsIt.hasNext()) {
        // statement-loser Takt
        statPos = getCurrentPositionRange();
        cycleChange();
        return true;
      }
    }
    Statement st = (Statement) statsIt.next();
    statPos = st.getPositionRange();
    
    if(st.exec()) {
      busesUpdate();
      currentStatement = st;
      if(!statsIt.hasNext()) { // Taktwechsel
      if(edgeType == RTSimGlobals.OSTAT_TYPE_2EDGE_1) fetchStatements2();
      else cycleChange();
      }
      return true;
    }
    else {
      return false;
    }
  }
 
  public PositionRange getCurrentStatementPositionRange() { 
    return statPos;
  }

  public String getErrorMessage() {
    if(pc.hasRuntimeError()) return pc.getErrorMessage();   
    else return "unbekannter Laufzeit-Fehler";
  }

  public PositionRange getErrorPosition() {
    if(pc.hasRuntimeError()) return pc.getErrorPosition();
    else return new PositionRange(0,0,0,0);
  }

  public int getParStatsIndexAtPosition(int line, int column) {
    return statSeq.getParStatsIndexAtPosition(line,column);
  }

  public int getCycleCount() { return pc.getCycleCount(); }

  public String toNoArrayString(String s) {
	    ListIterator it;
	    Bus b;
	    String bk = "";
	    if(nameSet) bk = "component "+componentName+";\n\n";
	    if(! buses.isEmpty()) {
	      for(it=busOrder.listIterator();it.hasNext();) {
	        b = (Bus) it.next();
	        bk += "declare ";
	        if(b.incoming()) bk += "in  ";
	        else { if(b.outgoing()) bk += "out ";}
	        bk += "bus " + b.getPrettyDecl() + ";\n";
	      }
	      bk += "\n";
	    }
	    if(! registers.isEmpty()) {
	      for(it=registerOrder.listIterator();it.hasNext();) 
		bk += "declare register "+ ((Register) it.next()).getPrettyDecl()
	           + ";\n";
	      bk += "\n";
	    }
	    bk+=s;
	    if(! memories.isEmpty()) {
	      for(it=memoryOrder.listIterator();it.hasNext();)
		bk += "declare memory " + ((Memory) it.next()).getPrettyDecl()+";\n";
	      bk += "\n";
	    }
	    bk += statSeq.toString(registers, regarrays) + "\n";
	    return bk;
	  }
  
  public String toString() {
    ListIterator it;
    Bus b;
    String bk = "";
    if(nameSet) bk = "component "+componentName+";\n\n";
    if(! buses.isEmpty()) {
      for(it=busOrder.listIterator();it.hasNext();) {
        b = (Bus) it.next();
        bk += "declare ";
        if(b.incoming()) bk += "in  ";
        else { if(b.outgoing()) bk += "out ";}
        bk += "bus " + b.getPrettyDecl() + ";\n";
      }
      bk += "\n";
    }
    if(! registers.isEmpty()) {
      for(it=registerOrder.listIterator();it.hasNext();) 
	bk += "declare register "+ ((Register) it.next()).getPrettyDecl()
           + ";\n";
      bk += "\n";
    }
    if(! memories.isEmpty()) {
      for(it=memoryOrder.listIterator();it.hasNext();)
	bk += "declare memory " + ((Memory) it.next()).getPrettyDecl()+";\n";
      bk += "\n";
    }
    if(! regarrays.isEmpty()) {
    	for(it=regarOrder.listIterator();it.hasNext();)
    bk += "declare register array " + ((RegisterArray) it.next()).getFullDecl()+";\n";
    	bk += "\n";
    }
    bk += statSeq.toString(registers, regarrays) + "\n";
    return bk;
  }

  public SignalsData getSignalsData() { return signalsData; }

  public void insertVHDLTemplate(PrintWriter out,String templateName) {
    insertVHDLTemplate("",out,templateName);
  }

  public void insertVHDLTemplate(PrintWriter out, String templateName, String replaceMap[][]) {
    insertVHDLTemplate("",out,templateName,replaceMap);
  }

  public void insertVHDLTemplate(String indent, PrintWriter out, String templateName) {
    insertVHDLTemplate(indent,out,templateName,new String[0][0]);
  }

  public void insertVHDLTemplate(String indent, PrintWriter out, String templateName, String[][] replaceMap) {
    int i;
    int rml = replaceMap.length;
    try 
    {
      BufferedReader tin = new BufferedReader(new InputStreamReader(
        getClass().getResource("/vhdltmpl/"+templateName+".vhd").openStream()));
      String line = tin.readLine();
      while(line != null) {
        for(i=0;i<rml;i++) line = line.replaceAll(replaceMap[i][0],replaceMap[i][1]);
        out.println(indent+line);
        line = tin.readLine();
      }
    } 
    catch(Throwable t) {
    	out.println("-- ERROR while inserting template '"+templateName+".vhd");
      System.err.println("INTERNAL ERROR while reading '"+templateName+".vhd: "
       + t.getMessage());
    }
  }
}
