package de.uniluebeck.iti.rteasy.kernel;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTBit_Seq;

public class BitSequence {
  
  public ArrayList bitReferences, bitNumbers;
  // in der Liste stehen die hoeherwertigen Bits zuerst
  private PositionRange pr;
  private ProgramControl pc;
  private int width;
  private boolean containsBus = false;
  private boolean containsRegArray = false;
  private SimulationObject ref = null; 
  private int registerNumber;
  private ASTBit_Seq bit_seq;

  BitSequence(ProgramControl tpc, ASTBit_Seq bit_seq) {
    getFromAST(tpc,bit_seq);
  }

  public int getWidth() { return width; }

  public void getFromAST(ProgramControl tpc, ASTBit_Seq bit_seq) {
    pc = tpc;
    this.bit_seq = bit_seq;
    pr = bit_seq.getPositionRange();
    bitReferences = new ArrayList();
    bitNumbers = new ArrayList();
    boolean cont;

    do { 
      SimulationObject ref = bit_seq.getRef();
      boolean direction;
      int begin, end;
      int twidth, offset;
      if(ref instanceof Bus) {
        containsBus = true;
        twidth = ((Bus) ref).getWidth();
        offset = ((Bus) ref).getOffset();
        direction = ((Bus) ref).getDirection();
      }
      else if(ref instanceof Register){
        twidth = ((Register) ref).getWidth();
        offset = ((Register) ref).getOffset();
        direction = ((Register) ref).getDirection();
      }
     else {
    	  containsRegArray=true;
    	  twidth = ((RegisterArray) ref).getWidth();
    	  offset = ((RegisterArray) ref).getOffset();
    	  direction = ((RegisterArray) ref).getDirection();  
      }
      if(bit_seq.allBits()) {
        if(direction) {
          begin = offset + twidth - 1;
          end = offset;
        }
        else {
          begin = offset;
          end = offset + twidth - 1;
        }
      }
      else {
        BitRange br = bit_seq.getBitRange();
        begin = br.begin;
        end = br.end;
      }
      if(direction) {
        for(int i=begin;i>=end;i--) {
          bitReferences.add(ref);
          bitNumbers.add(new Integer(i));
        }
      }
      else {
        for(int i=begin;i<=end;i++) {
          bitReferences.add(ref);
          bitNumbers.add(new Integer(i));
        }
      }
      if(bit_seq.hasNext()) {
        cont = true;
        bit_seq = bit_seq.next();
      }
      else cont = false;
    } while(cont);
    width = bitReferences.size();
  }     

  public boolean containsBus() { return containsBus; }
  
  public SimulationObject getRef() { return ref;}
  
  public boolean containsRegArray() {return containsRegArray;}

  public BitVector eval() { return eval(RTSimGlobals.OSTAT_TYPE_MEALY);}

  public BitVector eval(int edgeType) {
    String nval = "";
    ListIterator refIt = bitReferences.listIterator(0);
    ListIterator numIt = bitNumbers.listIterator(0);
    while(refIt.hasNext() && numIt.hasNext()) {
      int idx = ((Integer) numIt.next()).intValue();
      Object ref = refIt.next();
      if(ref instanceof Register) {
        Register r = (Register) ref;
        nval = nval + (r.get(edgeType,idx)?"1":"0");
      }
      else if(ref instanceof Bus) {
        Bus b = (Bus) ref;
        nval = nval + (b.get(idx)?"1":"0");
      }
      else if(ref instanceof RegisterArray) {
    	  RegisterArray ra = (RegisterArray) ref;
    	  registerNumber = getRegisterNumber();
    	  ra.setRegisterNumber(registerNumber);
    	  nval = nval + (ra.get(edgeType, idx)?"1":"0");
      }
      else nval = nval + "X";
    }
    return new BitVector(nval);
  } 

