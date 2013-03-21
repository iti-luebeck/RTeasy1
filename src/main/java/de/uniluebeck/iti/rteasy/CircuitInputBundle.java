package de.uniluebeck.iti.rteasy;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;

import de.uniluebeck.iti.rteasy.kernel.BitRange;

public class CircuitInputBundle {

  private Hashtable portTable;
  private boolean controlSignalMap[];

  CircuitInputBundle(int cscount) {
    portTable = new Hashtable();
    controlSignalMap = new boolean[cscount];
  }

  public void include(int cs, CircuitPortReference cpr) {
    ArrayList refList;
    int i;
    BitRange br;
    controlSignalMap[cs] = true;
    //System.err.println("insert "+cpr.toVHDLPortName()+" into:");
    //dumpPortTable();
    //System.err.println("--");
    if(portTable.containsKey(cpr.circuitPort)) {
      //System.err.println("contains key!");
      refList = (ArrayList) portTable.get(cpr.circuitPort);
      i = 0;
      br = cpr.bitRange;
      // join and remove all BitRanges from list adjacent to or intersecting
      // with cpr.bitRange
      while(i<refList.size()) {
        if(((BitRange) refList.get(i)).joinPossibleWith(cpr.bitRange)) {
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
      refList.add(cpr.bitRange);
      portTable.put(cpr.circuitPort,refList);
    }
  }

  public void dumpPortTable() {
    CircuitPort cp;
    BitRange br;
    ArrayList refList;
    ListIterator li;
    System.err.println("dumpPortTable():");
    for(Enumeration en=portTable.keys();en.hasMoreElements();) {
      cp = (CircuitPort) en.nextElement();
      System.err.println(cp.toSignalName()+" (hc: "+cp.hashCode()+")");
      refList = (ArrayList) portTable.get(cp);
      for(li=refList.listIterator();li.hasNext();)
        System.err.println("  "+((BitRange) li.next()).toString());
    }
  }

  public CircuitPortReference map(CircuitPortReference cpr) {
    if(portTable.containsKey(cpr.circuitPort)) {
      //System.err.println("contains "+cpr.circuitPort.toSignalName());
      ArrayList refList = (ArrayList) portTable.get(cpr.circuitPort);
      // search surrounding or equal BitRange to cpr.bitRange
      for(int i=0;i<refList.size();i++) {
        //System.err.print(((BitRange) refList.get(i)).toVHDL()+" contains "+cpr.bitRange.toVHDL()+" = ");
        if(((BitRange) refList.get(i)).contains(cpr.bitRange)) {
          //System.err.println("true");
          return new CircuitPortReference(cpr.circuitPort,(BitRange) refList.get(i));
        }
        //else System.err.println("false");
      }
      // not found
      return null;
    }
    else return null;
  }

  public void emitPortDeclarations(String indent, PrintWriter out) {
    ArrayList refList;
    CircuitPort key;
    ListIterator it;
    boolean hasCSig = false;
    for(int i=0;i<controlSignalMap.length;i++) if(controlSignalMap[i]) {
      out.print((hasCSig?", ":indent)+"C"+i);
      hasCSig = true;
    }
    if(hasCSig) out.println(" : IN  std_logic;");
    for(Enumeration en=portTable.keys();en.hasMoreElements();) {
      key = (CircuitPort) en.nextElement();
      refList = (ArrayList) portTable.get(key);
      for(it=refList.listIterator();it.hasNext();)
        out.println(indent+(new CircuitPortReference(key,(BitRange) it.next())).toVHDLPortDecl("IN",false,null)+";");
    }
  }

  public void emitPortMap(String indent, PrintWriter out, SignalsData signalsData) {
    ArrayList refList;
    CircuitPort key;
    ListIterator it;
    for(int i=0;i<controlSignalMap.length;i++) if(controlSignalMap[i])
      out.println(indent+"C"+i+" => C_SIG("+i+"),");
    for(Enumeration en=portTable.keys();en.hasMoreElements();) {
      key = (CircuitPort) en.nextElement();
      refList = (ArrayList) portTable.get(key);
      for(it=refList.listIterator();it.hasNext();)
        out.println(indent+(new CircuitPortReference(key,(BitRange) it.next())).getVHDLPortMap(signalsData)+",");
    }
  }
}
