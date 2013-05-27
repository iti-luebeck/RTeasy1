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