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

import java.util.*;

public class IUI {

  private static ResourceBundle bundle = null;
  private static String bundleErrorMsg;
    private final static Locale availableLocales[] = {
	new Locale("en","US"), new Locale("de","DE")};

  public static void init() {
    init(Locale.getDefault());
  }

  public static void init(Locale l) {
    try{
      bundle = ResourceBundle.getBundle("langpack",l);
    }
    catch(Throwable t) {
      System.err.println(t.getMessage());
      bundleErrorMsg = t.getMessage();
      bundle = null;
    }
    RTOptions.locale = getLocale();
   }
 
  public static String get(String id) {
    String back;
    if(bundle == null) {
      System.err.println(bundleErrorMsg);
      return ("resource bundle langpack unaccessible");
    }
    try {
      back = bundle.getString(id);
    }
    catch(MissingResourceException mse) {
      back = "no message for message key \""+id+"\"";
    }
    return back;
  }

  public static Locale getLocale() {
    if(bundle == null) {
      //System.err.println("INTERNAL ERROR -- IUI.bundle == null");
      return availableLocales[0];
    }
    else return get("LOCALE").equals("de")?availableLocales[1]:availableLocales[0];
  }

  public static void setLocale(Locale l) {
    init(l);
  }

  public static Locale[] getAvailableLocales() {
    return availableLocales;
  }

  public static int getLocaleIndex() {
    Locale l = getLocale();
    for(int i=0;i<availableLocales.length;i++)
      if(l.equals(availableLocales[i])) return i;
    System.err.println("INTERNAL ERROR -- getLocaleIndex() => return 0");
    return 0;
  }

    /* Deutsche Umlaute und Sonderzeichen:
     * Ae  - \u00C4
     * ae  - \u00E4
     * Oe  - \u00D6
     * oe  - \u00F6
     * Ue  - \u00DC
     * ue  - \u00FC
     * sz  - \u00DF
     * +-  - \u00B1
     * (c) - \u00A9
     * (R) - \u00AE
     * neg - \u00AC
     * ,,  - \u201E
     * ��  - \u201C
     */
}