  public boolean assign(BitVector bv) {
    ListIterator refIt = bitReferences.listIterator(0);
    ListIterator numIt = bitNumbers.listIterator(0);
    int idx;
    int i = width;
    while(refIt.hasNext() && numIt.hasNext()) {
      i--;
      idx = ((Integer) numIt.next()).intValue();
      Object ref = refIt.next();
      if(ref instanceof Register) {
        Register r = (Register) ref;
        if(!r.set(idx,bv.get(i))) {
          pc.raiseRuntimeError("Bit Nr. "+idx+" von Register "+r.getIdStr()+" wurde mehr als einmal im gleichen Takt beschrieben!",pr);
          return false;
        }
      }
      else if(ref instanceof Bus) {
        Bus b = (Bus) ref;
        if(!b.set(idx,bv.get(i))) {
          pc.raiseRuntimeError("Bit Nr. "+idx+" von Bus "+b.getIdStr()+" wurde mehr als einmal im gleichen Takt beschrieben!",pr);
          return false;
        }
      }
      else if(ref instanceof RegisterArray) {
    	  RegisterArray ra = (RegisterArray) ref;
    	  registerNumber = getRegisterNumber();
    	  ra.setRegisterNumber(registerNumber);
    	  if (!ra.set(idx, bv.get(i))) {
    		  pc.raiseRuntimeError("Bit Nr. " + idx + " von Registerarray " + ra.getIdStr() + " wurde mehr als einmal im gleichen Takt beschrieben!", pr);
    		  return false;
    	  }
      }
    }
    return true;
  }
  /**
   * Gibt die Position des Zeigers im Register-Array zur�ck
   * @return Integer mit Position des Zeigers
   */
  public int getRegisterNumber()
  {
	  if(bit_seq.hasReferenceRegister())
	    {
		  	ASTBit_Seq bit_seqReferenceRegister = bit_seq.getReferenceRegister();
	    	Register referenceRegister = (Register) pc.getRTProgram().getRegisters().
	    			get(bit_seqReferenceRegister.getTargetId()); 
	    	
	    	
	    	int twidth = referenceRegister.getWidth();
	    	int offset = referenceRegister.getOffset();
	    	boolean direction = referenceRegister.getDirection(); 
	    	int begin,end;
	    	if(bit_seqReferenceRegister.allBits())
	    	{
	    		if(direction) {
	    			begin = offset + twidth - 1;
	    	        end = offset;
	    		}
	    		else {
	    			begin = offset;
	    			end = offset + twidth - 1;
	    		}
	    	}
	    	else
	    	{
	    		BitRange br = bit_seqReferenceRegister.getBitRange();
	    		begin = br.begin;
	    		end = br.end;
	    	}
	    	boolean[] content = new boolean[begin-end+1];
	    	int k = 0;
	    	for(int j = end; j <= begin; j++)
	    	{
	    		content[k] = referenceRegister.get(j);
	    		k++;
	    	}
	    	registerNumber = Integer.parseInt(RTSimGlobals.boolArray2String(content,
	    			RTSimGlobals.BASE_DEC));
	    	//Ermittle Zeigerposition des Registerarrays aus dem Wert des Referenzregisters 
	    }
	    else
	    {
	    	 registerNumber = bit_seq.getRegisterNumber();
	    	 //Zeigerposition entspricht dem Wert in den eckigen Klammern
	    }
	  return registerNumber;
  }

  public void insertDrivers(SignalsData signalsData, int controlSignal) {
    ListIterator refIt = bitReferences.listIterator(bitReferences.size()),
                 numIt = bitNumbers.listIterator(bitNumbers.size());
    int i = 0;
    while(refIt.hasPrevious()) 
      signalsData.insertDriver((RegBus) refIt.previous(),
			       ((Integer) numIt.previous()).intValue(),
			       controlSignal,i++);
  }

  public PositionRange getPositionRange() { return pr; }

  public LinkedList clusterBits() {
    ListIterator refIt = bitReferences.listIterator(0);
    ListIterator numIt = bitNumbers.listIterator(0);
    int num, old_num = 0, left = 0;
    LinkedList l = new LinkedList();
    int state = 0;
    Object entry[];
    RegBus rb = null;
    RegBus rb_match = null;

    for(;refIt.hasNext() && numIt.hasNext();) {
      Object o = refIt.next();
      rb = (RegBus) o;
      num = ((Integer) numIt.next()).intValue();
      switch(state) {
      case 1: // match
        if(rb == rb_match && num == rb.nextRightIndex(old_num)) {
	  old_num = num;
	  break;
	}
	// no match, new entry
	entry = new Object[3];
	entry[0] = rb_match;
	entry[1] = new Integer(left);
	entry[2] = new Integer(old_num);
	l.add(entry);
      case 0: // init
        rb_match = rb;
        old_num = num;
        left = num;
        state = 1;
      }
    }
    entry = new Object[3];
    entry[0] = rb;
    entry[1] = new Integer(left);
    entry[2] = new Integer(old_num);
    l.add(entry);

    return l;
  }

