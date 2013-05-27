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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.frontend.ASTCaseList;
import de.uniluebeck.iti.rteasy.frontend.ASTSwitch_Case_Stat;

public class SwitchStatement {
	Expression switcher;
	BitRange br;
	LinkedList<CaseDef> caselist;
	PositionRange pr;
	ProgramControl pc;
	String lf = "\n";
	String tmpswitch;
	boolean hasDefault = false;
	Hashtable registers;
	Hashtable regarrays;
	Register swreg = null;
	RegisterArray swarray = null;
	int width;
	
	/**
	 * Der Konstruktor des Switch-Case Konstrukts. Hier werden
	 * alle Cases in einer Liste gespeichert.
	 * @param tpc : Der ProgramControl für das Switch-Case.
	 * @param swStat : ASTSwitch_Case_Stat die alle ben&ouml;tigten
	 * Informationen f&uuml; das Switch-Case beinhaltet.
	 */
	SwitchStatement(ProgramControl tpc, ASTSwitch_Case_Stat swStat){
		tmpswitch = swStat.getSwitch();
		br = swStat.getBitRange();
		switcher = new Expression(swStat.getBitSequence());
		switcher.sinkWidth(1);
		pr = swStat.getPositionRange();
		pc = tpc;
		caselist = new LinkedList<CaseDef>();
		ASTCaseList list = swStat.getCases();
		ASTCaseList tmplist = list;
		do {
			list = tmplist;
			CaseDef tmp = new CaseDef(tpc, list);
			caselist.add(tmp);
			if(list.hasNext) tmplist = list.next();
		} while(list.hasNext);
		if(swStat.hasDefault()) {
			hasDefault = true;
			list = swStat.getDefaultCase();
			CaseDef tmp = new CaseDef(tpc, list);
			caselist.add(tmp);
		}
	}
	
	/**
	 * Hier werden für die Simulation alle auszuführenden Statements
	 * geholt. Es wird gepr&uuml;ft, welcher Case mit dem Register/
	 * Registerarray aus der Switchdefinition &uuml;bereinstimmt.
	 * Dieser Case wird zur&uuml;ckgegeben
	 * @param edgeType : der entsprechende EdgeType
	 * @param registers : Die Liste aller Register mit aktuellem Inhalt
	 * @param regarrays : Alle Registerarrays mit aktuellem Inhalt
	 * @return ArrayList mit allen in diesem Zustand 
	 * auszuf&uuml;hrenden Statements.
	 */
	public ArrayList getStatements(int edgeType, Hashtable registers,
			Hashtable regarrays) {
		//updateSwitch(registers, regarrays);
		ArrayList back = new ArrayList();
		boolean switchexpr = false;
		int pos = 0;
		Expression tmp;
		Statement switchSt = null;
		do { 
			tmp = new Expression(RTSimGlobals.EQ,switcher,caselist.get(pos).comparator);
			switchexpr = tmp.eval(edgeType).get(0);
			pos++;
		} while(!switchexpr && caselist.size()>(pos+1));
		//back.add(new Statement(pc, getSwitcherPR(), switchexpr));
		if(switchexpr){
			switchSt = new Statement(pc,getSwitcherPR(),switchexpr,getCasePR(pos-1));
			back.add(switchSt);
			back.addAll(caselist.get(pos-1).caseStats.getStatements(edgeType,0, registers, regarrays));
		} else if(hasDefault){
			switchSt = new Statement(pc,getSwitcherPR(),switchexpr,getCasePR(caselist.size()-1));
			back.add(switchSt);
			back.addAll(caselist.get(caselist.size()-1).caseStats.getStatements(edgeType,0,registers,regarrays));
		}
		return back;
	}
	
	/**
	 * Hier werden alle aktuellen Register(-array)-inhalte auf
	 * das geswitchte Objekt verglichen um das Switchobjekt zu
	 * aktualisieren/initialisieren.
	 * @param registers : Liste der aktuellen Registerinhalte
	 * @param regarrays : Liste der aktuellen Registerarrayinhalte
	 */
	public void updateSwitch(Hashtable registers, Hashtable regarrays){
//		Register tmp = (Register) registers.get(tmpswitch);
//		if (tmp == null){
//			RegisterArray tmpArray = (RegisterArray) regarrays.get(tmpswitch);
//			switcher = new Expression(tmpArray.getContent());
//		} else {
//			switcher = new Expression(tmp.getContent());
//		}
		this.registers = registers;
		this.regarrays = regarrays;
		swreg = (Register) registers.get(tmpswitch);
		if (swreg == null) {
			swarray = (RegisterArray) regarrays.get(tmpswitch);
			width = swarray.getWidth();
		} else {
			width = swreg.getWidth();
		}
	}
	
	public void deNest(Expression cond, List newStats) {
		 if(switcher == null) return;
		    Expression newCond;
		    Expression eTrue = new Expression("1");
		    if(cond.equals(eTrue)) newCond = switcher;
		    else newCond =  new Expression(RTSimGlobals.AND,cond,switcher);
		    //if(thenStats != null) thenStats.deNest(newCond,newStats);
	}
	
