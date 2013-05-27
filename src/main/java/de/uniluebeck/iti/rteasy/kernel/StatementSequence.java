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

import java.util.*;
import java.io.PrintWriter;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTStat_Seq;

public class StatementSequence {

  private PositionRange pr;
  private ProgramControl pc;
  private ArrayList sequence;
  private int length;

  public void dumpSimTree(PrintWriter out, String indent) {
    int i, s;
    s = sequence.size();
    for(i=0;i<s;i++) {
      out.println(indent+"["+i+"] : ParallelStatements = ");
      ((ParallelStatements) sequence.get(i)).dumpSimTree(out,indent+"  ");
    }
  }

  StatementSequence(ProgramControl tpc, ASTStat_Seq stat_seq) {
    pc = tpc;
    pr = stat_seq.getPositionRange();
    boolean cont;
    sequence = new ArrayList();
    int statSeqIndex = 0; // Zaehler fuer Labels
    do {
      if(stat_seq.has2Edges())
        sequence.add(new ParallelStatements(pc,stat_seq.getStatements(),stat_seq.getStatements2(),statSeqIndex++));
      else
        sequence.add(new ParallelStatements(pc,stat_seq.getStatements(),statSeqIndex++));
      if(stat_seq.hasNext()) {
        cont = true; 
        stat_seq = stat_seq.next();
      }
      else cont = false;
    } while(cont);
    length = sequence.size();
  }

  public int getEndState() { return sequence.size(); }

  public String getVHDLstateSubtype() {
    return "natural RANGE 0 TO "+sequence.size();
  }

  public void emitStateTransitionProcess(String indent, PrintWriter out,
                                         SignalsData signalsData, int stateWidth) {
    int i;
    int s = sequence.size();
    ParallelStatements ps;
    String defaultGotoState;
    out.println(indent+"statetrans: PROCESS(I,STATE)");
    out.println(indent+"BEGIN");
    out.println(indent+"  CASE STATE IS");
    for(i=0;i<s;i++) {
      out.print(indent+"    WHEN \""+RTSimGlobals.int2bitVectorString(i,stateWidth)
                  + "\" =>");
      ps = (ParallelStatements) sequence.get(i);
      if(i==s-1) defaultGotoState = "endstate";
      else defaultGotoState = "\""+RTSimGlobals.int2bitVectorString(i+1,stateWidth)+"\"";
      ps.emitStateTransitionAssignment(indent+"      ",out,signalsData,
        stateWidth, defaultGotoState);
    }
    out.println(indent+"    WHEN OTHERS =>");
    out.println(indent+"      NEXTSTATE <= endstate;");
    out.println(indent+"  END CASE;");
    out.println(indent+"END PROCESS;");
  }

  public void emitStateOutputProcess(String indent, PrintWriter out,
                                         SignalsData signalsData, int stateWidth) {
    int i;
    int s = sequence.size();
    ParallelStatements ps;
    out.println(indent+"output: PROCESS(I,STATE)");
    out.println(indent+"BEGIN");
    out.println(indent+"  CASE STATE IS");
    for(i=0;i<s;i++) {
      out.print(indent+"    WHEN \""+RTSimGlobals.int2bitVectorString(i,stateWidth)
                  + "\" =>");
      ps = (ParallelStatements) sequence.get(i);
      ps.emitOutputAssignments(indent+"      ",out,signalsData);
    }
    out.println(indent+"    WHEN OTHERS =>");
    out.println(indent+"      C <= (OTHERS => '0');");
    out.println(indent+"  END CASE;");
    out.println(indent+"END PROCESS;");
  }

  public void emitDeltaFunction(String indent, PrintWriter out,
                                SignalsData signalsData) {
    int i;
    int s = sequence.size();
    ParallelStatements ps;
    out.println(indent+"CASE state IS");
    for(i=0;i<s;i++) {
      out.println(indent+"  WHEN "+i+" =>");
      ps = (ParallelStatements) sequence.get(i);
      ps.emitDeltaFunction(indent+"    ",out,signalsData);
    }
    out.println(indent+"  WHEN "+s+" => goto_calc := "+s+";");
    out.println(indent+"END CASE;");
  }

  /**
   * f&uuml;gt einen neuen Eintrag in die Statements-Liste ein
   * @param statSeqIndex Position des neuen Eintrags, alle folgenden
   * Eintr&auml;ge werden nach rechts verschoben
   * @param ps der neue Eintrag
   * @return true, falls Position des Eintrags in Liste oder am Ende
   */
  public boolean insertParallelStatements(int statSeqIndex, ParallelStatements
					  ps) {
    if(statSeqIndex == sequence.size()) {
      // anfuegen
      sequence.add(ps);
      length++;
      return true;
    }
    if(statSeqIndex >= 0 && statSeqIndex < sequence.size()) {
      // einfuegen
      sequence.add(statSeqIndex,ps);
      length++;
      return true;
    }
    // out of range
    else {
      System.err.println("sequence.size() = "+sequence.size()+", Index = "+
			 statSeqIndex);
      return false;
    }
  }

  public void deNest() {
    for(ListIterator it = sequence.listIterator();it.hasNext();) 
      ((ParallelStatements) it.next()).deNest();
  }

  public void eleminateElse() {
    for(ListIterator it = sequence.listIterator();it.hasNext();) 
      ((ParallelStatements) it.next()).eleminateElse();
  }
  
  public void transformSwitch() {
	for(ListIterator it = sequence.listIterator();it.hasNext();) 
	    ((ParallelStatements) it.next()).transformSwitch();
  }

