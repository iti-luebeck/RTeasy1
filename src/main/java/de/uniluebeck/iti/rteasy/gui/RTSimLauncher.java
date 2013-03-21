package de.uniluebeck.iti.rteasy.gui;

//import java.net.URL;

public class RTSimLauncher extends RTSimWindow {

	private static final long serialVersionUID = 1L;

public void exit(int exit_code) {
    System.exit(exit_code);
  }

  public static void main(String args[]) {
    RTSimLauncher m = new RTSimLauncher();
    m.setVisible(true);
  }

}