  public void bundleInputs(ExpressionInputBundle bundle) {
    List l = clusterBits();
    for(ListIterator it=l.listIterator();it.hasNext();)
      bundle.include(new RegBusReference((Object []) it.next()));
  }

  public String toString() {
    List l = clusterBits();
    ListIterator it = l.listIterator();
    Object entry[];
    RegBus rb;
    RegisterArray ra;
    int left, right;
    String bk = "";
    while(it.hasNext()) {
      entry = (Object[]) it.next();
      if (entry[0] instanceof RegisterArray){
    	  ra = (RegisterArray) entry[0];
    	  left = ((Integer) entry[1]).intValue();
    	  right = ((Integer) entry[2]).intValue();
    	  bk += ra.getIdStr();
    	  if(!((left == ra.getBitRange().begin) && (right==ra.getBitRange().end))) {
    	  if (left!=right){
    		  bk += "("+left+":"+right+")";
    	  } else {
    		  bk += "("+left+")";
    	  }
    	  }
    	  if (it.hasNext()) bk += ".";
      } else {
      rb = (RegBus) entry[0];
      left = ((Integer) entry[1]).intValue();
      right = ((Integer) entry[2]).intValue();
      bk += rb.getIdStr();
      if(!(left == rb.leftBound() && right == rb.rightBound())) {
        if(left != right) bk += "("+left+":"+right+")";
	else bk += "("+left+")";
      }
      if(it.hasNext()) bk += ".";
      }
    }
    return bk;
  }

  public void emitTristateDrivers(String indent, PrintWriter out, int cs) {
    List l = clusterBits();
    RegBusReference rbref;
    int w;
    int left = width-1;
    int new_left;
    int right;
    for(ListIterator it=l.listIterator();it.hasNext();) {
      rbref = new RegBusReference((Object[]) it.next());
      w = rbref.bitRange.getWidth();
      new_left = left-w;
      if(rbref.regBus instanceof Bus) {
        right = new_left+1;
        out.println(indent+"tristate_"+rbref.regBus.getIdStr()+
          "_"+rbref.bitRange.begin+"_"+rbref.bitRange.end+"_C"+
		    cs+": tristate");
        out.println(indent+"  GENERIC MAP(width => "+w+")");
        out.println(indent+"  PORT MAP(");
        out.println(indent+"    ENABLE => C("+cs+"),");
        out.println(indent+"    INPUT => rtop_C"+cs+"_out("+left+" DOWNTO "
                    +right+"),");
        out.println(indent+"    OUTPUT => "+rbref.toVHDL()+");");
      }
      left = new_left;
    }
  }

  public String toVHDLrval(int toWidth) {
    List l = clusterBits();
    ListIterator it = l.listIterator();
    Object entry[];
    RegBus rb;
    int left, right;
    String bk = "";
    if(toWidth > width) {
      bk += "\"";
      for(int i=width;i<toWidth;i++) bk += "0";
      bk += "\" & ";
    }
    String rangeStr;
    while(it.hasNext()) {
      entry = (Object[]) it.next();
      rb = (RegBus) entry[0];
      rangeStr = (rb.getDirection())?" DOWNTO ":" TO ";
      left = ((Integer) entry[1]).intValue();
      right = ((Integer) entry[2]).intValue();
      bk += rb.getVHDLName();
      if(rb instanceof Register) bk += "_out";
      if(rb instanceof RegisterArray) {
    	  bk+="";
      } else if(!(left == rb.leftBound() && right == rb.rightBound())) {
	bk += "("+left+rangeStr+right+")";}
      if(it.hasNext()) bk += " & ";
    }
    return bk;
  }
  
