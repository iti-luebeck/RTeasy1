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

import de.uniluebeck.iti.rteasy.kernel.ExtensionFilter;

import java.awt.*;
import java.io.*;

public class VHDLFrame extends JInternalFrame {

  protected JButton closeButton, copyButton, saveButton;
  protected JEditorPane browserPane;
  protected URL currentURL;
  protected RTSimWindow parent;
  protected String fileName;
  private ExtensionFilter fileFilter;

  public VHDLFrame(RTSimWindow toParent, String content, int width, int height,
		String componentName) {
    super("VHDL: "+componentName,true,true,true,true);
    createFrame(width, height);
    parent = toParent;
    browserPane.setText(content);
    fileFilter = new ExtensionFilter(new String[]{"vhd","vhdl"},
				     IUI.get("FILEDESC_VHD"));
  }

  protected void createFrame(int width, int height) {
    setSize(width,height);
    
    JPanel controlPanel = new JPanel();
    copyButton = new JButton(IUI.get("BUTTON_COPYALL"));
    copyButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ae) {
	  VHDLFrame.this.copyAll();
	}
      });
    saveButton = new JButton(IUI.get("BUTTON_SAVE"));
    saveButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ae) {
	  VHDLFrame.this.saveAs();
	}
      });
    closeButton = new JButton(IUI.get("BUTTON_CLOSE"));
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        VHDLFrame.this.dispose();
      }
    });
    controlPanel.add(copyButton);
    controlPanel.add(saveButton);
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
      JOptionPane.showInternalMessageDialog(this,
        IUI.get("DIALOG_URL_FETCH_ERROR").replaceAll("%%URL",url.toExternalForm())
        +": "+t.getLocalizedMessage(),IUI.get("TITLE_ERROR"),JOptionPane.ERROR_MESSAGE);
    }
  }

  protected void saveAs() {
    JFileChooser chooser = parent.getFileChooser();
    chooser.setFileFilter(fileFilter);
    int result = chooser.showSaveDialog(this);
    if(result == JFileChooser.CANCEL_OPTION) return;
    try {
      File f = chooser.getSelectedFile();
      if(!fileFilter.hasExtension(f))
	f = new File(f.getPath()+".vhd");
      /*      if(!(f.isFile() && f.canWrite())) {
	JOptionPane.showInternalMessageDialog(this,
          IUI.get("DIALOG_ERROR_WRITE_FILE").replaceAll("%%FILENAME",f.getAbsolutePath())+"!",
	  IUI.get("TITLE_ERROR"), JOptionPane.ERROR_MESSAGE);
        return;
	}*/
      String lineSep = System.getProperty("line.separator");
      String s = browserPane.getText();
      s.replaceAll("[^.]",lineSep);
      FileWriter fw = new FileWriter(f);
      fw.write(s);
      fw.close();
    }
    catch (Throwable t) {
      JOptionPane.showInternalMessageDialog(this, t.getLocalizedMessage(),
					    IUI.get("TITLE_ERROR"),
					    JOptionPane.ERROR_MESSAGE);
    }
  }

  protected void copyAll() {
    browserPane.selectAll();
    browserPane.copy();
    browserPane.select(0,0);
  }
} 
