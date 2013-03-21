package de.uniluebeck.iti.rteasy.kernel;
import java.util.*;
import java.io.*;

public class ExpressionInputBundle {

  private Hashtable regBusTable;

  ExpressionInputBundle() {
    regBusTable = new Hashtable();
  }

  public boolean hasRegArrays(){
	  boolean stat = false;
	  for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
		  if(((RegBus) en.nextElement()) instanceof RegisterArray){
			  stat=true;
		  }
	  }
	  return stat;
  }
  
  public LinkedList<RegBus> getRefReg(){
	  LinkedList<RegBus> ll = new LinkedList<RegBus>();
	  RegBus rb;
	  if(hasRegArrays()){
		  for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
			  RegBus r = (RegBus) en.nextElement();
			  if(r instanceof RegisterArray){
				  RegisterArray a = (RegisterArray) r;
//				  rb = a.getReference();
//				  ll.add(rb);
			  }
		  }
		  return ll;
	  } else {
		  return null;
	  }
  }
  
  public BitRange getArrayWidth(int i){
	  BitRange br = null;
	  int j=0;
	  if(hasRegArrays()){
		  for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
			  RegBus r = (RegBus) en.nextElement();
			  if(r instanceof RegisterArray){
				  if(i==j){
					  RegisterArray a = (RegisterArray) r;
					  br=a.getBitRange();
				  }
				  j+=1;
			  }
		  }
	  }
	  return br;
  }
  
  public void include(RegBusReference rbr) {
    ArrayList refList;
    int i;
    BitRange br;
    if(regBusTable.containsKey(rbr.regBus)) {
      refList = (ArrayList) regBusTable.get(rbr.regBus);
      i = 0;
      br = rbr.bitRange;
      // join and remove all BitRanges from list adjacent to or intersecting
      // with rbr.bitRange
      while(i<refList.size()) {
        if(((BitRange) refList.get(i)).joinPossibleWith(rbr.bitRange)) {
          br = br.joinWith((BitRange) refList.get(i));
          refList.remove(i);
        }
        else i++;
      }
      // now insert new BitRange into list keeping the order
      i = 0;
      while(i<refList.size() && ((BitRange) refList.get(i)).lt(br)) i++;
      refList.add(i,br);
    }
    else {
      refList = new ArrayList();
      refList.add(rbr.bitRange);
      regBusTable.put(rbr.regBus,refList);
    }
  }

  public RegBusReference map(RegBusReference rbr) {
    if(regBusTable.containsKey(rbr.regBus)) {
      ArrayList refList = (ArrayList) regBusTable.get(rbr.regBus);
      // search surrounding or equal BitRange to rbr.bitRange
      for(int i=0;i<refList.size();i++)
        if(((BitRange) refList.get(i)).contains(rbr.bitRange))
          return new RegBusReference(rbr.regBus,(BitRange) refList.get(i));
      // not found
      return null;
    }
    else return null;
  }

  public void emitPortDeclarations(String indent, PrintWriter out) {
    ArrayList refList;
    RegBus rb;
    ListIterator it;
    RegBus ref=null;
    boolean b = false;
    BitRange range=null;
    for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
      rb = (RegBus) en.nextElement();
      refList = (ArrayList) regBusTable.get(rb);
      if(rb instanceof RegisterArray){
    	  b = true;
    	  RegisterArray ra = (RegisterArray) rb;
//    	  ref = ra.getReference();
//    	  out.println(indent+(new RegBusReference(ref,ref.offset,ref.offset+ref.width-1)).toVHDLPortDecl("IN",false,null)+";");
      }
      for(it=refList.listIterator();it.hasNext();){
    	  if(ref!=null){
    	  range=(new RegBusReference(ref,ref.offset,ref.offset+ref.width-1)).bitRange;
    	  }
    	out.println(indent+(new RegBusReference(rb,(BitRange) it.next())).toVHDLPortDecl("IN",b,range)+";");}
    } b=false;
  }
  
  public void emitOutRegArray(String indent, PrintWriter out) {
	  ArrayList refList;
	    RegBus rb;
	    RegBus ref=null;
	    for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
	      rb = (RegBus) en.nextElement();
	      refList = (ArrayList) regBusTable.get(rb);
	      if(rb instanceof RegisterArray){
	    	  RegisterArray ra = (RegisterArray) rb;
//	    	  ref = ra.getReference();
//	    	  out.println(indent+(new RegBusReference(ref,ref.offset,ref.offset+ref.width-1)).toVHDLPortDecl("IN",false,null)+";");
	      }
	    }
  }

  public void emitPortMap(String indent, PrintWriter out) {
    emitPortMap(indent,out,null);
  }

  public void emitPortMap(String indent, PrintWriter out, String port) {
    ArrayList refList;
    RegBus rb;
    ListIterator it;
    for(Enumeration en=regBusTable.keys();en.hasMoreElements();) {
      rb = (RegBus) en.nextElement();
      refList = (ArrayList) regBusTable.get(rb);
      for(it=refList.listIterator();it.hasNext();)
        out.println(indent+(new RegBusReference(rb,(BitRange) it.next())).getVHDLPortMap(port)+",");
    }
  }

}