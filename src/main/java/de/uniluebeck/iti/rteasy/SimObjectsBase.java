package de.uniluebeck.iti.rteasy;
public class SimObjectsBase {
  private int val = RTSimGlobals.BASE_BIN;

  public SimObjectsBase() {}
  public SimObjectsBase(int b) {
    setValue(b);
  }

  public int getValue() { return val; }

  public void setValue(int b) {
    val = b;
  }

  public String toString() {
    switch(val) {
      case RTSimGlobals.BASE_BIN: return "BIN";
      case RTSimGlobals.BASE_DEC: return "DEC";
      case RTSimGlobals.BASE_HEX: return "HEX";
      case RTSimGlobals.BASE_DEC2: return "DEC2";
      case RTSimGlobals.BASE_HEX2: return "HEX2";
      default: return "ERR";
    }
  }

  public boolean equals(Object o) {
    if(o instanceof SimObjectsBase) {
      return val == ((SimObjectsBase) o).getValue();
    }
    else return false;
  }

  public int hashCode() {
    switch(val) {
      case RTSimGlobals.BASE_BIN: return 0;
      case RTSimGlobals.BASE_DEC: return 1;
      case RTSimGlobals.BASE_HEX: return 2;
      case RTSimGlobals.BASE_DEC2: return 3;
      case RTSimGlobals.BASE_HEX2: return 4;
      default: return 10;
    }
  } 
 
} 
