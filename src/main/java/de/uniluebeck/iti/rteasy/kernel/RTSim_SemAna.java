package de.uniluebeck.iti.rteasy.kernel;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.frontend.ASTBit_Seq;
import de.uniluebeck.iti.rteasy.frontend.ASTCaseList;
import de.uniluebeck.iti.rteasy.frontend.ASTDecl;
import de.uniluebeck.iti.rteasy.frontend.ASTDecls;
import de.uniluebeck.iti.rteasy.frontend.ASTExpr;
import de.uniluebeck.iti.rteasy.frontend.ASTIf_Stat;
import de.uniluebeck.iti.rteasy.frontend.ASTInner_ParStats;
import de.uniluebeck.iti.rteasy.frontend.ASTMemDecl;
import de.uniluebeck.iti.rteasy.frontend.ASTMemDeclList;
import de.uniluebeck.iti.rteasy.frontend.ASTOuter_ParStats;
import de.uniluebeck.iti.rteasy.frontend.ASTRegArrayDecl;
import de.uniluebeck.iti.rteasy.frontend.ASTRegArrayDeclList;
import de.uniluebeck.iti.rteasy.frontend.ASTRegBusDecl;
import de.uniluebeck.iti.rteasy.frontend.ASTRegBusDeclList;
import de.uniluebeck.iti.rteasy.frontend.ASTRtProg;
import de.uniluebeck.iti.rteasy.frontend.ASTStat;
import de.uniluebeck.iti.rteasy.frontend.ASTStat_Seq;
import de.uniluebeck.iti.rteasy.frontend.ASTSwitch_Case_Stat;
import de.uniluebeck.iti.rteasy.frontend.IASTBit_Seq;
import de.uniluebeck.iti.rteasy.gui.IUI;
import de.uniluebeck.iti.rteasy.gui.RTOptions;

public class RTSim_SemAna {

  class BitSeqCheckInfo {
    // Tupel-Typ fuer Fehleranz. und Breite in Bit-Sequenzen
    public int errorCount, width;
    BitSeqCheckInfo() {
      errorCount = 0;
      width = 1;
    }
    BitSeqCheckInfo(int ec, int wi) {
      errorCount = ec;
      width = wi;
    }
  } 

  Hashtable memories = new Hashtable();
  Hashtable labels = new Hashtable();
  LinkedList gotoLabels = new LinkedList();
  LinkedList gotoPositions = new LinkedList();
  LinkedList gotoASTRefs = new LinkedList();
  Hashtable buses = new Hashtable();
  HashSet inBuses = new HashSet();
  HashSet outBuses = new HashSet();
  Hashtable registers = new Hashtable();
  Hashtable regarrays = new Hashtable();
  Hashtable comparators = new Hashtable();
  
  LinkedList memoryOrder = new LinkedList();
  LinkedList registerOrder = new LinkedList();
  LinkedList busOrder = new LinkedList();
  LinkedList regBusList = new LinkedList();
  LinkedList regArrayOrder = new LinkedList();

  LinkedList errorMessages = new LinkedList();
  LinkedList errorPositions = new LinkedList();
  LinkedList warningMessages = new LinkedList();
  LinkedList warningPositions = new LinkedList();

  int previousType;
  PositionRange previousPos;
  String previousTypeStr;
  String previousPosStr;
  int regBusCount;
  

  public RTSim_SemAna() {
  }

  public Hashtable getRegisters() { return registers; }
  public Hashtable getBuses() { return buses; }
  public Hashtable getMemories() { return memories; }
  public Hashtable getLabels() { return labels; }
  public HashSet getInBuses() { return inBuses; }
  public HashSet getOutBuses() { return outBuses; }
  public Hashtable getRegArrays() {return regarrays;}
  public Hashtable getComps() {return comparators;}
  public LinkedList getRegisterOrder() { return registerOrder; }
  public LinkedList getBusOrder() { return busOrder; }
  public LinkedList getMemoryOrder() { return memoryOrder; }
  public LinkedList getRegArrayOrder() {return regArrayOrder;}
  public int getRegBusCount() { return regBusCount; }
  public RegBus[] getRegBusList() {
    RegBus rbarray[] = new RegBus[1];
    return (RegBus[]) regBusList.toArray(rbarray);
  }

