package de.uniluebeck.iti.rteasy.gui;

import javax.swing.*;
import java.util.*;
import java.net.URL;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

class HelpBrowserFrame extends JInternalFrame {

  protected JButton contentButton, backButton, forwardButton;
  protected JEditorPane browserPane;
  protected Stack historyBack, historyForward;
  protected URL currentURL;
  protected String INDEX_URL = "/help/index.html."+IUI.get("LOCALE");
  protected RTSimWindow parent;

  HelpBrowserFrame(RTSimWindow toParent) {
    super(IUI.get("TITLE_HELP"),true,true,true,true);
    historyBack = new Stack();
    historyForward = new Stack();
    currentURL = null;
    createFrame();
    parent = toParent;
  }

  protected void createFrame() {
    setSize(600,480);
    
    JPanel controlPanel = new JPanel();
    contentButton = new JButton(IUI.get("BUTTON_INDEX"));
    contentButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        HelpBrowserFrame.this.openIndexURL();
      }
    });
    backButton = new JButton("<< "+IUI.get("BUTTON_BACK"));
    backButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        HelpBrowserFrame.this.historyBack();
      }
    });
    forwardButton = new JButton(IUI.get("BUTTON_FORWARD")+" >>");
    forwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        HelpBrowserFrame.this.historyForward();
      }
    });
    controlPanel.add(contentButton); controlPanel.add(backButton);
    controlPanel.add(forwardButton); 

    browserPane = new JEditorPane();
    browserPane.setEditable(false);
   
    browserPane.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent he) {
        HyperlinkEvent.EventType type = he.getEventType();
        if(type == HyperlinkEvent.EventType.ENTERED)
            browserPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else if(type == HyperlinkEvent.EventType.EXITED)
            browserPane.setCursor(Cursor.getDefaultCursor()); 
        else if(type == HyperlinkEvent.EventType.ACTIVATED)
            openURL(he.getURL());
      }
    });

    getContentPane().add(controlPanel,BorderLayout.NORTH);
    getContentPane().add(new JScrollPane(browserPane),BorderLayout.CENTER);
  }

  protected void openURL(URL url) {
    try {
      browserPane.setPage(url);
      if(currentURL != null) historyBack.push(currentURL);
      currentURL = url;
    }
    catch(Throwable t) {
      JOptionPane.showInternalMessageDialog(this,
        IUI.get("DIALOG_URL_FETCH_ERROR").replaceAll("%%URL",url.toExternalForm())+": "+
        t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
    }
  }

  protected void historyBack() {
    if(historyBack.empty()) return;
    URL url = (URL) historyBack.pop();
    try {
      browserPane.setPage(url);
      historyForward.push(currentURL);
      currentURL = url;
    }
    catch(Throwable t) {
      historyBack.push(url);
      JOptionPane.showInternalMessageDialog(this,
        IUI.get("DIALOG_URL_FETCH_ERROR").replaceAll("%%URL",url.toExternalForm())+": "+
        t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
    }
  }

  protected void historyForward() {
    if(historyForward.empty()) return;
    URL url = (URL) historyForward.pop();
    try {
      browserPane.setPage(url);
      historyBack.push(currentURL);
      currentURL = url;
    }
    catch(Throwable t) {
      historyForward.push(url);
      JOptionPane.showInternalMessageDialog(this,
        IUI.get("DIALOG_URL_FETCH_ERROR").replaceAll("%%URL",url.toExternalForm())+": "+
        t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
    }
  }

  public void openIndexURL() {
    String urlString = "<empty>";
    try {
      URL url = parent.getClass().getResource(INDEX_URL);
      urlString = url.toExternalForm();
      openURL(url);
    }
    catch(Throwable t) {
      JOptionPane.showInternalMessageDialog(this,IUI.get("DIALOG_URL_SYNTAX_ERROR").replaceAll("%%URL",INDEX_URL)+": "+t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
    }
  } 

  public void updateCaptions() {
	setTitle(IUI.get("TITLE_HELP"));
    forwardButton.setText(IUI.get("BUTTON_FORWARD"));
    backButton.setText(IUI.get("BUTTON_BACK"));
    contentButton.setText(IUI.get("BUTTON_INDEX"));
    INDEX_URL = "/help/index.html."+IUI.get("LOCALE");
    //die alte URL
    URL old = browserPane.getPage();
    //die neue URL, bzw. der Pfad, wo sie hin soll
    URL nurl = parent.getClass().getResource("/help/");
    //um sie zu generieren brauchen wir die aktuelle Datei:
    String help = old.toString().replace((nurl.toString()),"");
    //und konkatenieren den Hauptpfad mit der Sprachkonstanten
    //und der aktuellen Datei
    String tmp = nurl.toString().concat(IUI.get("LOCALE").
    		concat(help.substring(2,help.length())));
    try {
    	openURL(new URL(tmp)); 
    } catch(Throwable t) {}
    if(isShowing()) show();
  }
   
} 
