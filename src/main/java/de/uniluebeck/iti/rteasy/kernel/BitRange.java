package de.uniluebeck.iti.rteasy.kernel;
public class BitRange {

  public int begin, end; 
  public boolean direction;

  public BitRange() {
    direction = true; // descending
    begin = 0;
    end = 0;
  }

  public BitRange(int bitno) {
    direction = true; // descending
    begin = bitno;
    end = bitno;
  }

  public BitRange(int bitno, boolean dir) {
    direction = dir;
    begin = bitno;
    end = bitno;
  }

  public BitRange(int tbegin, int tend) {
    if(tbegin<tend) direction = false; // ascending
    else direction = true; // descending
    begin = tbegin;
    end = tend;
  }

  public BitRange(int tbegin, int tend, boolean dir) {
    direction = dir;
    begin = tbegin;
    end = tend;
  }

  public int getWidth() {
    if(begin<=end) return end-begin+1;
    else return begin-end+1;
  }

  public String toString() {
    if(begin==end) return "("+Integer.toString(begin)+")";
    else return "("+Integer.toString(begin)+":"+Integer.toString(end)+")";
  }

  /**
   * @return true if BitRange starts left of x
   */
  public boolean lt(BitRange x) {
    if(begin<=end)
      if(x.begin <= x.end) return begin<x.begin;
      else return begin<x.end;
    else
      if(x.begin <= x.end) return end>x.end;
      else return end>x.end;
  }

  public boolean adjacentTo(BitRange x) {
    if(begin<end) return (x.begin == end+1) || (x.end+1 == begin);
    else if(begin>end) return (x.end == begin+1) || (x.begin+1 == end);
    else if(x.begin <= x.end) return (end+1==x.begin) || (x.end+1==begin);
         else return (begin==x.begin+1) || (begin+1==x.end);
  }

  public boolean intersectsWith(BitRange x) {
    if(begin<end) return x.begin >= begin && x.begin <= end
                       || x.end >= begin && x.end <= end;
    else if(begin > end) return x.begin <= begin && x.begin >= end
             || x.end <= begin && x.end >= end;
    else if(x.begin <= x.end) return begin >= x.begin && begin <= x.end;
         else return begin <= x.begin && begin >= x.end;
  }

  public boolean joinPossibleWith(BitRange x) {
    return direction==x.direction && (intersectsWith(x) || adjacentTo(x));
  }

  public boolean contains(BitRange x) {
    if(begin<end) return (x.begin<=x.end) && (x.begin>=begin) && (x.end<=end);
    else if(begin>end) return (x.begin>=x.end) && (x.begin<=begin) && (x.end>=end);
    else return begin==x.begin && end==x.end;
  }

  /**
   * joins BitRange with other BitRange x if they intersect and have the
   *  same direction (begin <= end and x.begin <= x.end or
   *  begin > end and x.begin > x.end or are adjacent
   */
  public BitRange joinWith(BitRange x) {
    if(joinPossibleWith(x))
      if(direction) 
        return new BitRange((begin>x.begin?begin:x.begin),(end<x.end?end:x.end),direction);
      else
        return new BitRange((begin<x.begin?begin:x.begin),(end>x.end?end:x.end),direction);
    else return x;
  }

  public String toVHDLType(boolean ra, BitRange b) {
	  if(ra){
		  return "reg_array"+b.toVHDLArray();
	  } else {
		  return "std_logic_vector("+begin+(direction?" DOWNTO ":" TO ")+end+")";
    }
  }

  public String toVHDL() {
    return "("+begin+(direction?" DOWNTO ":" TO ")+end+")";
  }
  
  public String toVHDLArray() {
	  return "_"+begin+"_"+end;
  }
  
  public int nextRightIndex(int i) {
	    if(direction) return i-1;
	    else return i+1;
  }
}
