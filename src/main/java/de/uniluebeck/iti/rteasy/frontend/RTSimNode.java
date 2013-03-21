package de.uniluebeck.iti.rteasy.frontend;
import de.uniluebeck.iti.rteasy.frontend.SimpleNode;
import de.uniluebeck.iti.rteasy.frontend.Token;
import de.uniluebeck.iti.rteasy.PositionRange;

public class RTSimNode extends SimpleNode {
  protected Token firstToken, lastToken;
  protected boolean noTokens = true;
  protected int beginLine, beginColumn, endLine, endColumn;
  protected String beginStr, endStr, rangeStr;

  public RTSimNode(int id) {super(id);}

  public void setFirstToken(Token t) { firstToken = t; }
  public Token getFirstToken() { return firstToken; }
  public void setLastToken(Token t) {
    lastToken = t;
    if(lastToken.next == firstToken) {
      firstToken = null;
      lastToken = null;
      noTokens = true;
      beginStr = "<NO POSITION>";
      endStr = "<NO POSITION>";
      rangeStr = "<NO POSITION>";
      beginLine = -1;
      beginColumn = -1;
      endLine = -1;
      endColumn = -1;
    }
    else {
      noTokens = false;
      beginLine = firstToken.beginLine;
      beginColumn = firstToken.beginColumn;
      endLine = lastToken.endLine;
      endColumn = lastToken.endColumn;
      beginStr = "line "+beginLine+", column "+beginColumn;
      endStr = "line "+endLine+", column "+endColumn; 
      rangeStr = beginStr+" - "+endStr;
    }
  }
  public int getBeginLine() { return beginLine; }
  public int getBeginColumn() { return beginColumn; }
  public int getEndLine() { return endLine; }
  public int getEndColumn() { return endColumn; }
  public String getBeginStr() { return beginStr; }
  public String getEndStr() { return endStr; }
  public String getRangeStr() { return rangeStr; }
  public boolean empty() { return noTokens; }
  public PositionRange getPositionRange() {
    return new PositionRange(beginLine,beginColumn,endLine,endColumn);
  }
}
