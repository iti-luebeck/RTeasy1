package de.uniluebeck.iti.rteasy.kernel;

import java.io.PrintWriter;
import java.util.BitSet;

import de.uniluebeck.iti.rteasy.PositionRange;

public abstract class RegBus extends SimulationObject {
  protected int width, offset;
  protected boolean direction;
  protected int regBusId;

  RegBus(String tname, PositionRange tpr, int twidth, int toffset,
	 boolean tdirection) {
    super(tname,tpr);
    width = twidth;
    offset = toffset;
    direction = tdirection;
  }

  public void setRegBusId(int i) { regBusId = i; }
  public int getRegBusId() { return regBusId; }

  public int getWidth() { return width; }
  public int getOffset() { return offset; }
  public boolean getDirection() { return direction; }
  
  public int leftBound() { return offset+(direction?(width-1):0); }
  public int rightBound() { return offset+(direction?0:(width-1)); }

  public boolean exceedsRightBound(int i) {
    if(direction) return i<rightBound();
    else return i>rightBound();
  }

  /**
   * Converts array bit numbering (leftmost bit has index 0, rightmost
   * bit has index getWidth()-1) to register/bus bit numbering.
   * Useful for administration of register/bus contents in an array table.
   * @param ai array index of bit
   * @return bit index in register/bus bit numbering
   */
  public int array2BitIndex(int ai) {
    return offset+(direction?(width-1-ai):ai);
  }

  /**
   * Converts register/bus bit numbering to array bit numbering.
   * @see array2BitIndex
   * @param bi bit index in register/bus numbering
   * @return array index of bit
   */
  public int bitIndex2Array(int bi) {
    return direction?(offset+width-1-bi):(bi-offset);
  }

  public int nextRightIndex(int i) {
    if(direction) return i-1;
    else return i+1;
  }

  public String getVHDLdirection() { return direction?"DOWNTO":"TO"; }


  public String getVHDLType() {
    if(direction)
      return "std_logic_vector ("+Integer.toString(offset+width-1)+
	" DOWNTO "+Integer.toString(offset)+")";
    else
      return "std_logic_vector ("+Integer.toString(offset)+" TO "+
	Integer.toString(offset+width-1)+")";
  }

  public String getPrettyDecl() { 
    if(direction) 
      return getIdStr()+" ("+Integer.toString(offset+width-1)+":"
        +Integer.toString(offset)+")";
    else
      return getIdStr()+" ("+Integer.toString(offset)+":"
        +Integer.toString(offset+width-1)+")";
  }
  
  public String getWidthDecl() { 
	    if(direction) 
	      return "("+Integer.toString(offset+width-1)+":"
	        +Integer.toString(offset)+")";
	    else
	      return "("+Integer.toString(offset)+":"
	        +Integer.toString(offset+width-1)+")";
	  }

  public boolean checkBitRange(BitRange br) {
    if(direction)
      return br.begin >= br.end && br.begin <= (offset+width-1)
          && br.end >= offset;
    else
      return br.begin <= br.end && br.begin >= offset
          && br.end <= (offset+width-1);
  }

  public abstract BitSet getContentBitSet();
  public abstract BitVector getContent();
  public abstract void setContent(BitSet bs);
  public abstract void setContent(BitVector bv);
  public abstract void editContent(BitSet bs);
  public abstract void editContent(BitVector bv);
  public abstract boolean get(int i);
  public abstract boolean set(int i, boolean b);
  public abstract void clear();  
  public abstract boolean written();
  public abstract String getContentStr(int base);
  public boolean equals(Object o) {
    if(o instanceof RegBus) return getIdStr().equals(((RegBus) o).getIdStr());
    else return false;
  }
  public int hashCode() { return getRegBusId(); }
}
