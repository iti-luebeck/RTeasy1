package de.uniluebeck.iti.rteasy.gui;
import javax.swing.*;

public class RTSimApplet extends JApplet {

  RTSimAppletWindow m;

  public void start() {
    m = new RTSimAppletWindow(this);
    m.setVisible(true);
  }
}