  public void cleanUp() {
    int i;
    int s = sequence.size();
    for(i=0;i<s;i++) ((ParallelStatements) sequence.get(i)).cleanUp();
  }

  public int expandLabelStates(RTProgram rtprog) {
    int i = 0;
    ParallelStatements ps;
    while(i<sequence.size()) {
      ps = (ParallelStatements) sequence.get(i);
      i += ps.expandLabelStates(rtprog,i) + 1;
    }
    length = sequence.size();
    return length;
  }

  /**
   * @return neue L&auml;nge von sequence
   */
  public int expandPipeOps(RTProgram rtprog) {
    int i=0;
    int s;
    ParallelStatements ps;
    HashSet marked = new HashSet();
    Iterator it;
    while(i<sequence.size()) {
      ps = (ParallelStatements) sequence.get(i);
      if(ps.performPipeOpExpansion(i,rtprog)) {
        marked.add(new Integer(i+1));
        i += 2;
      }
      else i++;
    }
    for(it = marked.iterator();it.hasNext();) {
      i = ((Integer) it.next()).intValue();
      ps = (ParallelStatements) sequence.get(i);
      ps.expandGotos(rtprog);
    }
    return sequence.size();
  }

  /**
   * Signale f&uuml;r Kommunikation zwischen Steuerwerk und Operationswerk
   * berechnen und in signalsData ablegen
   */
  public void calculateSignals(SignalsData signalsData){
    for(ListIterator it=sequence.listIterator();it.hasNext();)
      ((ParallelStatements) it.next()).calculateSignals(signalsData);
  }
 
  /*public boolean execAt(int statSeqIndex, int parStatIndex) {
    if(statSeqIndex < 0 || statSeqIndex >= sequence.size()) return false;
    ParallelStatements ps = (ParallelStatements) sequence.get(statSeqIndex);
    return ps.exec(simtypes[statSeqIndex],parStatIndex);
  }*/

  public ParallelStatements getParStatsAt(int statSeqIndex) {
    if(statSeqIndex < 0 || statSeqIndex >= sequence.size()) return null;
    return (ParallelStatements) sequence.get(statSeqIndex);
  }

  /**
   * Gibt das ParallelStatements-Objekt zur&uuml;ck, auf das das Label l
   * verweist. Vorsicht: Das zur&uuml;ckgegebene Objekt kann auch Statements
   * vor dem Label enthalten.
   */
  public ParallelStatements getParStatsAtLabel(Label l) {
    if(l == null) return null;
    else return getParStatsAt(l.getStatSeqEntry());
  }

  public ParallelStatements getParStatsCopyAtLabel(Label l) {
    ParallelStatements ps = getParStatsAtLabel(l);
    if(ps == null) return null;
    else return ps.copyNoLabels(l.getParStatsEntry());
  }

  public boolean has2Edges(int statSeqIndex) {
    if(statSeqIndex < 0 || statSeqIndex >= sequence.size()) return false;
    ParallelStatements ps = (ParallelStatements) sequence.get(statSeqIndex);
    return ps.has2Edges();
  }

  public ArrayList getStatementsOrderedAt(int edgeType, int statSeqIndex, int parStatIndex,
		  Hashtable registers, Hashtable regarrays) {
    if(statSeqIndex < 0 || statSeqIndex >= sequence.size()) return null;
    ParallelStatements ps = (ParallelStatements) sequence.get(statSeqIndex);
    return ps.getStatementsOrdered(edgeType,parStatIndex, registers, regarrays);
  }
 
  public PositionRange getPositionRangeAt(int statSeqIndex, int parStatIndex) {
    if(statSeqIndex < 0 || statSeqIndex >= sequence.size()) return new PositionRange(0,0,0,0);
    ParallelStatements ps = (ParallelStatements) sequence.get(statSeqIndex);
    return ps.getPositionRangeAt(parStatIndex);
  }
 
  public int getLength() { return length; }

  public int getParStatsIndexAtPosition(int line, int column) {
    int startIdx = line - 1;
    int bk;
    if(line > length) startIdx = length - 1;
    ListIterator it = sequence.listIterator(startIdx);
    PositionRange tpr = ((ParallelStatements) it.next()).getPositionRange();
    if(line < tpr.beginLine || line == tpr.beginLine && column < tpr.beginColumn) {
      while(it.hasPrevious()) {
        bk = it.previousIndex();
        tpr = ((ParallelStatements) it.previous()).getPositionRange();  
        if(line > tpr.beginLine || line == tpr.beginLine && column >= tpr.beginColumn) return bk;
      }
      return -1;
    }
    else if(line > tpr.endLine || line == tpr.endLine && column > tpr.endColumn) {
      while(it.hasNext()) {
        bk = it.nextIndex();
        tpr = ((ParallelStatements) it.next()).getPositionRange();
        if(line < tpr.endLine || line == tpr.endLine && column <= tpr.endColumn)
          return bk;
      }
      return -1;
    }
    else return startIdx;
  }
  
  public int expandSwitch(RTProgram rtprog){
	  int length = 0;
	  ParallelStatements ps;
	  for(int i=0; i<sequence.size(); i++){
		  ps = (ParallelStatements) sequence.get(i);
		  length += ps.expandSwitch(i,rtprog);
	  }
	  return length;
  }

  public String toString(Hashtable registers, Hashtable regarrays) {
    String bk = "";
    String s;
    for(ListIterator it=sequence.listIterator();it.hasNext();) {
      bk += ((ParallelStatements) it.next()).toString("  ", registers,regarrays) + ";\n";
    }
    return bk;
  }

}  
