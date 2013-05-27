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
import java.util.HashSet;

import de.uniluebeck.iti.rteasy.gui.IUI;

public class RTSimGlobals {
  public final static int UNUSED = -1;
  public final static int SIGN = 1;
  public final static int PLUS = 3;
  public final static int MINUS = 4;
  public final static int LT = 5;
  public final static int LE = 6;
  public final static int GT = 7;
  public final static int GE = 8;
  public final static int EQ = 9;
  public final static int NE = 10;
  public final static int NOT = 11;
  public final static int AND = 12;
  public final static int NAND = 13;
  public final static int OR = 14;
  public final static int NOR = 15;
  public final static int XOR = 16;
  public final static int NUM_CONST = 17;
  public final static int BIT_SEQ = 18;
  public final static int ERR = 70;
  public final static int ASSIGN = 100;
  public final static int READ = 101;
  public final static int WRITE = 102;
  public final static int GOTO = 103;
  public final static int ASSIGN_NORMAL = 104;
  public final static int ASSIGN_REG2BUS = 105;
  public final static int ASSIGN_BUS2REG = 106; 
  public final static int NOP = 107;
  public final static int IFBAILOUT = 109;
  public final static int STAT = 110;
  public final static int IFSTAT = 111;
  public final static int SWITCH = 112;
  public final static int SWITCHBAILOUT = 113;
  public final static int REGISTER = 120;
  public final static int BUS = 121;
  public final static int MEMORY = 122;
  public final static int LABEL = 123;
  public final static int ARRAY = 124;
  public final static int BASE_BIN = 200;
  public final static int BASE_DEC = 201;
  public final static int BASE_HEX = 202;
  public final static int BASE_DEC2 = 203;
  public final static int BASE_HEX2 = 204;
  public final static int SIMTYPE_MEALY = 300;
  public final static int SIMTYPE_2EDGE = 301;
  public final static int OSTAT_TYPE_MEALY = 302;
  public final static int OSTAT_TYPE_2EDGE_1 = 303;
  public final static int OSTAT_TYPE_2EDGE_2 = 304;
  public final static int DIR_IN = 320;
  public final static int DIR_OUT = 321;

  private final static String VHDL_reserved_words [] = {
    "abs","access","after","alias","all","and","architecture",
    "array","assert","attribute","begin","block","body","buffer",
    "bus","case","component","configuration","constant","disconnect",
    "downto","else","elsif","end","entity","exit","file","for",
    "function","generate","generic","group","guarded","if","impure",
    "in","inertial","inout","is","label","library","linkage","literal",
    "loop","map","mod","nand","new","next","nor","not","null","of","on",
    "open","or","others","out","package","port","postponed","procedure",
    "process","pure","range","record","register","reject","rem","report",
    "return","rol","ror","select","severity","signal","shared","sla",
    "sll","sra","srl","subtype","then","to","transport","type",
    "unaffected","units","until","use","variable","wait","when","while",
    "with","xnor","xor",
    "read","write"};
  private final static String rteasy_reserved_words [] = {
    "CLK","CLK_SIGNAL","C","I" };

  private static HashSet<String> lookup_VHDL_reserved_words;
  private static HashSet<String> lookup_rteasy_reserved_words;

  public static void init() {
    int i;
    lookup_VHDL_reserved_words = new HashSet<String>();
    for(i=0;i<VHDL_reserved_words.length;i++)
      lookup_VHDL_reserved_words.add(VHDL_reserved_words[i]);
    lookup_rteasy_reserved_words = new HashSet<String>();
    for(i=0;i<rteasy_reserved_words.length;i++)
      lookup_rteasy_reserved_words.add(rteasy_reserved_words[i]);
  }

  public static boolean isReservedWord(String s) {
    String ls = s.toLowerCase();
    return lookup_VHDL_reserved_words.contains(ls)
      || lookup_rteasy_reserved_words.contains(ls);
  }

  public static int boolArrayCompare(boolean a[], boolean b[]) {
    int maxlen = a.length>b.length?a.length:b.length;
    boolean aval, bval;
    for(int i=maxlen-1;i>=0;i--) {
      aval = i<a.length?a[i]:false;
      bval = i<b.length?b[i]:false;
      if(aval && !bval) return 1;
      if(!aval && bval) return -1;
    }
    return 0;
  }

