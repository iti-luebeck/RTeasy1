package de.uniluebeck.iti.rteasy.frontend;

import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.SimulationObject;

public interface IASTBit_Seq {

	public abstract boolean containsBus();

	public abstract boolean containsInBus();

	public abstract void setRef(SimulationObject so);

	public abstract SimulationObject getRef();

	public abstract void setTargetId(String s);

	public abstract void setBitRange(BitRange tbr);

	public abstract String getTargetId();

	public abstract BitRange getBitRange();

	public abstract boolean allBits();
	
	 public boolean hasNext();
	 
	 public ASTBit_Seq next();

}