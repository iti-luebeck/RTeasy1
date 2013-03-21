package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.frontend.ParseException;
import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.Bus;
import de.uniluebeck.iti.rteasy.kernel.Register;

public class ASTRegBusDecl extends RTSimNode {
  private int width = 1;
  private int offset = 0;
  private String name;
  private Register register = null;
  private Bus bus = null;
  private boolean direction = true;
 
  public ASTRegBusDecl(int id) {super(id);}

  public void setName(String s) { name = s; }
  public String getName() { return name; }
  public void setBitRange(BitRange br) throws ParseException {
    if(br.begin < 0 || br.end < 0)
      throw new ParseException("Falscher Bit-Bereich bei Deklaration, beide Grenzen m�ssen positive Ganzzahlen sein.");
    else if(br.begin < br.end) {
      width = br.end-br.begin+1;
      offset = br.begin;
      direction = false;
    }
    else {
      width = br.begin-br.end+1;
      offset = br.end;
      direction = true;
    } 
  }

  public void setWidth(int w) { this.width = w;}
  public boolean getDirection() { return direction; }
  public int getWidth() { return width; }
  public int getOffset() { return offset; }
  public void setRegister(Register r) { register = r; }
  public Register getRegister() { return register; }
  public void setBus(Bus b) { bus = b; }
  public Bus getBus() { return bus; }

  public String toString() {
    if(direction) 
      return name+" ("+Integer.toString(offset+width-1)+":"
        +Integer.toString(offset)+")";
    else
      return name+" ("+Integer.toString(offset)+":"
        +Integer.toString(offset+width-1)+")"; 
  }
} 
