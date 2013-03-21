package de.uniluebeck.iti.rteasy.frontend;

import de.uniluebeck.iti.rteasy.kernel.BitVector;

public class ASTCaseList extends RTSimNode {
	public boolean hasDefault = false;
	public boolean hasNext = false;
	public String comp;
	public int type;
	public ASTStat_Seq s;
	
	public ASTCaseList(int id) {super(id);}
	public void hasDefault(boolean b) {hasDefault = b;}
	public void setComparator(String s) {comp = s;}
	public void setType(int i) {type = i; }
	public void setStatementSequence(ASTStat_Seq ss) {s = ss;}
	public void setHasNext(boolean b) {hasNext = b;}
	public ASTCaseList next() {
		if (hasNext) {return (ASTCaseList) jjtGetChild(2);}
		else return null;
	}
	public boolean getHasNext() {return hasNext;}
	public ASTInner_ParStats getStatSeq() {
		if(hasDefault){
			return (ASTInner_ParStats) jjtGetChild(0);
		} else {
			return (ASTInner_ParStats) jjtGetChild(1);
			}
	}
	public BitVector getComparator() {
		if(hasDefault){return null;}
		else {
			return ((ASTNum_Const)jjtGetChild(0)).getBitVector();
		}
	}
	public boolean getHasDefault() {return hasDefault;}
	public int getType() { return type; }
}