  public boolean checkSymbol(String s) {
    if(registers.containsKey(s)) {
      previousType = RTSimGlobals.REGISTER;
      previousPos = getRegister(s).getPositionRange();
      previousTypeStr = "register";
      previousPosStr = previousPos.toString();
      return false;
    }
    else if(buses.containsKey(s)) {
      previousType = RTSimGlobals.BUS;
      previousPos = getBus(s).getPositionRange();
      previousTypeStr = "bus";
      previousPosStr = previousPos.toString();
      return false;
    }
    else if(memories.containsKey(s)) {
      previousType = RTSimGlobals.MEMORY;
      previousPos = getMemory(s).getPositionRange();
      previousTypeStr = "memory";
      previousPosStr = previousPos.toString();
      return false;
    }
    else if (regarrays.containsKey(s)) {
    	previousType = RTSimGlobals.ARRAY;
    	previousPos = getRegArray(s).getPositionRange();
    	previousTypeStr = "registerarray";
    	previousPosStr = previousPos.toString();
    	return false;
    }
    else if(labels.containsKey(s)) {
      previousType = RTSimGlobals.LABEL;
      previousPos = getLabel(s).getPositionRange();
      previousTypeStr = "label";
      previousPosStr = previousPos.toString();
      return false;
    }
    return true;
  }

  public void insertSymbolPreviouslyDeclaredError(String s,String typeStr,PositionRange pr) {
    String errStr = IUI.get("ERROR_SYMBOL_PREV_DECL");
    errStr = errStr.replaceAll("%%TYPE",typeStr);
    errStr = errStr.replaceAll("%%SYMBOL",s);
    errStr = errStr.replaceAll("%%PREVTYPE",previousTypeStr);
    errStr = errStr.replaceAll("%%PREVPOS",previousPosStr);
    insertError(errStr,pr);
  }

  public Register insertRegister(ASTRegBusDecl regdecl) {
    String s = regdecl.getName();
    if(checkSymbol(s)) { 
      Register r = new Register(regdecl);
      r.setRegBusId(regBusCount++);
      registers.put(s,r);
      registerOrder.add(r);
      regBusList.add(r);
      return r;
    }
    else return null;
  }

  public Register getRegister(String s) { return (Register) registers.get(s); }

  public Bus insertBus(ASTRegBusDecl busdecl) {
    String s = busdecl.getName();
    if(checkSymbol(s)) {
      Bus b = new Bus(busdecl);
      b.setRegBusId(regBusCount++);
      buses.put(s,b);
      busOrder.add(b);
      regBusList.add(b);
      return b;
    }
    else return null;
  }

  public Bus getBus(String s) { return (Bus) buses.get(s); }

  public Memory insertMemory(String s, PositionRange tpr, Register addrReg, Register dataReg) {
    if(checkSymbol(s)) {
      Memory m = new Memory(s,tpr,addrReg,dataReg);
      memories.put(s,m);
      memoryOrder.add(m);
      return m;
    } 
    else return null;
  }

  public Memory getMemory(String s) { return (Memory) memories.get(s); }  

  public RegisterArray insertRegArray(ASTRegArrayDecl radecl){
	  String s = radecl.getName();
	  if(checkSymbol(s)){
		  RegisterArray ra = new RegisterArray(radecl);
		  ra.setRegBusId(regBusCount++);
		  regarrays.put(s,ra);
		  regArrayOrder.add(ra);
		  return ra;
	  }
	  else return null;
  }
  
  public RegisterArray getRegArray(String s) {
	  return (RegisterArray) regarrays.get(s);
	  }
  
  public Label insertLabel(String s, PositionRange tpr) {
    if(checkSymbol(s)) {
      Label l = new Label(s,tpr);
      labels.put(s,l);
      return l;
    }
    else return null;
  } 

  public Label getLabel(String s) { return (Label) labels.get(s); }

  public void insertGoto(String s, PositionRange tpr, ASTStat stat) {
    gotoLabels.add(s);
    gotoPositions.add(tpr);
    gotoASTRefs.add(stat);
  }

  public void insertError(String msg, PositionRange tpr) {
    errorMessages.add(msg);
    errorPositions.add(tpr);
  }

  public void insertWarning(String msg, PositionRange tpr) {
    warningMessages.add(msg);
    warningPositions.add(tpr);
  }

  public LinkedList getErrorMessages() { return errorMessages; }
  public LinkedList getErrorPositions() { return errorPositions; }
  public LinkedList getWarningMessages() { return warningMessages; }
  public LinkedList getWarningPositions() { return warningPositions; }

  public void dumpErrors(PrintStream out) {
    ListIterator msgIt = errorMessages.listIterator(0);
    ListIterator posIt = errorPositions.listIterator(0);

    while(msgIt.hasNext() && posIt.hasNext()) 
      out.println(((PositionRange) posIt.next()).toString()+": "+((String) msgIt.next()));
  }

  public void dumpWarnings(PrintStream out) {
    ListIterator msgIt = warningMessages.listIterator(0);
    ListIterator posIt = warningPositions.listIterator(0);

    while(msgIt.hasNext() && posIt.hasNext())
      out.println(((PositionRange) posIt.next()).toString()+": "+((String) msgIt.next()));
  }

