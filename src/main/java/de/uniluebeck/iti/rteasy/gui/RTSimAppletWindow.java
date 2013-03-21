package de.uniluebeck.iti.rteasy.gui;

import javax.swing.*;
import java.net.URL;

public class RTSimAppletWindow extends RTSimWindow {
  private JApplet applet;

  RTSimAppletWindow(JApplet a) {
    super();
    applet = a;
    applet.getAppletContext().showStatus("RTeasy l&auml;ft");
  }

  public void exit(int exit_code) {
    applet.getAppletContext().showStatus("RTeasy beendet");
    dispose();
  }
}
