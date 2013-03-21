package de.uniluebeck.iti.rteasy.frontend;

import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.StatementSequence;

public class ASTSwitch_Case_Stat extends RTSimNode {
	public StatementSequence s = null;
	public boolean hasDefault = false;
	
	public ASTSwitch_Case_Stat(int id) {super(id);}
	
	public ASTBit_Seq getBitSequence() {return (ASTBit_Seq) jjtGetChild(0);}
	public void setDef(boolean b) {hasDefault = b;}
	public String getSwitch() {return getBitSequence().getTargetId();}
	public BitRange getBitRange() {return getBitSequence().getBitRange();}
	public ASTCaseList getCases() {return (ASTCaseList) jjtGetChild(1);}
	public boolean hasDefault() {return hasDefault;}
	public ASTCaseList getDefaultCase() {
		if(hasDefault) return (ASTCaseList) jjtGetChild(2);
		else return null;
	}
}
