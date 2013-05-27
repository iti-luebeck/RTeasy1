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