  public int checkGotos() {
    int errorCount = 0;
    ListIterator posIt = gotoPositions.listIterator(0);
    ListIterator labelIt = gotoLabels.listIterator(0);
    ListIterator refIt = gotoASTRefs.listIterator(0);

    while(posIt.hasNext() && labelIt.hasNext() && refIt.hasNext()) {
      String idStr = (String) labelIt.next();
      PositionRange pr = (PositionRange) posIt.next();
      ASTStat stat = (ASTStat) refIt.next(); 
      Label l = getLabel(idStr);
      if(l == null) {
        errorCount++;
        String errStr = IUI.get("ERROR_NO_LABEL_FOR_GOTO");
        errStr = errStr.replaceAll("%%LABEL",idStr);
        insertError(errStr,pr);
      }
      else stat.setLabel(l);
    }
    
    return errorCount;
  }  

  public int checkRTProgram(ASTRtProg rtprog) {
    int errorCount = 0;
    int regBusCount = 0;
    errorCount += checkDeclarations(rtprog.getDeclarations());
    errorCount += checkStatementSequence(rtprog.getStatementSequence());
    errorCount += checkGotos();
    return errorCount;
  }

  public int checkDeclarations(ASTDecls decls) {
    if(decls == null) return 0;
    int errorCount = checkDeclaration(decls.getDeclaration());
    if(decls.hasNext()) errorCount += checkDeclarations(decls.next());
    return errorCount;
  }

  public int checkDeclaration(ASTDecl decl) {
    int i = decl.getDeclType();
    if(i==RTSimGlobals.REGISTER) return checkRegisterDecls(decl.getRegisterDecls()); 
    else if(i==RTSimGlobals.BUS) return checkBusDecls(decl.getBusDecls(),
                                                      decl.getSignalDirection());
    else if(i==RTSimGlobals.MEMORY) return checkMemoryDecls(decl.getMemoryDecls());
    else if(i==RTSimGlobals.ARRAY) return checkRegArrayDecls(decl.getRegArrayDecls());
    else insertError(IUI.get("INTERNAL_ERROR"),decl.getPositionRange()); 
    return 1;
  }

  public int checkRegisterDecls(ASTRegBusDeclList regBusDeclList) {
    int errorCount = checkRegisterDecl(regBusDeclList.getRegBusDecl());
    if(regBusDeclList.hasNext()) errorCount += checkRegisterDecls(regBusDeclList.next());
    return errorCount;
  }

  public int checkBusDecls(ASTRegBusDeclList regBusDeclList) {
    return checkBusDecls(regBusDeclList,-1);
  }

  public int checkBusDecls(ASTRegBusDeclList regBusDeclList, int signalDirection) {
    int errorCount = checkBusDecl(regBusDeclList.getRegBusDecl(),
                                  signalDirection);
    if(regBusDeclList.hasNext()) errorCount +=
      checkBusDecls(regBusDeclList.next(),signalDirection);
    return errorCount;
  }

  public int checkMemoryDecls(ASTMemDeclList memDeclList) {
    int errorCount = checkMemoryDecl(memDeclList.getMemDecl());
    if(memDeclList.hasNext()) errorCount += checkMemoryDecls(memDeclList.next());
    return errorCount;
  }

  public int checkRegisterDecl(ASTRegBusDecl regBusDecl) {
    Register r = insertRegister(regBusDecl); 
    if(r != null) {
      regBusDecl.setRegister(r);
      return 0;
    }
    else {
      insertSymbolPreviouslyDeclaredError(regBusDecl.getName(),"register",regBusDecl.getPositionRange());
      return 1;
    }
  }

  public int checkBusDecl(ASTRegBusDecl regBusDecl) {
    return checkBusDecl(regBusDecl,-1);
  }

  public int checkBusDecl(ASTRegBusDecl regBusDecl, int signalDirection) {
    Bus b = insertBus(regBusDecl);
    if(b != null) {
      if(signalDirection == RTSimGlobals.DIR_IN) {
        inBuses.add(b);
        b.setIncoming(true);
      }
      else if(signalDirection == RTSimGlobals.DIR_OUT) {
        outBuses.add(b);
        b.setOutgoing(true);
      }
      regBusDecl.setBus(b);
      return 0;
    }
    else {
      insertSymbolPreviouslyDeclaredError(regBusDecl.getName(),"bus",regBusDecl.getPositionRange());
      return 1;
    }
  }

