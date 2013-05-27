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
