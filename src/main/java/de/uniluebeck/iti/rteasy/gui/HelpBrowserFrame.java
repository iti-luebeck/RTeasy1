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
