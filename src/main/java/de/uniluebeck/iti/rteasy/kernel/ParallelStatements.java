/*
 * Copyright (c) 2003-2013, University of Luebeck, Institute of Computer Engineering
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Luebeck, the Institute of Computer
 *       Engineering nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE UNIVERSITY OF LUEBECK OR THE INSTITUTE OF COMPUTER
 * ENGINEERING BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package de.uniluebeck.iti.rteasy.kernel;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTInner_ParStats;
import de.uniluebeck.iti.rteasy.frontend.ASTOuter_ParStats;

public class ParallelStatements {

  private ArrayList statements = null;
  private ArrayList statements2 = null;
  private PositionRange pr;
  private ProgramControl pc;
  private Hashtable labels;
  private boolean hasLabels = false;
  private boolean has2Edges = false;

  public void dumpSimTree(PrintWriter out, String indent) {
    int i,s;
    Object o;
    out.println(indent+"statements : List of {Statement, IfStatement} = ");
    s = statements.size();
    for(i=0;i<s;i++) {
      if(hasLabels) {
        if(labels.containsKey(new Integer(i))) 
          out.println(indent+"Label "+((Label) labels.get(new Integer(i))).getIdStr() + ":");
      }
      o = statements.get(i);
      if(o instanceof Statement) ((Statement) o).dumpSimTree(out,indent+"  ");
      else if(o instanceof IfStatement) ((IfStatement) o).dumpSimTree(out,indent+"  ");
    }
    if(has2Edges) {
      s = statements2.size();
      for(i=0;i<s;i++) {
        o = statements2.get(i);
        if(o instanceof Statement) ((Statement) o).dumpSimTree(out,indent+"  ");
        else if(o instanceof IfStatement) ((IfStatement) o).dumpSimTree(out,indent+"  ");
      }
    }
  }

  public ParallelStatements copyNoLabelsLeftOnly() {
    ArrayList toStats = null;
    int s;
    Object o;
    if(statements != null) {
      toStats = new ArrayList();
      s = statements.size();
      for(int i=0;i<s;i++) {
        o = statements.get(i);
        if(o instanceof Statement)
          toStats.add(((Statement) o).copy());
        else if(o instanceof IfStatement)
          toStats.add(((IfStatement) o).copy());
      }
    }
    return new ParallelStatements(toStats,null,pr,pc,null,false,false);
  }

  public ParallelStatements copyNoLabels() {
    return copyNoLabels(0);
  }

  public ParallelStatements getRightSide() {
    if(!has2Edges) return null;
    return new ParallelStatements(statements2,null,pr,pc,null,false,false);
  }

  public ParallelStatements copyLeftSide(int parStatIndex) {
    ArrayList toStats = null;
    int s;
    Object o;
    if(statements != null) {
      toStats = new ArrayList();
      s = statements.size();
      for(int i=parStatIndex;i<s;i++) {
        o = statements.get(i);
        if(o instanceof Statement)
          toStats.add(((Statement) o).copy());
        else if(o instanceof IfStatement)
          toStats.add(((IfStatement) o).copy());
      }
    }
    Hashtable newLabels = null;
    if(hasLabels) {
      newLabels = (Hashtable) labels.clone();
      Integer i;
      for(Enumeration e=newLabels.elements();e.hasMoreElements();) {
        i = (Integer) e.nextElement();
        if(i.intValue() < parStatIndex) newLabels.remove(i);
      }
    }
    return new ParallelStatements(toStats,null,pr,pc,newLabels,hasLabels,false);
  }

  public ParallelStatements copyNoLabels(int parStatIndex) {
    ArrayList toStats = null;
    ArrayList toStats2 = null;
    int s;
    Object o;
    if(statements != null) {
      toStats = new ArrayList();
      s = statements.size();
      for(int i=parStatIndex;i<s;i++) {
        o = statements.get(i);
        if(o instanceof Statement)
          toStats.add(((Statement) o).copy());
        else if(o instanceof IfStatement)
          toStats.add(((IfStatement) o).copy());
      }
    }
    if(statements2 != null) {
      toStats2 = new ArrayList();
      s = statements2.size();
      for(int i=0;i<s;i++) {
        o = statements2.get(i);
        if(o instanceof Statement)
          toStats2.add(((Statement) o).copy());
        else if(o instanceof IfStatement)
          toStats2.add(((IfStatement) o).copy());
      }
    }
    return new ParallelStatements(toStats,toStats2,pr,pc,null,false,has2Edges);
  }

  ParallelStatements(ArrayList toStats, ArrayList toStats2, PositionRange tpr,
		     ProgramControl tpc,Hashtable toLabels, boolean hL,
		     boolean h2E) {
    statements = toStats; statements2 = toStats2; pr = tpr; pc = tpc;
    labels = toLabels; hasLabels = hL; has2Edges = h2E;
  }

  ParallelStatements(Statement st) {
    statements = new ArrayList();
    statements.add(st);
    statements2 = null;
    pr = null; pc = null; labels = null; hasLabels = false;
    has2Edges = false;
  }
  
  ParallelStatements(IfStatement st) {
	    statements = new ArrayList();
	    statements.add(st);
	    statements2 = null;
	    pr = st.getPositionRange();
	    pc = null; labels = null; hasLabels = false;
	    has2Edges = false;
	  }

  ParallelStatements(ProgramControl tpc, ASTInner_ParStats parStats) {
    boolean cont;
    pc = tpc;
    pr = parStats.getPositionRange();
    statements = new ArrayList();
    do {
      switch(parStats.getStatNodeType()) {
        case RTSimGlobals.STAT:
          statements.add(new Statement(pc,parStats.getStatement()));
          break;
        case RTSimGlobals.IFSTAT:
          statements.add(new IfStatement(pc,parStats.getIfStatement()));
          break;
       case RTSimGlobals.SWITCH:
    	   statements.add(new SwitchStatement(pc, parStats.getSwitchStatement()));
    	   break;
      }
      if(parStats.hasNext()) {
        cont = true;
        parStats = parStats.next();
      }
      else cont = false;
    } while(cont);
  }

  ParallelStatements(ProgramControl tpc, ASTOuter_ParStats parStats, int statSeqIndex) {
    boolean cont;
    labels = new Hashtable();
    pc = tpc;
    pr = parStats.getPositionRange();
    statements = new ArrayList();
    int parStatsIndex = 0; // Zaehler fuer Labels
    do {
      if(parStats.hasLabel()) {
        parStats.getLabel().setEntry(statSeqIndex,parStatsIndex);
        labels.put(new Integer(parStatsIndex),parStats.getLabel());
      }
      switch(parStats.getStatNodeType()) {
        case RTSimGlobals.STAT:
          statements.add(new Statement(pc,parStats.getStatement()));
          break;
        case RTSimGlobals.IFSTAT:
          statements.add(new IfStatement(pc,parStats.getIfStatement()));
          break;
        case RTSimGlobals.SWITCH:
        	statements.add(new SwitchStatement(pc,parStats.getSwitchStatement()));
        	break;
      }
      if(parStats.hasNext()) {
        cont = true;
        parStats = parStats.next();
      }
      else cont = false;
      parStatsIndex++;
    } while(cont);
    if(labels.isEmpty()) { labels.clear(); labels = null; }
    else hasLabels = true;
  }

  ParallelStatements(ProgramControl tpc, ASTOuter_ParStats parStats, ASTOuter_ParStats parStats2, int statSeqIndex) {
    this(tpc,parStats,statSeqIndex);
    boolean cont;
    PositionRange pr2 = parStats2.getPositionRange();
    pr.endLine = pr2.endLine;
    pr.endColumn = pr2.endColumn;
    has2Edges = true;
    statements2 = new ArrayList();
    do {
      switch(parStats2.getStatNodeType()) {
        case RTSimGlobals.STAT:
          statements2.add(new Statement(pc,parStats2.getStatement()));
          break;
        case RTSimGlobals.IFSTAT:
          statements2.add(new IfStatement(pc,parStats2.getIfStatement()));
          break;
        case RTSimGlobals.SWITCH:
        	statements2.add(new SwitchStatement(pc,parStats2.getSwitchStatement()));
        	break;
      }
      if(parStats2.hasNext()) {
        cont = true;
        parStats2 = parStats2.next();
      }
      else cont = false;
    } while(cont);
  }

  public void putLabel(int parStatsIndex, Label l) {
    hasLabels = true;
    if(labels == null) labels = new Hashtable();
    labels.put(new Integer(parStatsIndex),l);
  }

  /**
   * macht aus jedem nicht am Anfang stehenden Label der ParallelStatements
   * einen eigenen Zustand und f&uuml;gt ihn mit entsprechendem Default-
   * Goto ein.
   * @return Anzahl der neuen Zust&auml;nde
   */
  public int expandLabelStates(RTProgram rtprog, int state) {
    if(!hasLabels) return 0;
    Label l;
    int pe;
    int last = statements.size()-1;
    int nextState = state+1;
    int inserted = 0;
    ParallelStatements newStats;
    for(Enumeration e=labels.elements();e.hasMoreElements();) {
      l = (Label) e.nextElement();
      pe = l.getParStatsEntry();
      if(pe > 0) {
        newStats = copyNoLabels(pe);
        if(pe < last) newStats.insertDefaultGoto(
	  rtprog.getLabelForIndex(nextState));
        newStats.putLabel(0,l);
        rtprog.insertParallelStatements(++state,newStats);
        l.setEntry(state,0);
        labels.remove(new Integer(pe));
        nextState++;
        inserted++;
      }
    }
    if(inserted > 0) insertDefaultGoto(rtprog.getLabelForIndex(nextState));
    return inserted;
  }

  public void eleminateElse() {
    Object o;
    LinkedList newStats = new LinkedList();
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof IfStatement) 
        ((IfStatement) o).eleminateElse(newStats);
      else if(o instanceof Statement)
	newStats.add(o);
    }
    statements.clear();
    statements.addAll(newStats);
  }
  
  public void transformSwitch() {
	  Object o;
	  LinkedList newStats = new LinkedList();
	  LinkedList newStats2=new LinkedList();
	  IfStatement ifStat = null;
	  for(int i=0;i<statements.size();i++){
		  o=statements.get(i);
		  if(o instanceof SwitchStatement){
			  ifStat = ((SwitchStatement) o).transformToIf();
			  newStats.add(ifStat);
		  } else {newStats.add(o);}
	  }
	  if(has2Edges){
		  for(int j=0; j<statements2.size();j++) {
			  o=statements2.get(j);
			  if(o instanceof SwitchStatement){
				  ifStat = ((SwitchStatement) o).transformToIf();
				  newStats2.add(ifStat);
			  } else {newStats2.add(o);}
		  }
		  statements2.clear();
		  statements2.addAll(newStats2);
	  }
	  statements.clear();
	  statements.addAll(newStats);
  }
      
  public void deNest(Expression cond, List newStats) {
    Expression eTrue = new Expression("1");
    Object o;
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof Statement) {
	if(cond.equals(eTrue)) newStats.add(o);
	else newStats.add(new IfStatement(cond,new ParallelStatements(
					       (Statement) o)));  
      }
      else if(o instanceof IfStatement)
	((IfStatement) o).deNest(cond,newStats);
      else if(o instanceof SwitchStatement){
    	  ((SwitchStatement) o).deNest(cond, newStats);
      }
    }
  }

  public void deNest() {
    Expression eTrue = new Expression("1");
    LinkedList newStats = new LinkedList();
    deNest(eTrue,newStats);
    statements.clear();
    statements.addAll(newStats);
  }

  public boolean has2Edges() { return has2Edges; }

  public void emitOutputAssignments(String indent, PrintWriter out,
                                    SignalsData signalsData) {
    Expression conditions[] = new Expression[signalsData.getControlSignalCount()];
    Expression eTrue = new Expression("1");
    Expression eFalse = new Expression("0");
    Object o;
    IfStatement is;
    Statement st;
    int cs;
    if(hasLabels) 
      out.println(indent + "-- "+((Label) labels.elements().nextElement()).getIdStr()+":");
    else out.println();
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof IfStatement) {
        is = (IfStatement) o;
        if(is.isConditionalTransferOperation()) {
          st = is.getConditionalTransferOperation();
          cs = st.getControlSignal();
          if(conditions[cs] == null)
            conditions[cs] = is.getExpression();
          else if(! conditions[cs].equals(eTrue))
            conditions[cs] = new Expression(RTSimGlobals.OR,is.getExpression(),conditions[cs]);
        }
      }
      else if(o instanceof Statement) {
        st = (Statement) o;
        if(st.isTransferOperation())
          conditions[st.getControlSignal()] = eTrue;
      }
    }
    for(cs=0;cs<conditions.length;cs++) {
      if(conditions[cs] == null) out.println(indent+"C("+cs+") <= '0';");
      else if(conditions[cs].equals(eFalse)) out.println(indent+"C("+cs+") <= '0';");
      else if(conditions[cs].equals(eTrue)) out.println(indent+"C("+cs+") <= '1';");
      else {
        out.println(indent+"-- if "+conditions[cs].toString() + " then "
          +signalsData.getStatementByControlSignal(cs).toString() + " fi");
        out.println(indent+"C("+cs+") <= "+conditions[cs].getVHDLcondition(signalsData)+";");
      }
    }
  }

    /**
     * emits VHDL state transition function for state represented by this
     * object
     */
  public void emitStateTransitionAssignment(String indent, PrintWriter out,
    SignalsData signalsData, int stateWidth, String defaultGotoState) {
    Hashtable possibleGotos = new Hashtable();
    Object o;
    IfStatement is;
    Statement st;
    Expression e;
    Label l;
    if(hasLabels) 
      out.println(indent + "-- "+((Label) labels.elements().nextElement()).getIdStr()+":");
    else out.println();
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof IfStatement) {
        is = (IfStatement) o;
        if(is.isConditionalGoto()) {
          l = is.getGotoLabel();
          if(possibleGotos.containsKey(l)) {
            e = (Expression) possibleGotos.get(l);
            possibleGotos.put(l,
              new Expression(RTSimGlobals.OR,is.getExpression(),e));
          }
          else possibleGotos.put(l,is.getExpression());
        }
      }
      else if(o instanceof Statement) {
        st = (Statement) o;
        if(st.getStatementType() == RTSimGlobals.GOTO) {
          // ATTENTION: this code assumes that there is only one GOTO in this state
          out.println(indent+"NEXTSTATE <= \""+RTSimGlobals.int2bitVectorString(
            st.getLabel().getStatSeqEntry(),stateWidth)+"\"; -- goto "+st.getLabel().getIdStr());
          return;
        }
      }
    }
    if(possibleGotos.isEmpty()) {
      out.println(indent+"NEXTSTATE <= "+defaultGotoState+";");
    }
    else {
      out.print(indent);
      for(Enumeration en=possibleGotos.keys();en.hasMoreElements();) {
        l = (Label) en.nextElement();
        e = ((Expression) possibleGotos.get(l)).getMinimizedExpression();
        out.println("IF "+e.getVHDLcondition(signalsData)+"='1' THEN"
          + " -- if "+e.toString()+" then goto "+l.getIdStr()+" fi");
        out.println(indent+"  NEXTSTATE <= \""
          +RTSimGlobals.int2bitVectorString(l.getStatSeqEntry(),stateWidth)
          +"\";");
        out.print(indent+"ELS");
      }
      out.println("E");
      out.println(indent+"  NEXTSTATE <= "+defaultGotoState+";");
      out.println(indent+"END IF;");
    }
  }
        
  public void emitDeltaFunction(String indent, PrintWriter out,
                                SignalsData signalsData) {
    Object o;
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof IfStatement)
	((IfStatement) o).emitDeltaFunction(indent,out,signalsData);
      else if(o instanceof Statement)
        ((Statement) o).emitDeltaOperation(indent+"  ",out,signalsData);
    }
  }
         
  public void calculateSignals(SignalsData signalsData){
    ListIterator it;
    Object o;
    for(it=statements.listIterator();it.hasNext();) {
      o=it.next();
      if(o instanceof IfStatement) 
        ((IfStatement) o).calculateSignals(signalsData);
      else if(o instanceof Statement) 
        ((Statement) o).calculateSignals(signalsData);
    }
    if(has2Edges) for(it=statements2.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof IfStatement)
        ((IfStatement) o).calculateSignals(signalsData);
      else if(o instanceof Statement) 
        ((Statement) o).calculateSignals(signalsData);
    }
  }
         
  public ArrayList getStatements(int edgeType, int index,
		  Hashtable registers, Hashtable regarrays) {
    ArrayList back = new ArrayList();
    ArrayList sl = statements;
    if(has2Edges && edgeType == RTSimGlobals.OSTAT_TYPE_2EDGE_2) 
      sl = statements2;
    Object o;
    int noElems = sl.size();
    for(int i=index;i<noElems;i++) {
      o = sl.get(i);
      if(o instanceof Statement) back.add(o);
      else if(o instanceof IfStatement)
        //back.addAll(((IfStatement) o).getStatements(edgeType, registers, regarrays));
          back.add(o);
      else if(o instanceof SwitchStatement){
    	  back.addAll(((SwitchStatement) o).getStatements(edgeType, registers, regarrays));
      }
    }
    return back;
  }

  public ArrayList getStatementsOrdered(int edgeType, int index,
		  Hashtable registers, Hashtable regarrays) {
    ArrayList temp = getStatements(edgeType,index, registers, regarrays);
    ArrayList reg2bus = new ArrayList();
    ArrayList others = new ArrayList();
    ArrayList ifs = new ArrayList();
    Statement st;
    for(ListIterator statIt = temp.listIterator();statIt.hasNext();) {
      Object o = statIt.next();
      if(o instanceof Statement) {
          st = (Statement) o;
        if(st.hasBusOnLeftSide())
          reg2bus.add(st);
        // dieser Zweig ist jetzt eventuell überflüssig
        else if (st.getStatementType() == RTSimGlobals.IFBAILOUT)
          ifs.add(st);
        else
          others.add(st);
      } else if(o instanceof IfStatement) {
          ifs.add(o);
      }
    }
    reg2bus.addAll(others);
    reg2bus.addAll(ifs);
    return reg2bus;
  }

  /*
  public boolean exec(int index) {
    ArrayList cycleStats = getStatementsOrdered(index); 
    Statement st;
    for(ListIterator statIt = cycleStats.listIterator();statIt.hasNext();) {
      st = (Statement) statIt.next();
      if(!st.exec()) return false;
    }
    return true;
  }
  */

  public PositionRange getPositionRange() { return pr; }

  public PositionRange getPositionRangeAt(int index) {
    PositionRange begR;
    if(index<0 || index > statements.size()) return new PositionRange(0,0,0,0);
    Object o = statements.get(index);
    if(o instanceof Statement)  
      begR = ((Statement) o).getPositionRange();
    else if(o instanceof IfStatement)
      begR = ((IfStatement) o).getPositionRange();
    else if(o instanceof SwitchStatement){
    	begR = ((SwitchStatement) o).getPositionRange();
    }
    else return new PositionRange(0,0,0,0);
    return new PositionRange(begR.beginLine,begR.beginColumn,pr.endLine,pr.endColumn);
  }
  
  public ArrayList getParStatsList() { return statements; }
  
  public ArrayList getParStatsList2() { return statements2; }

  /**
   * gibt einen Ausdruck E zur&uuml;ck, f&uuml;r den gilt:
   * E <=> es findet eine goto-Operation statt
   */
  public Expression getGotoExpression() {
    return getGotoExpressionStats(statements);
  }

  /**
   * genauso wie getGotoExpression(), nur f&uuml;r eventuelle 
   * Statements nach dem |-Operator
   */
  public Expression getGotoExpression2() {
    return getGotoExpressionStats(statements2);
  }

  private Expression getGotoExpressionStats(ArrayList stats) {
    int i;
    Expression e;
    Expression eFalse = new Expression("0");
    Expression eTrue  = new Expression("1");
    if(stats == null) return eFalse;
    int s = stats.size();
    LinkedList el = new LinkedList();
    Object o;
    for(i=0;i<s;i++) {
      o = stats.get(i);
      if(o instanceof Statement) {
        if(((Statement) o).getStatementType() == RTSimGlobals.GOTO)
          return eTrue;
      }
      else if(o instanceof IfStatement) {
        e = ((IfStatement) o).getGotoExpression();
        if(e.equals(eTrue)) return eTrue;
        else if(! e.equals(eFalse)) el.add(e);
      }
    }
    if(el.isEmpty()) return eFalse;
    else {
      e = (Expression) el.get(0);
      for(ListIterator li=el.listIterator(1);li.hasNext();)
        e = new Expression(RTSimGlobals.OR,
		       (Expression) li.next(), e);
      el.clear();  // aufraeumen
      return e;
    }
  }
      
  private void insertDefaultGotoIntoStats(ArrayList sl, Label l) {
    if(sl == null || l == null) return;
    Expression eTrue = new Expression("1");
    Expression eFalse = new Expression("0");
    Expression e = getGotoExpressionStats(sl);
    if(e.equals(eTrue)) return;
    Statement gs = new Statement(l);
    if(e.equals(eFalse)) sl.add(gs);
    else sl.add(new IfStatement(new Expression(RTSimGlobals.NOT,e),
                                new ParallelStatements(gs)));
  }

  public void insertDefaultGoto(Label l) {
    insertDefaultGotoIntoStats(statements,l);
  }

  public void insertDefaultGoto2(Label l) {
    if(has2Edges) insertDefaultGotoIntoStats(statements2,l);
  }

  private boolean edgeUseless(ArrayList stats) {
    if(stats == null) return true;
    int s = stats.size();
    Object o;
    for(int i=0;i<s;i++) {
      o = stats.get(i);
      if(o instanceof Statement) {
        if(((Statement) o).getStatementType() != RTSimGlobals.NOP)
	  return false;
      }
      else if(o instanceof IfStatement) {
        if(! ((IfStatement) o).useless()) return false;
      }
    }
    return true;
  }

  /**
   * @return true, falls die erste Flanke nur aus nops besteht
   */
  public boolean edge1useless() { return edgeUseless(statements); }

  /**
   * @return true, falls die zweite "Flanke" nur aus nops besteht
   */
  public boolean edge2useless() { return edgeUseless(statements2); }

  public void deleteEdge2() {
    has2Edges = false;
    statements2 = null;
  }

  public void cleanUp() {
    if(statements == null) return;
    int i;
    int s = statements.size();
    Object o;
    IfStatement is;
    LinkedList stats = new LinkedList();
    for(i=0;i<s;i++) {
      o = statements.get(i);
      if(o instanceof Statement) {
        if(((Statement) o).usefull()) stats.add(o);
      }
      else if(o instanceof IfStatement) {
        is = ((IfStatement) o).cleanUp();
        if(is != null) stats.add(is);
      }
    }
    statements.clear();
    if(stats.isEmpty()) {
      statements.add(new Statement());
    }
    else {
      statements.addAll(stats);
    }
  }
 
  public void expandGotos(RTProgram rtprog) {
    if(statements == null) return;
    Label l, defaultJump;
    int i;
    int s = statements.size();
    Object o;
    Statement st;
    IfStatement is;
    ParallelStatements ps;
    LinkedList stats = new LinkedList();
    for(i=0;i<s;i++) {
      o = statements.get(i);
      if(o instanceof Statement) {
        st = (Statement) o;
        if(st.getStatementType() == RTSimGlobals.GOTO) {
          l = st.getLabel();
          if(l.equals(rtprog.getEndLabel())) {
            stats.add(st);
          }
	  else {
            ps = rtprog.getStatementSequence().getParStatsCopyAtLabel(l);
            l = rtprog.getLabelForIndex(l.getStatSeqEntry()+1);
            ps.insertDefaultGoto(l);
            stats.addAll(ps.getParStatsList());
          }
        }
        else stats.add(st);
      }
      else if(o instanceof IfStatement) {
        ((IfStatement) o).expandGotos(rtprog);
        stats.add(o);
      }
    }
    statements.clear();
    statements.addAll(stats);
  }

  /**
   * teilt jeden Zustand mit |-Operator in zwei folgende auf und
   * f&uuml;gt dem zweiten ein Default-Goto ein.
   * @return true, falls an einem |-Operator aufgeteilt wurde
   */
  public boolean performPipeOpExpansion
    (int statSeqIndex, RTProgram rtprog) {
    if(!has2Edges) return false;
    if(edge2useless()) {
      deleteEdge2();
      return false;
    }
    Label nextLabel = rtprog.getLabelForIndex(statSeqIndex+1);
    ParallelStatements newStats = getRightSide();
    newStats.insertDefaultGoto(nextLabel);
    rtprog.insertParallelStatements(statSeqIndex+1,newStats);
    has2Edges = false;
    statements2 = null;
    return true;
  }
  
  public int expandSwitch(int statSeqIndex, RTProgram rtprog){
	  return 0;
  }

  public String toString(String indentStr, Hashtable registers, Hashtable regarrays) {
    String bk = "";   
    ListIterator it;
    Object o;
    int i = 0;
    for(it = statements.listIterator();it.hasNext();) {
      o = it.next();
      if(hasLabels) {
        if(labels.containsKey(new Integer(i))) 
          bk += ((Label) labels.get(new Integer(i))).getIdStr() + ":\n";
      }
      if(o instanceof Statement) bk += indentStr+((Statement) o).toString();
      else if(o instanceof IfStatement) bk += ((IfStatement) o).toString(indentStr, registers, regarrays);
      else if(o instanceof SwitchStatement) bk += ((SwitchStatement) o).toString(indentStr,registers, regarrays);
      if(it.hasNext()) bk += ",\n";
      i++; 
    }
    if(has2Edges) {
      bk += "\n" + indentStr + "|\n";
      for(it = statements2.listIterator();it.hasNext();) {
        o = it.next();
        if(o instanceof Statement) bk += indentStr + ((Statement) o).toString(); 
        else if(o instanceof IfStatement) bk += ((IfStatement) o).toString(indentStr, registers, regarrays);
        else if(o instanceof SwitchStatement) bk += ((SwitchStatement) o).toString(indentStr, registers, regarrays);
        if(it.hasNext()) bk += ",\n";
      }
    }
    return bk;
  }

  public boolean isUnconditionalTransferOperation() {
    if(statements.size() != 1) return false;
    else return ((Statement) statements.get(0)).isTransferOperation();
  }

  public Statement getUnconditionalTransferOperation() {
    Object o = statements.get(0);
    if(o instanceof Statement)
      return (Statement) o;
    else return null;
  }

  public boolean containsUnconditionalGoto() {
    Object o;
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof Statement) {
        if(((Statement) o).getStatementType() == RTSimGlobals.GOTO)
          return true;
      }
    }
    return false;
  }

  public Label getUnconditionalGoto() {
    Object o;
    Statement st;
    for(ListIterator it=statements.listIterator();it.hasNext();) {
      o = it.next();
      if(o instanceof Statement) {
        st = (Statement) o;
        if(st.getStatementType() == RTSimGlobals.GOTO)
          return st.getLabel();
      }
    }
    return null;
  }
 
  public String toString() { return toString("", null, null); }
}