  public static String typeToString(int ot) {
    String s;
    switch(ot) {
      case SIGN: s = "SIGN"; break;
      case PLUS: s = "PLUS"; break;
      case MINUS: s = "MINUS"; break;
      case LT: s = "LT"; break;
      case LE: s = "LE"; break;
      case GT: s = "GT"; break;
      case GE: s = "GE"; break;
      case EQ: s = "EQ"; break;
      case NE: s = "NE"; break;
      case NOT: s = "NOT"; break;
      case AND: s = "AND"; break;
      case NAND: s = "NAND"; break;
      case OR: s = "OR"; break;
      case NOR: s = "NOR"; break;
      case XOR: s = "XOR"; break;
      case ERR: s = "ERR"; break;
      case NUM_CONST: s = "NUM_CONST"; break;
      case BIT_SEQ: s = "BIT_SEQ"; break;
      case ASSIGN: s = "ASSIGN"; break;
      case READ: s = "READ"; break;
      case WRITE: s = "WRITE"; break;
      case GOTO: s = "GOTO"; break;
      case NOP: s = "NOP"; break;
      case ASSIGN_NORMAL: s = "ASSIGN_NORMAL"; break;
      case ASSIGN_BUS2REG: s = "ASSIGN_BUS2REG"; break;
      case ASSIGN_REG2BUS: s = "ASSIGN_REG2BUS"; break;
      case STAT: s = "STAT"; break;
      case IFSTAT: s = "IFSTAT"; break;
      case REGISTER: s = "REGISTER"; break;
      case BUS: s = "BUS"; break;
      case MEMORY: s = "MEMORY"; break;
      case LABEL: s = "LABEL"; break;
      default: s = "<WRONG TYPE WITH NO. "+ot+" >";
    }
    return s;
  }

  public static String typeToLiteral(int ot) {
    String s;
    switch(ot) {
      case SIGN: s = "-"; break;
      case PLUS: s = "+"; break;
      case MINUS: s = "-"; break;
      case LT: s = "<"; break;
      case LE: s = "<="; break;
      case GT: s = ">"; break;
      case GE: s = ">="; break;
      case EQ: s = "="; break;
      case NE: s = "<>"; break;
      case NOT: s = "not"; break;
      case AND: s = "and"; break;
      case NAND: s = "nand"; break;
      case OR: s = "or"; break;
      case NOR: s = "nor"; break;
      case XOR: s = "xor"; break;
      case ERR: s = "ERR"; break;
      case ASSIGN: s = "<-"; break;
      case READ: s = "read"; break;
      case WRITE: s = "write"; break;
      case GOTO: s = "goto"; break;
      case NOP: s = "nop"; break;
      case REGISTER: s = "register"; break;
      case BUS: s = "bus"; break;
      case MEMORY: s = "memory"; break;
      default: s = "<WRONG TYPE WITH NO. "+ot+" >";
    }
    return s;
  }

  public static int getOperatorPrecedence(int ot) {
    switch(ot) {
      case RTSimGlobals.NUM_CONST:
      case RTSimGlobals.BIT_SEQ:
        return 0;
      case RTSimGlobals.SIGN:
        return 1;
      case RTSimGlobals.PLUS:
      case RTSimGlobals.MINUS:
        return 2;
      case RTSimGlobals.LT:
      case RTSimGlobals.LE:
      case RTSimGlobals.GT:
      case RTSimGlobals.GE:
        return 3;
      case RTSimGlobals.EQ:
      case RTSimGlobals.NE:
        return 4;
      case RTSimGlobals.NOT:
        return 5;
      case RTSimGlobals.AND:
      case RTSimGlobals.NAND:
        return 6;
      case RTSimGlobals.OR:
      case RTSimGlobals.NOR:
      case RTSimGlobals.XOR:
        return 7;
      default:
        return 10;
    }  
  }

  public static String int2hexdigit(int digit) {
    if(digit>=0 && digit<10) return Integer.toString(digit);
    else switch(digit) {
      case 10: return "A";
      case 11: return "B";
      case 12: return "C";
      case 13: return "D";
      case 14: return "E";
      case 15: return "F";
    }
    return "X";
  }

  public static String base2String(int base) {
    switch(base) {
      case RTSimGlobals.BASE_BIN:
        return "%";
      case RTSimGlobals.BASE_DEC:
      case RTSimGlobals.BASE_DEC2:
        return "";
      case RTSimGlobals.BASE_HEX:
      case RTSimGlobals.BASE_HEX2:
        return "$";
      default:
        return "<WRONG BASE>";
    }
  }

