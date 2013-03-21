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