	/**
	 * Hier wird die Position des Switch zur&uuml;ckgegeben.
	 * @return PositionRange mit Switchposition
	 */
	public PositionRange getSwitcherPR() {
		int x = tmpswitch.length()+6;
		if(br != null){
			x += br.toString().length();
		}
		PositionRange prr = new PositionRange(pr.beginLine,
				pr.beginColumn, pr.beginLine, pr.beginColumn+x);
		return prr;
	}
	
	/**
	 * Hier wird die Position des gesammten Switch-Case 
	 * zur&uuml;ckgegeben
	 * @return Position des Switch-Case
	 */
	public PositionRange getPositionRange() {
		return pr;
	}
	
	/**
	 * Hier wird die Position der einzelnen Case ermittelt.
	 * @param pos : Information über Caseposition in der Caseliste
	 * @return Position des geforderten Case
	 */
	public PositionRange getCasePR(int pos) {
		PositionRange tmp = caselist.get(pos).getPositionRange();
		int x = 0;
		if (pos == caselist.size()-1){
			x = 7;
		} else {
			int casel;
			if (swreg == null && swarray == null) {
				casel = caselist.get(pos).getComparator().toString().length();
			} else {
				casel = width+1;
			}
			x = 3;
		}
		PositionRange cpr = new PositionRange(tmp.beginLine,
				tmp.beginColumn, tmp.beginLine, tmp.beginColumn+x);
		return cpr;
	}
	
	/**
	 * Hier wird ein Low-Level-Modell des Switch-Case erstellt.
	 * Das bestehende Switch-Case wird dabei in eine if-else
	 * Konstruktion &uuml;berf&uuml;hrt.
	 * @return Die entsprechende if-else Konstruktion
	 */
	public IfStatement transformToIf() {
		IfStatement ifStat;
		Expression exp = new Expression(RTSimGlobals.EQ,
				switcher,caselist.get(0).getComparator());
		if(caselist.size()>1){
			ifStat = new IfStatement(true,pr,pc,caselist.get(0).getStatements(),
					getElse(1),exp,lf);
		} else {
			ifStat = new IfStatement(false, pr, pc,
					caselist.get(0).getStatements(),null,exp,lf);
		}
		return ifStat;
	}
	
	/**
	 * Hier wird f&uuml;r transformToIf() der else-Zweig rekursiv
	 * erzeugt.
	 * @see transformToIf()
	 * @param i : entsprechende Position in der Caselist
	 * @return : das ParallelStatement des else-Zweiges
	 */
	public ParallelStatements getElse(int i) {
		ParallelStatements ps = null;
		if(i<(caselist.size()-1)){
			Expression tmp = new Expression(RTSimGlobals.EQ,switcher,
					caselist.get(i).getComparator());
			IfStatement ifStat = new IfStatement(true,pr,pc,
					caselist.get(i).getStatements(),getElse(i+1),
					tmp,lf);
			ps = new ParallelStatements(ifStat);
		} else if(i==(caselist.size()-1)) {
			if(hasDefault){
				ps = caselist.get(i).getStatements();
			} else {
				Expression tmp = new Expression(RTSimGlobals.EQ,switcher,
						caselist.get(i).getComparator());
				IfStatement ifTmp = new IfStatement(false,pr,pc,
						caselist.get(i).getStatements(),null,
						tmp, lf);
				ps = new ParallelStatements(ifTmp);
			}
		}
		return ps;
	}
	
	/**
	 * Hier wird das Switch-Case über die Funktion "Pretty Print"
	 * in eine &uuml;bersichtliche Form gebracht. Die einzelnen
	 * Komparatoren aus den Cases werden dabei in die L&auml;nge
	 * des geswitchten Register(-array)s umgesetzt.
	 * @param indentStr : Einr&uuml;ckung, falls vorhanden
	 * @param registers : Liste der aktuellen Registerinhalte
	 * @param regarrays : Liste der aktuellen Registerarrayinhalte
	 * @return Eine &uuml;bersichtliche Form des Switch-Case
	 */
	public String toString(String indentStr, Hashtable registers, Hashtable regarrays){
		updateSwitch(registers, regarrays);
		int width = 0;
		if(swreg == null) {
			width = swarray.getWidth();
		} else {
			width = swreg.getWidth();
		}
		String cmp = "";
		cmp += indentStr + "switch ";
		if (switcher == null) cmp += "ERROR";
		else cmp += switcher.toString();
		cmp += " { ";
		if(caselist.isEmpty()) cmp += "ERROR";
		else {
			for (int i=0; i<caselist.size(); i++) {
				cmp += lf +indentStr+ indentStr;
				if(caselist.get(i).getDefaultFlag()){
					cmp += "default ";
				} else {
					cmp += "case " + caselist.get(i).getComparator().toString2(width);
				}
				cmp += " : " + lf;
				String x = indentStr+indentStr+indentStr;
				cmp += caselist.get(i).getStatements().toString(x, registers, regarrays);
			}
		}
		return cmp += lf+indentStr+"}";
	}

}
