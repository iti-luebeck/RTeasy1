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

public class CircuitPort {
  public String circuitName, portName;
  public int circuitId;

  CircuitPort(String toName, String toPortName, int toId) {
    circuitName = toName;
    portName = toPortName;
    circuitId = toId;
  }

  public String toSignalName() {
    if(portName.equals("")) return circuitName;
    else return circuitName+"_"+portName;
  }

  public String toSignalName(String port) {
    if(port == null || port.equals("")) return toSignalName();
    else return circuitName+"_"+port;
  }

  public boolean equals(Object o) {
    CircuitPort cp;
    if(o instanceof CircuitPort) {
      cp = (CircuitPort) o;
      return circuitName.equals(cp.circuitName) && portName.equals(cp.portName) && circuitId == cp.circuitId;
    }
    else return false;
  }

  public int hashCode() {
    return toSignalName().hashCode();
  }
}
