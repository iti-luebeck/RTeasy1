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
import de.uniluebeck.iti.rteasy.frontend.Token;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.BitVector;

public class ASTNum_Const extends RTSimNode {
 
  private BitVector bv;
  private int base = RTSimGlobals.BASE_BIN;

  public ASTNum_Const(int id) {
    super(id);
  }

  public void setValDec(Token t) {
    base = RTSimGlobals.BASE_DEC;
    String s = t.image.toString();
    if(s.length() < 1) {bv = null; return; }
    for(int i=0;i<s.length();i++) {
      if(i==0) bv = new BitVector(BitVector.B00);
      else bv = bv.mult_int(10);
      switch(s.charAt(i)) {
        case '0': bv = bv.plus(BitVector.B00); break;
        case '1': bv = bv.plus(BitVector.B01); break;
        case '2': bv = bv.plus(BitVector.B02); break;
        case '3': bv = bv.plus(BitVector.B03); break;
	case '4': bv = bv.plus(BitVector.B04); break;
	case '5': bv = bv.plus(BitVector.B05); break;
	case '6': bv = bv.plus(BitVector.B06); break;
	case '7': bv = bv.plus(BitVector.B07); break;
        case '8': bv = bv.plus(BitVector.B08); break;
        case '9': bv = bv.plus(BitVector.B09); break;
      }
    }
    bv.cut_zeros();
  }

  public void setValHex(Token t) {
    base = RTSimGlobals.BASE_HEX;
    String s = t.image.toString();
    s = s.substring(1);
    if(s.length() < 1) {bv = null; return; }
    String nval = "";
    for(int i=0;i<s.length();i++) switch(s.charAt(i)) {
        case '0': nval += "0000"; break;
        case '1': nval += "0001"; break;
        case '2': nval += "0010"; break;
        case '3': nval += "0011"; break;
        case '4': nval += "0100"; break;
        case '5': nval += "0101"; break;
        case '6': nval += "0110"; break;
        case '7': nval += "0111"; break;
        case '8': nval += "1000"; break;
        case '9': nval += "1001"; break;
        case 'A': nval += "1010"; break;
        case 'B': nval += "1011"; break;
        case 'C': nval += "1100"; break;
        case 'D': nval += "1101"; break;
        case 'E': nval += "1110"; break;
        case 'F': nval += "1111"; break;
    }
    bv = new BitVector(nval); // cut_zeros durch Konstruktor
  }
 
  public void setValBin(Token t) {
    base = RTSimGlobals.BASE_BIN;
    bv = new BitVector(t.image.toString().substring(1));
  }

  public BitVector getBitVector() {return bv; }
  public int getBase() { return base; }

  public String toString() {
    return "Num_Const: "+bv;
  }
}