  public int checkMemoryDecl(ASTMemDecl memDecl) {
    int errorCount = 0;
    String errStr;
    Register taddr = getRegister(memDecl.getAddrReg());
    Register tdata = getRegister(memDecl.getDataReg());
    if(taddr == null) {
      errorCount++;
      errStr = IUI.get("ERROR_MEMORY_ADDRREG_UNDECL");
      errStr = errStr.replaceAll("%%ADDRREG",memDecl.getAddrReg());
      errStr = errStr.replaceAll("%%MEMORY",memDecl.getName());
      insertError(errStr,memDecl.getPositionRange());
    }
    if(tdata == null) {
      errorCount++;
      errStr = IUI.get("ERROR_MEMORY_DATAREG_UNDECL");
      errStr = errStr.replaceAll("%%DATAREG",memDecl.getDataReg());
      errStr = errStr.replaceAll("%%MEMORY",memDecl.getName());
      insertError(errStr,memDecl.getPositionRange());
    }
    if(errorCount == 0) {
      Memory m = insertMemory(memDecl.getName(),memDecl.getPositionRange(),taddr,tdata);
      if(m != null) {
        memDecl.setMemory(m);
        return 0;
      }
      else {
        insertSymbolPreviouslyDeclaredError(memDecl.getName(),"memory",memDecl.getPositionRange());
        return 1;
      }
    }
    else return errorCount;
  }  

  public int checkRegArrayDecls(ASTRegArrayDeclList list) {
	  int errorCount = checkRegArrayDecl(list.getRegArDecl());
	    if(list.hasNext()) errorCount += checkRegArrayDecls(list.next());
	    return errorCount;
  }
  
  public int checkRegArrayDecl(ASTRegArrayDecl radecl) {
	  int errorCount = 0;
	  String err;
	  if(errorCount == 0) {
		  RegisterArray r = insertRegArray(radecl);
		  if(r != null) {
			  radecl.setRegArray(r);
			  return 0;
		  } else {
			  insertSymbolPreviouslyDeclaredError(radecl.getName(), "registerarray", radecl.getPositionRange());
		  }
	  }
	  return errorCount;
  }
  
  public int checkStatementSequence(ASTStat_Seq stat_seq) {
    int errorCount = 0;
    //Schreibzugriffe auf Register-Array pr�fen
    errorCount += checkRegArrayWriteAccess(stat_seq.getStatements());
    errorCount += checkRegArrayReadAccess(stat_seq.getStatements());
    if(stat_seq.has2Edges()) {
      errorCount += checkOuterParallelStatements(stat_seq.getStatements(),
        RTSimGlobals.OSTAT_TYPE_2EDGE_1);
      errorCount += checkOuterParallelStatements(
        stat_seq.getStatements2(),RTSimGlobals.OSTAT_TYPE_2EDGE_2);
    }
    else errorCount += checkOuterParallelStatements(stat_seq.getStatements(),
      RTSimGlobals.OSTAT_TYPE_MEALY);
    if(stat_seq.hasNext()) errorCount += checkStatementSequence(stat_seq.next());
    return errorCount;
  }

  public int checkOuterParallelStatements(ASTOuter_ParStats outer_ParStats,
    int ostat_type) {
    int errorCount = 0;
    if(outer_ParStats.hasLabel()) {
      if(ostat_type == RTSimGlobals.OSTAT_TYPE_2EDGE_2) {
        insertError(IUI.get("ERROR_NO_LABELS_AFTER_PIPE"),outer_ParStats.getPositionRange());
        errorCount++;
      }
      else {  
        Label l = insertLabel(outer_ParStats.getLabelId(),outer_ParStats.getPositionRange());
        if(l == null) {
          errorCount++;
          insertSymbolPreviouslyDeclaredError(outer_ParStats.getLabelId(),"label",outer_ParStats.getPositionRange());
        }
        else outer_ParStats.setLabel(l); 
      }
    }
    switch(outer_ParStats.getStatNodeType()) {
      case RTSimGlobals.STAT: errorCount += checkStatement(outer_ParStats.getStatement(),ostat_type); break;
      case RTSimGlobals.IFSTAT: errorCount += checkIfStatement(outer_ParStats.getIfStatement(),ostat_type); break;
      case RTSimGlobals.SWITCH:	errorCount += checkSwitchStatement(outer_ParStats.getSwitchStatement(),ostat_type); break;
      default: errorCount++;
        insertError(IUI.get("INTERNAL_ERROR2"),outer_ParStats.getPositionRange());
        break;
    }
    if(outer_ParStats.hasNext()) errorCount += checkOuterParallelStatements(outer_ParStats.next(),ostat_type);
    return errorCount;
  }