  public String toVHDLrval(ExpressionInputBundle bundle, int toWidth) {
    List l = clusterBits();
    ListIterator it = l.listIterator();
    Object entry[];
    RegBus rb;
    int left, right;
    String bk = "";
    if(toWidth > width) {
      bk += "\"";
      for(int i=width;i<toWidth;i++) bk += "0";
      bk += "\" & ";
    }
    String rangeStr;
    RegBusReference mapped_port, entry_port;
    while(it.hasNext()) {
      entry = (Object[]) it.next();
      entry_port = new RegBusReference(entry);
      mapped_port = bundle.map(entry_port);
      if(mapped_port == null) bk += "<null for "+entry_port.toVHDLPortRval()+">";
      else if(entry_port.regBus instanceof RegisterArray){
    	  RegisterArray ra = (RegisterArray) mapped_port.regBus;
    	  //RegBusReference f = new RegBusReference(ra.getReference(),ra.getReference().offset,ra.getReference().offset+ra.getReference().width-1);
    	  //bk += mapped_port.toVHDLPortName()+"("+f.toVHDLPortName()+")"; 
      }
      else bk += mapped_port.toVHDLPortName()+entry_port.bitRange.toVHDL();
      if(it.hasNext()) bk += " & ";
    }
    return bk;
  }
  
  public String toVHDLlval(){
	  String bk="";
	  RegisterArray ra = (RegisterArray) bitReferences.get(1);
	  //RegBusReference f = new RegBusReference(ra.getReference(),ra.getReference().offset,ra.getReference().offset+ra.getReference().width-1);
	  //bk += "("+f.toVHDLPortName()+")"; 
	  return bk;
  }

  public void emitVHDLassign(String indent, PrintWriter out) {
    int i = bitReferences.size() - 1;
    int s = i;
    int j;
    SimulationObject so;
    for(j=0;j<=s;j++) {
      so = (SimulationObject) bitReferences.get(j);
      out.print(indent);
      if(so instanceof Register) out.print("reg_"+so.getIdStr()+"_new");
      else if(so instanceof Bus) out.print("bus_"+so.getIdStr());
      else out.print ("ERROR_"+so.getIdStr());
      out.println("("+bitNumbers.get(j)+") := temp("+i+");");
      i--;
    }
  }

  public void emitVHDLSignalAssignments(int c,
                                        String indent, PrintWriter out) {
    int i = bitReferences.size() - 1;
    int s = i;
    int j;
    HashSet writtenRegisters = new HashSet();
    SimulationObject so;
    for(j=0;j<=s;j++) {
      so = (SimulationObject) bitReferences.get(j);
      out.print(indent);
      if(so instanceof Register) {
        out.print("reg_"+so.getIdStr()+"_in");
        writtenRegisters.add(so);
      }
      else if(so instanceof Bus) out.print(((Bus) so).getVHDLName());
      else out.print ("ERROR_"+so.getIdStr());
      out.println("("+bitNumbers.get(j)+") <= net_C"+c+"("+i
		  +") WHEN C("+c+")='1' ELSE 'Z';");
      i--;
    }
    /*
    for(Iterator it=writtenRegisters.iterator();it.hasNext();)
      out.println("reg_"+((SimulationObject) it.next()).getIdStr()
		  +"_set <= '1' WHEN "+conditionString+" ELSE UNAFFECTED;");
    */
  }

  public boolean equals(Object o) {
    if(!(o instanceof BitSequence)) return false;
    BitSequence b = (BitSequence) o;
    if((bitReferences.size() != b.bitReferences.size())
     ||(bitNumbers.size() != b.bitNumbers.size())) return false;
    ListIterator refIt = bitReferences.listIterator();
    ListIterator brefIt = b.bitReferences.listIterator();
    ListIterator numIt = bitNumbers.listIterator();
    ListIterator bnumIt = b.bitNumbers.listIterator();
    for(;refIt.hasNext();) if(!(
      refIt.next().equals(brefIt.next())
      &&
      numIt.next().equals(bnumIt.next())
    )) return false;

    return true;
  }

  public int hashCode() {
    return 1; // BAD BAD BAD
  }
}
