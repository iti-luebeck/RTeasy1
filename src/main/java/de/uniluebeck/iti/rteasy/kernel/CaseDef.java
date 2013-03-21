package de.uniluebeck.iti.rteasy.kernel;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.frontend.ASTCaseList;

public class CaseDef {
	
	Expression comparator;
	RTSimGlobals type;
	ParallelStatements caseStats;
	boolean defaultFlag;
	PositionRange pr;
	
	/**
	 * Der Konstruktor der Casedefinition. Hier werden alle
	 * ben&ouml;tigten Werte aus der ASTcaseList geholt.
	 * @param pc : ProgramControl der Casedefinition
	 * @param list : ASTCaseList mit allen nötigen Werten
	 */
	CaseDef(ProgramControl pc, ASTCaseList list){
		BitVector comp = list.getComparator();
		pr = list.getPositionRange();
		if(comp==null) comparator = null;
		else comparator = new Expression(comp);
		caseStats = new ParallelStatements(pc, list.getStatSeq());
		defaultFlag = list.getHasDefault();
	}
	
	/**
	 * Hier wird der Komparator zur&uuml;ckgegeben.
	 * @return Komparator
	 */
	public Expression getComparator() {
		return comparator;
	}

	/**
	 * Hier werden die Statements des Case zur&uuml;ckgegeben.
	 * @return Statements
	 */
	public ParallelStatements getStatements() {
		return caseStats;
	}
	
	/**
	 * Hier wird zur&uuml;ckgegeben, ob es sich um einen default
	 * case handelt.
	 * @return true, wenn es ein default case ist, sonst false
	 */
	public boolean getDefaultFlag() {
		return defaultFlag;
	}
	
	/**
	 * Hier wird die Position des Case zur&uuml;ckgegeben
	 * @return PositionRange des Case
	 */
	public PositionRange getPositionRange(){
		return pr;
	}
}