  public static String boolArray2String(boolean bits[], int base) {
    long sum = 0;
    long pot2 = 1;
    long pot2max = 1 << (bits.length-1);
    for(int i=0;i<bits.length;i++,pot2*=2) if(bits[i]) sum += pot2;
    switch(base) {
      case RTSimGlobals.BASE_BIN:
        String s = Long.toString(sum,2);
        while(s.length()<bits.length) s = "0" + s;
        return s;
      case RTSimGlobals.BASE_DEC:
        return Long.toString(sum);
      case RTSimGlobals.BASE_HEX:
        return Long.toString(sum,16).toUpperCase();
      case RTSimGlobals.BASE_DEC2:
        if(sum >= pot2max) sum = sum - 2*pot2max;
        return Long.toString(sum);
      case RTSimGlobals.BASE_HEX2:
        if(sum >= pot2max) sum = sum - 2*pot2max;
        return Long.toString(sum,16).toUpperCase();
      default:
        return "<WRONG BASE>";
    }
  }

  public static boolean[] string2boolArray(String s, int width, int base)
    throws NumberFormatException {
    long val = 0;
    switch(base) {
      case RTSimGlobals.BASE_BIN:
        val = Long.parseLong(s,2);
        break;
      case RTSimGlobals.BASE_DEC:
      case RTSimGlobals.BASE_DEC2:
        val = Long.parseLong(s,10);
        break;
      case RTSimGlobals.BASE_HEX:
      case RTSimGlobals.BASE_HEX2:
        val = Long.parseLong(s,16);
        break;
    }
    if(val < 0) val = (1 << width) + val;
    String tmp = Long.toString(val,2);
    int tmp_len = tmp.length();
    if(width == 0) width = tmp_len;
    boolean back[] = new boolean[width];
    for(int i=0;i<width;i++) {
      if(tmp_len-i-1 >= 0) back[i] = tmp.charAt(tmp_len-i-1)=='1';
      else back[i] = false;
    }
    return back;
  }

  public static boolean[] copiedBoolArray(boolean b[]) {
    boolean bk[] = new boolean[b.length];
    for(int i=0;i<b.length;i++) bk[i] = b[i];
    return bk;
  }

  public static void boolArrayInc(boolean b[]) {
    boolean c = true;
    for(int i=0;i<b.length;i++) {
      b[i] = b[i] ^ c;
      c = c && !b[i];
    }
  }

  public static void intInBoolArray(boolean t[], int n) {
    String s = "";
    int i = 0;
    while(n != 0 && i<t.length) {
      t[i] = n%2==1;
      n = n >> 1;
      i++;
    }
    for(;i<t.length;i++) t[i] = false;
  } 

  public static String baseInputErrorMsg(int base) {
    switch(base) {
      case RTSimGlobals.BASE_BIN:
        return IUI.get("ERROR_BASE_BIN");
      case RTSimGlobals.BASE_DEC:
      case RTSimGlobals.BASE_DEC2:
        return IUI.get("ERROR_BASE_DEC");
      case RTSimGlobals.BASE_HEX:
      case RTSimGlobals.BASE_HEX2:
        return IUI.get("ERROR_BASE_HEX");
      default: return "<WRONG BASE>";
    }
  }

  public static int ld(int x) {
    int l = 0;
    while(x != 0) {
      x = x>>1;
      l++;
    }
    return l;
  }

  public static String int2bitVectorString(int x, int width) {
    String bk = Integer.toBinaryString(x);
    while(bk.length() < width) bk = "0" + bk;
    return bk;
  }

  public static boolean isBinOp(int optype) {
    switch(optype) {
      case PLUS:
      case MINUS:
      case EQ:
      case NE:
      case GT:
      case GE:
      case LT:
      case LE:
      case OR:
      case NOR:
      case XOR:
      case NAND:
      case AND:
        return true;
      default:
        return false;
    }
  }

  public static boolean isCompOp(int optype) {
    switch(optype) {
      case EQ:
      case NE:
      case GT:
      case GE:
      case LT:
      case LE:
        return true;
      default:
        return false;
    }
  }

  /*
  public static String simTypeToString(int simtype) {
    switch(simtype) {
      case SIMTYPE_MEALY: return "Mealy abstrakt";
      case SIMTYPE_2EDGE: return "Moore 2 Flanken";
      default: return "<WRONG SIMTYPE>";
    }
  }*/
}


