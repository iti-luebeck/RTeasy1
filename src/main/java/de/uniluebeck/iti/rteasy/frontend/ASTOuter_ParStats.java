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
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.kernel.Label;

public class ASTOuter_ParStats extends RTSimNode {
  private int statement_node_type = RTSimGlobals.ERR;
  private boolean hasNext = false;
  private boolean hasLabel = false;
  private String labelId = null;
  private Label label = null;

  public ASTOuter_ParStats(int id) {super(id); }

  public void setLabel(Label l) { label = l; }
  public Label getLabel() { return label; }
  public void setLabelId(String s) { labelId = s; hasLabel = true; }
  public boolean hasLabel() { return hasLabel; }
  public String getLabelId() { return labelId; }
  public void setStatNodeType(int nt) { statement_node_type = nt; }
  public int getStatNodeType() { return statement_node_type; }
  public void setHasNext(boolean b) { hasNext = b; }
  public boolean hasNext() { return hasNext; }
  public ASTIf_Stat getIfStatement() { return (ASTIf_Stat) jjtGetChild(0); }
  public ASTStat getStatement() { return (ASTStat) jjtGetChild(0); }
  public ASTSwitch_Case_Stat getSwitchStatement() {return (ASTSwitch_Case_Stat) jjtGetChild(0); }
  public ASTOuter_ParStats next() {
    if(hasNext) return (ASTOuter_ParStats) jjtGetChild(1);
    else return null;
  }
}