  public int checkIfStatement(ASTIf_Stat if_stat, int ostat_type) {
    int errorCount = checkExpression(if_stat.getExpression(),1);
    if(exprContainsBus(if_stat.getExpression()) && RTOptions.noisyWarnings) insertWarning(IUI.get("WARNING_BUS_IN_IFCOND"),if_stat.getExpression().getPositionRange());
    errorCount += checkInnerParallelStatements(if_stat.getThen(),ostat_type);
    if(if_stat.hasElse()) errorCount += checkInnerParallelStatements(if_stat.getElse(),ostat_type);
    return errorCount;
  }
  
  public int checkSwitchStatement(ASTSwitch_Case_Stat switch_stat, int ostat_type) {
	  comparators.clear();
	  int errorCount = 0;
	  String errStr;
	  BitRange br = switch_stat.getBitRange();
	  Register Switch = getRegister(switch_stat.getSwitch());
	  if(Switch == null) {
		  RegisterArray SwitchArray = getRegArray(switch_stat.getSwitch());
		  if(SwitchArray == null) {
			  errorCount++;
			  errStr = (IUI.get("SWITCH_UNDECL"));
			  insertError(errStr, switch_stat.getPositionRange());
		  }
		  if(br != null && !checkBitRange(br, SwitchArray, switch_stat.getPositionRange())) {
			  errorCount++;
		  }
	  } else {
		  if(br != null && !checkBitRange(br, Switch, switch_stat.getPositionRange())) {
			  errorCount++;
		  }
	  }
	  BitSeqCheckInfo ci = checkBitSequence(switch_stat.getBitSequence());
      errorCount += ci.errorCount;
	  errorCount += checkCaseList(switch_stat.getCases(),ostat_type);
	  if(switch_stat.hasDefault()) errorCount += checkDefaults(switch_stat.getDefaultCase(),ostat_type);
	  return errorCount;
  }
  
  public int checkDefaults(ASTCaseList defaults, int ostat_type){
	  int errorCount = 0;
	  errorCount +=checkInnerParallelStatements(defaults.getStatSeq(), ostat_type);
	  return errorCount;
  }
  
  public int checkCaseList(ASTCaseList caselist, int ostat_type) {
	  int errorCount = 0;
	  String errStr;
	  errorCount += checkInnerParallelStatements(caselist.getStatSeq(), ostat_type);
	  if(comparators.containsKey(caselist.getComparator())) {
		  errorCount++;
		  errStr = (IUI.get("SWITCH_DOUBLE_DECL"));
		  insertError(errStr, caselist.getPositionRange());
	  }
	  comparators.put(caselist.getComparator(),caselist.getComparator());
	  if(caselist.getHasNext()) {
		  errorCount += checkCaseList(caselist.next(), ostat_type);
	  }
	  return errorCount;
  }

  public int checkInnerParallelStatements(ASTInner_ParStats inner_ParStats, int ostat_type) {
    int errorCount = 0;
    switch(inner_ParStats.getStatNodeType()) {
      case RTSimGlobals.STAT: errorCount += checkStatement(inner_ParStats.getStatement(),ostat_type); break;
      case RTSimGlobals.IFSTAT: errorCount += checkIfStatement(inner_ParStats.getIfStatement(),ostat_type); break;
      default: errorCount++;
        insertError(IUI.get("INTERNAL_ERROR3"),inner_ParStats.getPositionRange());
        break;
    }
    if(inner_ParStats.hasNext()) errorCount += checkInnerParallelStatements(inner_ParStats.next(),ostat_type);
    return errorCount;
  }
  
 
  /**
   * �berpr�ft, ob pro Takt nur ein Schreibzugriff auf ein Register-Array stattfindet
   * @param outer_ParStats Parallele Statements innerhalb eines Taktes
   * @return
   */
  private int checkRegArrayWriteAccess(ASTOuter_ParStats outer_ParStats)
  {
	  int error = 0;
	  Vector<RegisterArray> registerArrays = new Vector<RegisterArray>();
	  ASTOuter_ParStats outer = outer_ParStats;
	  RegisterArray ra;
	  if(outer.getStatNodeType() == RTSimGlobals.STAT
			  && outer.getStatement().getStatementType() == RTSimGlobals.ASSIGN)
	  {		  
		  ra = getRegArray(outer.getStatement().getBitSequence().getTargetId());
		  if(ra != null)
		  {
			  registerArrays.add(ra);
		  } 
	  }
	  while(outer.hasNext())
	  {
		  outer = outer.next();
		  if(outer.getStatNodeType() == RTSimGlobals.STAT
					  && outer.getStatement().getStatementType() == RTSimGlobals.ASSIGN)
		  {
			  ra = getRegArray(outer.getStatement().getBitSequence().getTargetId());
				  
			  if(ra != null)
			  {
				  for(RegisterArray r:registerArrays)
				  {
					  if(ra.equals(r))
					  {
						  insertError(IUI.get("ERROR_TWO_WRITE_REGARRAY"),outer_ParStats.getPositionRange());
						  error++;						  }
					  }
					  registerArrays.add(ra);
				  }    		 	 
			  }	    		 
	  }  	    
	  
	  return error;
  }
  private int checkRegArrayReadAccess(ASTOuter_ParStats outer_ParStats)
  {
	  ASTOuter_ParStats outer = outer_ParStats;
	  Vector<RegisterArray> regArrays = new Vector<RegisterArray>();
	  Vector<Integer> readAccesses = new Vector<Integer>();
	  boolean error = false;
	  
	  if(outer.getStatNodeType() == RTSimGlobals.STAT)
	  {
		  ASTStat statement = outer.getStatement();
		  if(statement.getStatementType() == RTSimGlobals.ASSIGN)
		  {
			  if(checkRegArrayReadAccess(statement.getExpression(), regArrays, readAccesses))
			  {
				  error = true;
				  System.out.println("Fehler");
			  }
		  }
	  }
	  
	  while(outer.hasNext())
	  {
		  outer = outer.next();
		  if(outer.getStatNodeType() == RTSimGlobals.STAT)
		  {
			  ASTStat statement = outer.getStatement();
			  if(statement.getStatementType() == RTSimGlobals.ASSIGN)
			  {
				  if(checkRegArrayReadAccess(statement.getExpression(), regArrays, readAccesses))
				  {
					  error = true;
					  System.out.println("Fehler");
				  }
			  }
		  }
		  
	  }
	  if(error)
	  {
		  insertError(IUI.get("ERROR_READ_REGARRAY"),outer_ParStats.getPositionRange());
		  return 1;
	  }
	  else
	  {
		  return 0;
	  }
  }
  
