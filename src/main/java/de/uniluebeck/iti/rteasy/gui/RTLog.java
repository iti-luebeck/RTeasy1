package de.uniluebeck.iti.rteasy.gui;
import java.io.PrintStream;

public class RTLog {
  private static RTSimWindow window;
  private static PrintStream errorStream;

  public static void setWindow(RTSimWindow w) {
    window = w;
  }
  public static void log(String msg) {
    window.maintenance(msg);
  }
}
