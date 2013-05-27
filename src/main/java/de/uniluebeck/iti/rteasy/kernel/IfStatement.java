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


package de.uniluebeck.iti.rteasy.kernel;

import java.util.*;
import java.io.PrintWriter;

import de.uniluebeck.iti.rteasy.PositionRange;
import de.uniluebeck.iti.rteasy.RTSimGlobals;
import de.uniluebeck.iti.rteasy.SignalsData;
import de.uniluebeck.iti.rteasy.frontend.ASTIf_Stat;

public class IfStatement {

  private boolean hasElse = false;
  private PositionRange pr;
  private ProgramControl pc;
  private ParallelStatements thenStats = null;
  private ParallelStatements elseStats = null;
  private Expression expression = null;
  private String lf = "\n";

  public void dumpSimTree(PrintWriter out, String indent) {
    out.println(indent+"condition : Expression = ");
    expression.dumpSimTree(out,indent+"  ");
    out.println(indent+"thenStats : ParallelStatements = ");
    thenStats.dumpSimTree(out,indent+"  ");
    if(hasElse) {
      out.println(indent+"elseStats : ParallelStatements = ");
      elseStats.dumpSimTree(out,indent+"  ");
    }
  }

  public IfStatement copy() {
    ParallelStatements toThen = null;
    ParallelStatements toElse = null;
    if(thenStats != null) toThen = thenStats.copyNoLabelsLeftOnly();
    if(hasElse && elseStats != null) toElse = elseStats.copyNoLabelsLeftOnly();
    return new IfStatement(hasElse,pr,pc,toThen,toElse,expression,lf);
  }

  IfStatement(boolean hE, PositionRange tpr, ProgramControl tpc,
	      ParallelStatements toThen, ParallelStatements toElse,
	      Expression toExpression, String tlf) {
    hasElse = hE; pr = tpr; pc = tpc; thenStats = toThen;
    elseStats = toElse; expression = toExpression; lf = tlf;
    //expression.sinkWidth(1);
  }

  IfStatement(ProgramControl tpc, ASTIf_Stat if_stat) {
    pr = if_stat.getPositionRange();
    pc = tpc;
    hasElse = if_stat.hasElse();
    expression = new Expression(pc,if_stat.getExpression());
    expression.sinkWidth(1);
    thenStats = new ParallelStatements(pc,if_stat.getThen());
    if(hasElse) elseStats = new ParallelStatements(pc,if_stat.getElse());
    try {
      lf = System.getProperty("line.separator");
    } 
    catch(Throwable t) {
      lf = "\n";
    }
  }

  IfStatement(Expression expr, ParallelStatements ps) {
    expression = expr;
    //expression.sinkWidth(1);
    thenStats = ps;
  }

  IfStatement(Expression expr, ParallelStatements ps1, ParallelStatements ps2){
    expression = expr;
    //expression.sinkWidth(1);
    thenStats = ps1;
    elseStats = ps2;
    hasElse = true;
  }

  public void eleminateElse(List newStats) {
    if(thenStats != null) {
      thenStats.eleminateElse();
      newStats.add(new IfStatement(expression,thenStats));
    }
    if(hasElseStats()) {
      elseStats.eleminateElse();
      newStats.add(new IfStatement(new Expression(RTSimGlobals.NOT,expression),
				   elseStats));
    }
  }

  public void deNest(Expression cond, List newStats) {
    if(expression == null) return;
    Expression newCond;
    Expression eTrue = new Expression("1");
    if(cond.equals(eTrue)) newCond = expression;
    else newCond =  new Expression(RTSimGlobals.AND,cond,expression);
    if(thenStats != null) thenStats.deNest(newCond,newStats);
  }

  public Expression getExpression() { return expression; }

  public ParallelStatements getThenStats() { return thenStats; }

  public ParallelStatements getElseStats() { if(hasElse) return elseStats;
                                    else return null; }

  public boolean hasElseStats() {
    if(hasElse && elseStats != null) {
      return ! elseStats.edge1useless();
    }
    else return false;
  }

  public void emitDeltaFunction(String indent, PrintWriter out,
				SignalsData signalsData) {
    String condition = expression.getVHDLcondition(signalsData);
    out.println(indent+"IF "+condition+" THEN");
    thenStats.emitDeltaFunction(indent+"  ",out,signalsData);
    if(hasElse) {
      out.println(indent+"ELSE");
      elseStats.emitDeltaFunction(indent+"  ",out,signalsData);
    }
    out.println(indent+"END IF;");
  }

  public ArrayList getStatements(int edgeType, Hashtable registers,
		  Hashtable regarrays) {
	ArrayList back = new ArrayList();
	boolean ifexpr = expression.eval(edgeType).get(0);
    back.add(new Statement(pc,expression.getPositionRange(),ifexpr));
	if(ifexpr == true){
		back.addAll(thenStats.getStatements(edgeType,0, registers, regarrays));
	}
    else if(hasElse) back.addAll(elseStats.getStatements(edgeType,0, registers, regarrays));
	return back;
  }

