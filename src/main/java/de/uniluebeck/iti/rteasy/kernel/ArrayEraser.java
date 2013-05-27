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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import de.uniluebeck.iti.rteasy.RTSimGlobals;

public class ArrayEraser {

	public int numberOfArrays;
	public int[] currentPointer;
	public String[] pointerName;
	public String[] arrayName;
	public Hashtable registers;
	public Hashtable regArrays;
	public RegisterArray[] ra;
	
	public ArrayEraser(Hashtable reg, Hashtable regArray){
		numberOfArrays=regArray.size();
		registers=reg;
		regArrays=regArray;
		currentPointer = new int[numberOfArrays];
		pointerName = new String[numberOfArrays];
		arrayName = new String[numberOfArrays];
		ra = new RegisterArray[numberOfArrays];
		int i=0;
		for(Enumeration e=regArrays.elements();e.hasMoreElements();){
			//pointerName[i]=((RegisterArray) e.nextElement()).getReference().idStr;
			arrayName[i]=((RegisterArray) e.nextElement()).idStr;
			currentPointer[i]=0;
			i+=1;
		}
	}
	
	public String newSeq(String text){
		String a = "";
		String tmp1 = "";
		StringTokenizer st = new StringTokenizer(text);
		tmp1 = st.nextToken();
		String tmp2 = st.nextToken();
		String tmp3 = st.nextToken();
		while(st.hasMoreTokens()){
			tmp1=tmp2;
			tmp2=tmp3;
			tmp3=st.nextToken();
			for(int i=0;i<numberOfArrays;i++){
				if(tmp1==pointerName[i]){
					if(tmp2==RTSimGlobals.typeToLiteral(RTSimGlobals.ASSIGN)){
						currentPointer[i]=(Integer.valueOf(tmp3).intValue());
					}
				}
				else if(tmp1==arrayName[i] ||
						tmp1==arrayName[i]){
					
				}
			}
		}
		return a;
	}
}
