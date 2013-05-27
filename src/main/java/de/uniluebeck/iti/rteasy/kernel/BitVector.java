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

public class BitVector {
  public static BitVector B00 = new BitVector("0");
  public static BitVector B01 = new BitVector("1");
  public static BitVector B02 = new BitVector("10");
  public static BitVector B03 = new BitVector("11");
  public static BitVector B04 = new BitVector("100");
  public static BitVector B05 = new BitVector("101");
  public static BitVector B06 = new BitVector("110");
  public static BitVector B07 = new BitVector("111");
  public static BitVector B08 = new BitVector("1000");
  public static BitVector B09 = new BitVector("1001");
  public static BitVector B10 = new BitVector("1010");
  public static BitVector B11 = new BitVector("1011");
  public static BitVector B12 = new BitVector("1100");
  public static BitVector B13 = new BitVector("1101");
  public static BitVector B14 = new BitVector("1110");
  public static BitVector B15 = new BitVector("1111");
  public final static BitVector BV_TRUE = new BitVector("1");
  public final static BitVector BV_FALSE = new BitVector("0");

  protected String val;

  public BitVector() {
    val = "";
  }

  public BitVector(String s) {
    val = s;
    cut_zeros();
  }

  public BitVector(BitVector b) {
    val = b.val;
    cut_zeros();
  }

  public void clear() { val = "0"; }

  public void makeWidth(int toWidth) {
    while(val.length() > toWidth) val = val.substring(1);
    while(val.length() < toWidth) val = "0" + val;
  }

  public void cut_zeros() {
    while(val.length() > 0 && val.charAt(0) == '0') val = val.substring(1);
  }

  public void add_back(boolean b) {
    val += b?"1":"0";
  }

  public void add_front(boolean b) {
    val = b?"1":"0" + val;
  }

  public boolean get(int pos) {
    if(pos < val.length()) 
      return val.charAt(val.length()-1-pos) == '1';
    else
      return false;
  }

  public void set(int pos, boolean b) {
    if(pos < val.length()) {
      val = val.substring(0,val.length()-pos-1) + (b?"1":"0")
          + val.substring(val.length()-pos);
    }
    else {
      for(int i=0;i<pos-val.length();i++) val = "0" + val;
      val = (b?"1":"0") + val;
    }
  } 

  public BitVector not(int toWidth) {
    String nval = "";
    int i;
    // Negation
    for(i=0;i<val.length();i++) nval += val.charAt(i) == '1'?"0":"1";
    for(;i<toWidth;i++) nval = "1" + nval;     
    return new BitVector(nval);
  }

  public int length() {
    return val.length();
  }

  public int getWidth() {
    int x = val.length();
    if(x == 0) return 1;
    else return x;
  }

  public BitVector plus(BitVector b, int toWidth) {
    BitVector bk = plus(b);
    bk.makeWidth(toWidth);
    return bk;
  }