  /**
   * @return true, falls sowohl then- als auch else-Zweig nur nops enthalten
   */
  public boolean useless() {
    if(thenStats == null) {
      // sollte nicht vorkommen!
      System.err.println("thenStats == null!");
      if(expression != null) System.err.println(expression.toString());
      return true;
    }
    if(hasElse) return thenStats.edge1useless() && elseStats.edge1useless();
    else return thenStats.edge1useless();
  }

  public PositionRange getPositionRange() { return pr; }

  public String toString(String indentStr, Hashtable registers, Hashtable regarrays) {
    String cs, ts, es;
    cs = "ERROR";
    if(expression != null) cs = expression.toString();
    ts = "ERROR";
    if(thenStats != null) ts = thenStats.toString(indentStr+"  ", registers, regarrays);
    es = "ERROR";
    if(elseStats != null) es = elseStats.toString(indentStr+"  ", registers, regarrays);
    return indentStr + "if " + cs + " then" + lf
         + ts
         + (hasElse?(lf + indentStr + "else" + lf + es):"")
         + lf + indentStr + "fi";
  }

  public String toString() { return toString("", null, null); }

  public IfStatement cleanUp() {
    if(expression == null) return null;
    boolean noThen = false;
    boolean noElse = false;
    if(thenStats != null) {
      thenStats.cleanUp();
      if(thenStats.edge1useless()) noThen = true;
    }
    else noThen = true;
    if(hasElse && elseStats != null) {
      elseStats.cleanUp();
      if(elseStats.edge1useless()) noElse = true;
    }
    else noElse = true;
    if(noThen) {
      if(noElse) return null;
      else {
        hasElse = false;
        thenStats = elseStats;
        elseStats = null;
        expression = new Expression(RTSimGlobals.NOT,expression);
        return this;
      }
    }
    else {
      if(noElse) {
        hasElse = false;
        elseStats = null;
        return this;
      }
      else return this;
    }
  }
      
  public void expandGotos(RTProgram rtprog) {
    if(thenStats != null) thenStats.expandGotos(rtprog);
    if(hasElse && elseStats != null) elseStats.expandGotos(rtprog);
  }

  public Expression getGotoExpression() {
    Expression eFalse = new Expression("0");
    Expression eTrue = new Expression("1");
    Expression gt = thenStats.getGotoExpression();
    Expression gf = eFalse;
    if(hasElse && elseStats != null) gf = elseStats.getGotoExpression();
    if(gt.equals(eTrue)) {
      if(gf.equals(eTrue)) return eTrue;
      else if(gf.equals(eFalse)) return expression;
      else return new Expression(RTSimGlobals.OR, expression,
				 new Expression(RTSimGlobals.AND,
						new Expression(RTSimGlobals.NOT
							       , expression),
						gf));
    }
    else if(gt.equals(eFalse)) {
      if(gf.equals(eTrue)) return new Expression(RTSimGlobals.NOT,
						 expression);
      else if(gf.equals(eFalse)) return eFalse;
      else return new Expression(RTSimGlobals.AND,
				 new Expression(RTSimGlobals.NOT,
						expression), gf);
    }
    else {
      if(gf.equals(eTrue))
        return new Expression(RTSimGlobals.OR,
			      new Expression(RTSimGlobals.NOT, expression),
			      new Expression(RTSimGlobals.AND, expression, gt)
			      );
      else if(gf.equals(eFalse))
        return new Expression(RTSimGlobals.AND, expression, gt);
      else
        return new Expression(RTSimGlobals.OR,
			      new Expression(RTSimGlobals.AND, expression, gt),
			      new Expression(RTSimGlobals.AND,
					     new Expression(RTSimGlobals.NOT,
							    expression), gf)
			      );
    }
  }

  void calculateSignals(SignalsData signalsData) {
    expression.calculateSignals(signalsData);
    thenStats.calculateSignals(signalsData);
    if(hasElse) elseStats.calculateSignals(signalsData);
  }

  public boolean isConditionalTransferOperation() {
    if(thenStats != null) return thenStats.isUnconditionalTransferOperation();
    else return false;
  }

  public Statement getConditionalTransferOperation() {
    return thenStats.getUnconditionalTransferOperation();
  }

  public boolean isConditionalGoto() {
    if(thenStats != null) return thenStats.containsUnconditionalGoto();
    else return false;
  }

  public int getGotoState() {
    Label l = thenStats.getUnconditionalGoto();
    return l.getStatSeqEntry();
  }

  public Label getGotoLabel() {
    return thenStats.getUnconditionalGoto();
  }
} 