  private boolean checkRegArrayReadAccess(ASTExpr e, Vector<RegisterArray> rar, Vector<Integer> rac)
  {
	  RegisterArray ra;
	  boolean error = false;
	  Vector<RegisterArray> regArrays = rar;
	  Vector<Integer> readAccesses = rac;
	  int type = e.getType();
	  switch(type) {
	      case RTSimGlobals.NUM_CONST:
	       return false;
	      case RTSimGlobals.BIT_SEQ:
	    	  ra = getRegArray(e.getBitSeq().getTargetId());
	    	  if(ra != null)
	    	  {
	    		  System.out.println(ra.getRegisterNumber());
	    		  if(regArrays.contains(ra))
	    		  {
	    			  int index = regArrays.indexOf(ra);
	    			  readAccesses.set(index, readAccesses.get(index)+1);
	    			  if(readAccesses.get(index) > 2)
	    			  {
	    				  error = true;
	    			  }
	    		  }
	    		  else
	    		  {
	    			  regArrays.add(ra);
	    			  readAccesses.add(1);
	    		  }
	    	  }
	    	  return error;
	      case RTSimGlobals.NOT:
	      case RTSimGlobals.SIGN:
	    	  return checkRegArrayReadAccess(e.getOperand(), regArrays, readAccesses);
	      case RTSimGlobals.NE:
	      case RTSimGlobals.EQ:
	      case RTSimGlobals.LT:
	      case RTSimGlobals.LE:
	      case RTSimGlobals.GT:
	      case RTSimGlobals.GE:
	    	  return checkRegArrayReadAccess(e.getLeft(), regArrays, readAccesses) || 
	    	  checkRegArrayReadAccess(e.getRight(), regArrays, readAccesses); 
	      case RTSimGlobals.PLUS:
	      case RTSimGlobals.MINUS:
	      case RTSimGlobals.AND:
	      case RTSimGlobals.NAND:
	      case RTSimGlobals.OR:
	      case RTSimGlobals.NOR:
	      case RTSimGlobals.XOR: 
	    	  return checkRegArrayReadAccess(e.getLeft(), regArrays, readAccesses) ||
	    	  checkRegArrayReadAccess(e.getRight(), regArrays, readAccesses); 
	  }
	  return error;
	    
  }
  public int checkStatement(ASTStat stat, int ostat_type) {
    int errorCount = 0;
    String errStr;
    int stat_type = stat.getStatementType();
    switch(ostat_type) {
      case RTSimGlobals.OSTAT_TYPE_2EDGE_1 :
        if(stat_type == RTSimGlobals.GOTO) {
          insertError(IUI.get("ERROR_NO_GOTOS_BEFORE_PIPE"),stat.getPositionRange());
          errorCount++;
        }
        break;
      case RTSimGlobals.OSTAT_TYPE_2EDGE_2 :
        if(stat_type != RTSimGlobals.GOTO && stat_type != RTSimGlobals.NOP) {
          insertError(IUI.get("ERROR_ONLY_GOTO_NOP_AFTER_PIPE"),stat.getPositionRange());
          errorCount++;
        }
    }
    switch(stat.getStatementType()) {
      case RTSimGlobals.ASSIGN:
        BitSeqCheckInfo ci = checkBitSequence(stat.getBitSequence());
        errorCount += ci.errorCount;
        errorCount += checkExpression(stat.getExpression(),ci.width);
        stat.setStatementType(RTSimGlobals.ASSIGN);
        stat.setBusOnLeftSide(bitSeqContainsBus(stat.getBitSequence())); // Pruefen, ob Zuweisung an Bus enthalten
        // Zuweisung Bus <- Bus bzw. InBus <- irgendwas vermeiden
        if(stat.getBitSequence().containsBus()) {
          if(stat.getExpression().containsBus()) {
            errorCount++;
            insertError(IUI.get("ERROR_ASSIGN_BUS_TO_BUS"),
			stat.getPositionRange());
          }
          if(stat.getBitSequence().containsInBus()) {
            errorCount++;
            insertError(IUI.get("ERROR_ASSIGN_INBUS"),
			stat.getPositionRange());
          }
        }
        break;
      case RTSimGlobals.READ:
      case RTSimGlobals.WRITE:
        Memory m = getMemory(stat.getMemoryId());
        if(m == null) {
          errorCount++;
          errStr = IUI.get("ERROR_MEMORY_UNDECL_IN_MEMOP");
          errStr = errStr.replaceAll("%%MEMORY",stat.getMemoryId());
          errStr = errStr.replaceAll("%%MEMOP",RTSimGlobals.typeToLiteral(stat.getStatementType()));
          insertError(errStr,stat.getPositionRange());
        }
        else stat.setMemory(m);
        break;
      case RTSimGlobals.GOTO:
        if(!stat.isGotoEnd())
          insertGoto(stat.getLabelId(),stat.getPositionRange(),stat); 
        break;
      case RTSimGlobals.NOP: break;
      default:
        errorCount++;
        insertError(IUI.get("INTERNAL_ERROR4"),stat.getPositionRange());
        break;
    }
    return errorCount;
  }