  public BitVector plus(BitVector b) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    String nval = "";
    int carry = 0;
    int t = 0;
    for(int i=0;i<maxlen;i++) {
      t = carry;
      if(i<la) t += get(i)?1:0;
      if(i<lb) t += b.get(i)?1:0;
      carry = t>1?1:0;
      nval = (t%2==1?"1":"0") + nval;
    }
    if(carry==1) nval = "1" + nval;
    return new BitVector(nval);
  }

  public BitVector sign(int toWidth) {
    return not(toWidth).plus(new BitVector("1"),toWidth);
  }

  public BitVector minus(BitVector b, int toWidth) {
    return plus(b.sign(toWidth),toWidth);
  }

  // alle Vergleichsoperatoren ausprogrammiert wg. Optimierung

  public BitVector eq(BitVector b, int maxlen) {
    if(bv_equals(b)) return BV_TRUE;
    else return BV_FALSE;
  }

  public boolean equals(Object o) {
    if(o instanceof BitVector) return bv_equals((BitVector) o);
    else return false;
  }

  public boolean bv_equals(BitVector b) {
//    System.err.println("("+this.toString()+").bv_equals("+b.toString()+")");
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    for(int i=maxlen-1;i>=0;i--) {
      if(get(i) != b.get(i)) {
//        System.err.println("  = false");
        return false;
      }
    }
//    System.err.println("  = true");
    return true;
  }

  public boolean bv_lessthan(BitVector b, int maxlen) {
//    System.err.println("("+this.toString()+").bv_lessthan("+b.toString()+","+maxlen+")");
    if(b.get(maxlen-1) == get(maxlen-1)) {
    // both share the same sign
      for(int i=maxlen-2;i>=0;i--) if(get(i) != b.get(i)) // there is a difference
        if(get(i) == true) {
//          System.err.println("  = false");
          return false; // this bitvector is greater 
        }
        else {
//          System.err.println("  = true");
          return true;  // this bitvictor is less
        }
//      System.err.println("  = false");
      return false; // equality
    }
    else {
      //System.err.println("  = "+(get(maxlen-1)?"true":"false"));
    // signs are different, trivial case
    // if own sign is 1 (true) then bv_lessthan is true
      return get(maxlen-1);
    }
  }

  public int hashCode() {
    return val.hashCode();
  }
 
  public BitVector lt(BitVector b, int maxlen) {
    if(bv_lessthan(b,maxlen)) return BV_TRUE;
    else return BV_FALSE;
  }

  public BitVector le(BitVector b, int maxlen) {
    if(bv_equals(b) || bv_lessthan(b,maxlen)) return BV_TRUE;
    else return BV_FALSE;
  }

  public BitVector gt(BitVector b, int maxlen) {
    if(bv_equals(b) || bv_lessthan(b,maxlen)) return BV_FALSE;
    else return BV_TRUE;
  }

  public BitVector ge(BitVector b, int maxlen) {
    if(bv_lessthan(b,maxlen)) return BV_FALSE;
    else return BV_TRUE;
  }

  public BitVector ne(BitVector b, int maxlen) {
    if(bv_equals(b)) return BV_FALSE;
    else return BV_TRUE;
  }

  public BitVector and(BitVector b, int toWidth) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    maxlen = maxlen<toWidth?toWidth:maxlen;
    String nval = "";
    for(int i=0;i<maxlen;i++) 
      nval = (get(i)&&b.get(i)?"1":"0") + nval;
    return new BitVector(nval);
  }

  public BitVector nand(BitVector b, int toWidth) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    maxlen = maxlen<toWidth?toWidth:maxlen;
    String nval = "";
    for(int i=0;i<maxlen;i++) 
      nval = (get(i)&&b.get(i)?"0":"1") + nval;
    return new BitVector(nval);
  }

  public BitVector or(BitVector b, int toWidth) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    maxlen = maxlen<toWidth?toWidth:maxlen;
    String nval = "";
    for(int i=0;i<maxlen;i++) 
      nval = (get(i)||b.get(i)?"1":"0") + nval;
    return new BitVector(nval);
  }

  public BitVector nor(BitVector b, int toWidth) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    maxlen = maxlen<toWidth?toWidth:maxlen;
    String nval = "";
    for(int i=0;i<maxlen;i++) 
      nval = (get(i)||b.get(i)?"0":"1") + nval;
    return new BitVector(nval);
  }

  public BitVector xor(BitVector b, int toWidth) {
    int la = length();
    int lb = b.length();
    int maxlen = la>lb?la:lb;
    maxlen = maxlen<toWidth?toWidth:maxlen;
    String nval = "";
    for(int i=0;i<maxlen;i++) 
      nval = (get(i)!=b.get(i)?"1":"0") + nval;
    return new BitVector(nval);
  }

  public BitVector mult_int(int m) {
    if(m<0) return this;
    BitVector nb = new BitVector();
    BitVector b = new BitVector(this);
    String indent = "0";
    while(m%2 == 0) {
      m /= 2;
      b.val += indent;
      indent += "0";
    }
    for(int i=0;i<m;i++) {
      nb = nb.plus(b);
    }
    return nb;
  }

  public String toString() {
    if(val.equals("")) return "0";
    else return val;
  }

  public String toVHDL() { return "\"" + toString() + "\""; }

  public String toVHDL(int width) {
    String bk = "\"";
    for(int i=width-1;i>=0;i--) bk += get(i)?"1":"0";
    return bk + "\"";
  }

  public boolean[] toBoolArray(int toWidth) {
    int l = val.length();
    if(toWidth == 0 ) { 
      boolean back[] = new boolean[l];
      for(int i=0;i<l;i++) back[i] = val.charAt(l-i-1) == '1';
      return back;
    }
    else {
      boolean back[] = new boolean[toWidth];
      for(int i=0;i<toWidth;i++) back[i] = get(i);
      return back;
    }
  }
}
