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
