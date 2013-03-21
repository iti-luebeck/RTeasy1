package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.frontend.ParseException;
import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.RegisterArray;

public class ASTRegArrayDecl extends RTSimNode {
  
	public String name;
	private int width = 1;
	private int offset = 1;
	private boolean direction = true;
	private RegisterArray ra;
	private int numberOfRegisters;
	
	public ASTRegArrayDecl(int id) {
    super(id);
  }
  public void setName(String name) {
	  this.name = name;
  }
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
  public BitRange getBitRange() {
	  BitRange br;
	  int begin, end;
	  if(direction) {
		  end = offset;
		  begin = offset+width-1;
	  } else {
		  begin = offset;
		  end = offset+width-1;
	  }
	  return br = new BitRange(begin, end, direction);
  }
  
  public int getWidth() { return width;}
  public void setWidth(int w) { this.width = w;}
  public int getOffset() {return offset;}
  public boolean getDirection() {return direction;}
  public String getName() {return name;}
  public void setNumberOfRegisters(int n){
	  numberOfRegisters = n;
  }
  public int getNumberOfRegisters(){return numberOfRegisters;}
  public void setRegArray(RegisterArray r) { ra = r;}
  public RegisterArray getRegArray() {return ra;}
}