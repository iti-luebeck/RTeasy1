package de.uniluebeck.iti.rteasy.gui;

import javax.swing.*;
import java.util.*;
import java.net.URL;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

class InfoTextFrame extends JInternalFrame {

  protected JButton closeButton;
  protected JEditorPane browserPane;
  protected URL currentURL;
  protected RTSimWindow parent;

  InfoTextFrame(RTSimWindow toParent, URL url, String title, int width, int height, String buttonCaption) {
    super(title,true,true,true,true);
    createFrame(width, height, buttonCaption);
    parent = toParent;
    openURL(url);
  }

  InfoTextFrame(RTSimWindow toParent, String content, String title, int width, int height, String buttonCaption) {
    super(title,true,true,true,true);
    createFrame(width, height, buttonCaption);
    parent = toParent;
    browserPane.setText(content);
  }

  protected void createFrame(int width, int height, String buttonCaption) {
    setSize(width,height);
    
    JPanel controlPanel = new JPanel();
    closeButton = new JButton(buttonCaption);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        InfoTextFrame.this.dispose();
      }
    });
    controlPanel.add(closeButton); 

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

    getContentPane().add(controlPanel,BorderLayout.SOUTH);
    getContentPane().add(new JScrollPane(browserPane),BorderLayout.CENTER);
  }

  protected void openURL(URL url) {
    try {
      browserPane.setPage(url);
      currentURL = url;
    }
    catch(Throwable t) {
      JOptionPane.showInternalMessageDialog(this,"Kann URL "+
        url.toExternalForm()+" nicht \u00F6ffnen! Fehler: "+
        t.getLocalizedMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
    }
  }

} 