  public boolean checkBitRange(BitRange br, SimulationObject so, PositionRange pr) {
    String errStr;
    if(so instanceof Register) {
      Register r = (Register) so;
      if(!r.checkBitRange(br)) {
        errStr = IUI.get("ERROR_BITRANGE_EXCEEDS_REGDECL");
        errStr = errStr.replaceAll("%%BITRANGE",br.toString());
        errStr = errStr.replaceAll("%%REGDECL",r.getPrettyDecl());
        insertError(errStr,pr);
        return false;
      }
      else return true;
    }
    else if(so instanceof Bus) {
      Bus b = (Bus) so;
      if(!b.checkBitRange(br)) {
        errStr = IUI.get("ERROR_BITRANGE_EXCEEDS_BUSDECL");
        errStr = errStr.replaceAll("%%BITRANGE",br.toString());
        errStr = errStr.replaceAll("%%BUSDECL",b.getPrettyDecl());
        insertError(errStr,pr);
        return false;
      }
      else return true;
    }
    else if(so instanceof RegisterArray) {
    	RegisterArray ra = (RegisterArray) so;
    	if(!ra.checkBitRange(br)) {
    		errStr = IUI.get("ERROR_BITRANGE_EWCEEDS_REGARRAYDECL");
    		errStr = errStr.replaceAll("%%BITRANGE",br.toString());
    		errStr = errStr.replaceAll("%%REGARRDECL",ra.getPrettyDecl());
    		insertError(errStr,pr);
    		return false;
    	}
    	else return true;
    }
    else {
      insertError(IUI.get("INTERNAL_ERROR5"),pr);
      return false;
    }
  }

  public boolean bitSeqContainsBus(ASTBit_Seq bit_seq) {
    Bus b = getBus(bit_seq.getTargetId());
    if(b != null) return true;
    if(bit_seq.hasNext()) return bitSeqContainsBus(bit_seq.next());
    return false;
  }

