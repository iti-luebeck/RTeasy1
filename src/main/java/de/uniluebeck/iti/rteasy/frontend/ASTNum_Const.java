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
