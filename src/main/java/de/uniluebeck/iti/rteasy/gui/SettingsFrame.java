package de.uniluebeck.iti.rteasy.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class SettingsFrame extends JInternalFrame {
  JComboBox langBox, plafBox;
  JCheckBox noisyWarningsChecker;
  JButton applyButton, okButton, cancelButton, discardButton;
    JLabel plafLabel;
  RTSimWindow mainWindow;
  Locale availableLocales[];
  UIManager.LookAndFeelInfo availablePlafs[];
  String localeDescs[], plafDescs[];
    boolean inited = false;

  SettingsFrame(RTSimWindow mainWindow) {
    super(IUI.get("TITLE_SETTINGS"),false,false,false,false);
    this.mainWindow = mainWindow;
    
    int i;
    availableLocales = IUI.getAvailableLocales();
    localeDescs = new String[availableLocales.length];
    for(i=0;i<localeDescs.length;i++) localeDescs[i] = 
      availableLocales[i].getDisplayLanguage();
    availablePlafs = UIManager.getInstalledLookAndFeels();
    plafDescs = new String[availablePlafs.length];
    for(i=0;i<plafDescs.length;i++) plafDescs[i] =
      availablePlafs[i].getName();

    Container box = Box.createVerticalBox();
    box.add(Box.createVerticalGlue());
    JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    langPanel.add(new JLabel("Language / Sprache :"));
    langBox = new JComboBox(localeDescs);
    langPanel.add(langBox);
    box.add(langPanel);
    JPanel plafPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    plafLabel = new JLabel(IUI.get("PLAF")+" :");
    plafPanel.add(plafLabel);
    plafBox = new JComboBox(plafDescs);
    plafPanel.add(plafBox);
    box.add(Box.createVerticalGlue());
    box.add(plafPanel);
    box.add(Box.createVerticalGlue());
    JPanel checkerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    noisyWarningsChecker = new JCheckBox(IUI.get("LABEL_NOISY_WARNINGS"));
    checkerPanel.add(noisyWarningsChecker);
    box.add(checkerPanel);
    getContentPane().add(box,BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    applyButton = new JButton(IUI.get("BUTTON_APPLY"));
    applyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        SettingsFrame.this.apply();
        hide();
        SettingsFrame.this.show();
      }
    });
    buttonPanel.add(applyButton);
    okButton = new JButton(IUI.get("BUTTON_OK"));
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        SettingsFrame.this.apply(); 
        SettingsFrame.this.dispose();
      }
    });
    buttonPanel.add(okButton);
    discardButton = new JButton(IUI.get("BUTTON_DISCARD"));
    discardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        SettingsFrame.this.init();
      }
    });
    buttonPanel.add(discardButton);
    cancelButton = new JButton(IUI.get("BUTTON_CANCEL"));
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        SettingsFrame.this.dispose();
      }
    });
    buttonPanel.add(cancelButton);
    getContentPane().add(buttonPanel,BorderLayout.SOUTH);
    inited = true;
  }

    public void updateCaptions() {
      if(inited) {
	setTitle(IUI.get("TITLE_SETTINGS"));
	applyButton.setText(IUI.get("BUTTON_APPLY"));
	okButton.setText(IUI.get("BUTTON_OK"));
	discardButton.setText(IUI.get("BUTTON_DISCARD"));
	cancelButton.setText(IUI.get("BUTTON_CANCEL"));
	plafLabel.setText(IUI.get("PLAF"));
	noisyWarningsChecker.setText(IUI.get("LABEL_NOISY_WARNINGS"));
        if(isShowing()) show();
      }
    }

  public void show() {
    if(inited) {
      init();
      pack();
    }
    super.show();
  }

  public void init() {
    int i;
    langBox.setSelectedIndex(IUI.getLocaleIndex());
    for(i=0;i<availablePlafs.length;i++) {
	// Java sucks!
      if(UIManager.getLookAndFeel().getClass().getName().equals(
        availablePlafs[i].getClassName())) {
	  plafBox.setSelectedIndex(i);
	  break;
      } }
    noisyWarningsChecker.setSelected(RTOptions.noisyWarnings);
  }

  public void apply() {
    IUI.setLocale(availableLocales[langBox.getSelectedIndex()]);
    Locale.setDefault(IUI.getLocale());
    try {
      UIManager.setLookAndFeel(availablePlafs[plafBox.getSelectedIndex()].
			       getClassName());
    }
    catch(Throwable t) {
      System.err.println("INTERNAL ERROR -- during Look&Feel setting: "+t.getMessage());
    }
    RTOptions.noisyWarnings = noisyWarningsChecker.isSelected();
    SwingUtilities.updateComponentTreeUI(mainWindow);
    mainWindow.updateCaptions();
    RTOptions.saveOptions();
  }

}