  public BitSeqCheckInfo checkBitSequence(ASTBit_Seq bit_seq) {
    String errStr;
    Register r = getRegister(bit_seq.getTargetId());
    Bus b = getBus(bit_seq.getTargetId());
    RegisterArray ra = getRegArray(bit_seq.getTargetId());
    SimulationObject so;
    BitSeqCheckInfo ci;
    int width = 0;
    if(r != null || b != null || ra != null) {
      if(r == null && ra == null) {
        so = b;
        width = b.getWidth();
      }
      else if(b == null && ra == null){
        so = r;
        width = r.getWidth();
      } else {
    	  so = ra;
    	  width = ra.getWidth();
      }
      bit_seq.setRef(so);
      if(!bit_seq.hasNext()) 
        ci = new BitSeqCheckInfo(0,0);
      else 
        ci = checkBitSequence(bit_seq.next());
      if(bit_seq.allBits()) ci.width += width;
      else {
        BitRange br = bit_seq.getBitRange();
        if(checkBitRange(br,so,bit_seq.getPositionRange())) ci.width += br.getWidth();
        else ci.errorCount++; // checkBitRange fuegt Fehlermeldung selber ein
      }
      return ci;
    }
    // sonst: referenzierter Ident nicht gefunden
    errStr = IUI.get("ERROR_REGBUS_UNDECL_IN_BITSEQ");
    errStr = errStr.replaceAll("%%REGBUS",bit_seq.getTargetId());
    insertError(errStr,bit_seq.getPositionRange());
    int errorCount = 1;
    width = 0;
    if(!bit_seq.allBits()) {
      BitRange br = bit_seq.getBitRange();
      width = br.getWidth();
    }
    if(bit_seq.hasNext()) {
      ci = checkBitSequence(bit_seq.next());
      ci.width += width;
      ci.errorCount += errorCount;
      return ci;
    }
    else
      return new BitSeqCheckInfo(errorCount,width);
  }

  public int checkExpression(ASTExpr expr, int width) {
    String errStr;
    expr.setWidth(width);
    int type = expr.getType();
    switch(type) {
      case RTSimGlobals.NUM_CONST:
        return checkNumConst(expr.getNumConst(),width,expr.getPositionRange());
      case RTSimGlobals.BIT_SEQ:
    	//checkRegArrayReadAccess(expr.getBitSeq());
        BitSeqCheckInfo ci = checkBitSequence(expr.getBitSeq());
        if(width != 0 && ci.width>width) {
          errStr = IUI.get("ERROR_BITSEQ_EXCEEDS_WIDTH");
          errStr = errStr.replaceAll("%%HASWIDTH",Integer.toString(ci.width)); 
          errStr = errStr.replaceAll("%%NEEDSWIDTH",Integer.toString(width));
          insertError(errStr,expr.getBitSeq().getPositionRange());
          return ci.errorCount + 1;
        }
        else return ci.errorCount;
      case RTSimGlobals.NOT:
      case RTSimGlobals.SIGN:
        return checkExpression(expr.getOperand(),width); 
      case RTSimGlobals.NE:
      case RTSimGlobals.EQ:
      case RTSimGlobals.LT:
      case RTSimGlobals.LE:
      case RTSimGlobals.GT:
      case RTSimGlobals.GE:
        return checkExpression(expr.getLeft(),0)
             + checkExpression(expr.getRight(),0); 
      case RTSimGlobals.PLUS:
      case RTSimGlobals.MINUS:
      case RTSimGlobals.AND:
      case RTSimGlobals.NAND:
      case RTSimGlobals.OR:
      case RTSimGlobals.NOR:
      case RTSimGlobals.XOR: 
        return checkExpression(expr.getLeft(),width)
             + checkExpression(expr.getRight(),width); 
      default:
        insertError(IUI.get("INTERNAL_ERROR6")+type,expr.getPositionRange());
        return 1;
    }
  } 

  public boolean exprContainsBus(ASTExpr expr) {
    switch(expr.getType()) {
      case RTSimGlobals.NUM_CONST: return false;
      case RTSimGlobals.BIT_SEQ: return bitSeqContainsBus(expr.getBitSeq());
      case RTSimGlobals.NOT:
      case RTSimGlobals.SIGN:
        return exprContainsBus(expr.getOperand());
      default:
        ASTExpr l = expr.getLeft();
        ASTExpr r = expr.getRight();
        boolean lb = false;
        boolean lr = false;
        if(l != null) lb = exprContainsBus(l);
        if(r != null) lr = exprContainsBus(r);
        return lb || lr;
    }
  }

  public int checkNumConst(BitVector bv, int width, PositionRange pr) {
    String errStr;
    int bv_width = bv.getWidth();
    if(width > 0 && bv_width > width) {
      String old = bv.toString();
      bv.makeWidth(width);
      errStr = IUI.get("WARNING_NUMCONST_EXCEEDS_WIDTH");
      errStr = errStr.replaceAll("%%NEEDSWIDTH",Integer.toString(width));
      errStr = errStr.replaceAll("%%OLDVAL",old);
      errStr = errStr.replaceAll("%%NEWVAL",bv.toString());
      insertError(errStr,pr);
      return 0;
    }
    else return 0;
  }

  public boolean hasWarnings() { return warningMessages.size() > 0; }
}

