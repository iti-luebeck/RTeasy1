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
