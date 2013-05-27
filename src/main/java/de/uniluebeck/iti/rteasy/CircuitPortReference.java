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
import de.uniluebeck.iti.rteasy.kernel.BitRange;
import de.uniluebeck.iti.rteasy.kernel.Statement;


public class CircuitPortReference {
  public BitRange bitRange;
  public CircuitPort circuitPort;

  public CircuitPortReference(String toCircuitName,
                       String toCircuitPortName, int toId, BitRange br) {
    circuitPort = new CircuitPort(toCircuitName, toCircuitPortName, toId);
    bitRange = br;
  }

  public CircuitPortReference(String toCircuitName,
                       String toCircuitPortName, int toId, int begin, int end) {
    circuitPort = new CircuitPort(toCircuitName, toCircuitPortName, toId);
    bitRange = new BitRange(begin,end);
  }

  public CircuitPortReference(CircuitPort toCircuitPort, BitRange br) {
    circuitPort = toCircuitPort; bitRange = br;
  }

  public CircuitPortReference(CircuitPort toCircuitPort, int begin, int end) {
    circuitPort = toCircuitPort; bitRange = new BitRange(begin,end);
  }

  public String toVHDLPortName() {
    String back = circuitPort.circuitName;
    if(!circuitPort.portName.equals("")) back += "_"+circuitPort.portName;
    back += "_"+bitRange.begin+"_"+bitRange.end;
    return back;
  }

  public String toVHDLPortDecl(String inout, boolean ra, BitRange b) {
    return toVHDLPortName() + " : " + inout + " " + bitRange.toVHDLType(ra,b);
  }

  public String toVHDLPortRval() {
    return toVHDLPortName()+bitRange.toVHDL();
  }

  public String getVHDLPortMap() {
    return toVHDLPortName() + " => " + circuitPort.toSignalName()+bitRange.toVHDL();
  }

  public String getVHDLPortMap(String port) {
    return toVHDLPortName() + " => " + circuitPort.toSignalName(port)+bitRange.toVHDL();
  }

  public String getVHDLPortMap(SignalsData signalsData) {
    Statement st = signalsData.getStatementByControlSignal(circuitPort.circuitId);
    if(st.getStatementType() == RTSimGlobals.READ)
      return toVHDLPortName() + " => "+st.getMemory().getVHDLName()+"_data_out"+bitRange.toVHDL();
    else
      return toVHDLPortName() + " => " + circuitPort.toSignalName()+bitRange.toVHDL();
  }
}







