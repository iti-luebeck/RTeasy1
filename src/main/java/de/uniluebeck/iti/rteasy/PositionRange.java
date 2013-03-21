package de.uniluebeck.iti.rteasy;

import de.uniluebeck.iti.rteasy.gui.IUI;

public class PositionRange {
  public int beginLine, beginColumn, endLine, endColumn;
  public String beginStr, endStr, rangeStr;

  public PositionRange(int a, int b, int c, int d) {
    beginLine = a;
    beginColumn  = b;
    endLine = c;
    endColumn = d;
    beginStr = IUI.get("LINE")+" "+beginLine+", "+IUI.get("COLUMN")+" "+beginColumn;
    endStr = IUI.get("LINE")+" "+endLine+", "+IUI.get("COLUMN")+" "+endColumn;
    rangeStr = beginStr+" - "+endStr;    
  }

  public boolean equals(Object obj) {
    if(obj instanceof PositionRange) {
      PositionRange pr = (PositionRange) obj;
      return beginLine == pr.beginLine && beginColumn == pr.beginColumn
          && endLine == pr.endLine     && endColumn == pr.endColumn;
    }
    else return false;
  }

  public int hashCode() {
    return beginLine * endColumn - endLine * beginColumn;
  }

  public String toString() { return beginStr; }

}
