package de.uniluebeck.iti.rteasy.gui;
import java.io.PrintStream;

public class LogErrorStream extends PrintStream {
  private String lineBuffer = "";
  LogErrorStream() { super(System.err); }

  public void println(String x) {
    RTLog.log(lineBuffer+x);
    lineBuffer = "";
  }

  public void println(Object o) {
    println(o.toString());
  }

  public void print(String x) {
    lineBuffer += x;
  }

  public void print(Object o) {
    print(o.toString());
  }

  public void println() {
    RTLog.log(lineBuffer);
    lineBuffer = "";
  }
}
