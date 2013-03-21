<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
<?xml version="1.0" encoding="ISO-8859-1"?>
      <?xml-stylesheet href="../../../../../docbook/docbook-xsl-1.69.1/html/docbook.xsl" type="text/xsl"?>
      <!DOCTYPE xsl:stylesheet  [
	<!ENTITY nbsp   "&#160;">
	<!ENTITY copy   "&#169;">
	<!ENTITY reg    "&#174;">
	<!ENTITY trade  "&#8482;">
	<!ENTITY mdash  "&#8212;">
	<!ENTITY ldquo  "&#8220;">
	<!ENTITY rdquo  "&#8221;"> 
	<!ENTITY pound  "&#163;">
	<!ENTITY yen    "&#165;">
	<!ENTITY euro   "&#8364;">
]>
      <article>
        <articleinfo>
          <!-- Use "HOWTO", "mini HOWTO", "FAQ" in title, if appropriate -->
          <title>
            RTeasy Hilfe          </title>
          <author>
            <firstname>
              Torben            </firstname>
            <surname>
              Schneider            </surname>
            <affiliation>
              <!-- Valid email...spamblock/scramble if so desired -->
              <address>
                <email>
                  schneidt@informatik.uni-luebeck.de                </email>
              </address>
            </affiliation>
          </author>
          <!-- All dates specified in ISO "YYYY-MM-DD" format -->
          <pubdate>
            2005-11-09          </pubdate>
          <!-- Most recent revision goes at the top; list in descending order -->
          <revhistory>
            <revision>
              <revnumber>
                0.1              </revnumber>
              <date>
                2005-11-09              </date>
              <authorinitials>
                TS              </authorinitials>
              <revremark>
                Docbook-Version der RTeasy Hilfe              </revremark>
            </revision>
          </revhistory>
          <!-- Provide a good abstract; a couple of sentences is sufficient -->
          <abstract>
            <para>
              Dieses Dokument soll die Funktionen von RTeasy und ihre Bedienung erl
              &auml;
              utern.            </para>
          </abstract>
        </articleinfo>
        <!-- Content follows...include introduction, license information, feedback -->
        <sect1 id="intro">
          <title>
            Einf
            &uuml;
            hrung          </title>
          <para>
            RTeasy ist eine Entwicklungsumgebung f
            &uuml;
            r die Registertransfersprache. Mit RTeasy ist es m
            &ouml;
            glich Registertransferprogramme zu entwerfen und zu simulieren. Au
            &szlig;
            erdem kann RTeasy RT-Programme in VHDL
            &uuml;
            bersetzen.
          </para>
          <!-- Give credit where credit is due...very important -->
          <sect2 id="credits">
            <title>
              Credits / Contributors            </title>
            <para>
              In this document, I have the pleasure of acknowledging:
            </para>
            <!-- Please scramble addresses; help prevent spam/email harvesting -->
            <itemizedlist>
              <listitem>
                <para>
                  Individual 1
                  <email>
                    someone1 (at) somewhere.org                  </email>
                </para>
              </listitem>
              <listitem>
                <para>
                  Individual 2
                  <email>
                    someone2 (at) somewhere.org                  </email>
                </para>
              </listitem>
            </itemizedlist>
          </sect2>
        </sect1>
        <!-- 

     The rest of the document follows. This is where your
     subject-specific content goes. A logical reading
     progression should be present - installation, setup,
     configuration, using, advanced topics, etc.

-->
        <!-- Other Sections of Interest... -->
        <sect1 id="rteasy_start">
          <title>
            RTeasy starten          </title>
          <para>
            Um RTeasy benutzen zu k
            &ouml;
            nnen ben
            &ouml;
            tigen sie eine Java Virtual Machine(JVM). Bei Problemen
            &uuml;
            berpr
            &uuml;
            fen Sie bitte die Systemvoraussetzungen auf
            <ulink url="http://www.iti.uni-luebeck.de/~albrecht/rteasy/">
              http://www.iti.uni-luebeck.de/~albrecht/rteasy/            </ulink>
            . Sie starten RTeasy
            &uuml;
            ber die Shell mit dem Befehl
            <command>
              java -jar rteasy_version.jar            </command>
          </para>
          <para>
            Es
            &ouml;
            ffnet sich das Hauptfenster (
            <xref linkend="mainwindow"/>
            ) . Sie k
            &ouml;
            nnen nun RTeasy die Einstellungen von RTeasy anpassen (
            <xref linkend="settings"/>
            ), ein neues Programm erstellen oder ein vorhandenes Programm
            &ouml;
            ffnen (
            <xref linkend="filemenu"/>
            )          </para>
        </sect1>
        <sect1 id="mainwindow">
          <title>
            Hauptfenster          </title>
          <screenshot>
            <mediaobject>
              <imageobject>
                <imagedata fileref="pics/hauptfenster.jpg" format="JPG"/>
              </imageobject>
            </mediaobject>
          </screenshot>
          <para>
            Das Hauptfenster ist der
            &quot;
            Desktop
            &quot; 
            f
            &uuml;
            r die anderen RTeasyfenster. Alle  
            Fenster k
            &ouml;
            nnen innerhalb dieser Arbeitsfl
            &auml;
            che beliebig platziert werden. 
            In der Men
            &uuml;
            leiste befinden sich die Men
            &uuml;
            punkte
            <simplelist type="horiz" columns="1">
              <member>
                <guimenu>
                  Datei                </guimenu>
                (
                <xref linkend="filemenu"/>
                )              </member>
              <member>
                <guimenu>
                  Bearbeiten                </guimenu>
                (
                <xref linkend="editmenu"/>
                )              </member>
              <member>
                <guimenu>
                  Simulator                </guimenu>
                (
                <xref linkend="simmenu"/>
                )              </member>
              <member>
                <guimenu>
                  Entwurf                </guimenu>
                (
                <xref linkend="designmenu"/>
                )              </member>
              <member>
                <guimenu>
                  Hilfe                </guimenu>
                (
                <xref linkend="helpmenu"/>
                )              </member>
            </simplelist>
            Je nachdem ob sie sich im Editier- (
            <xref linkend="editmode"/>
            ) oder Simulationsmodus(
            <xref linkend="simmode"/>
            ) befinden, sind unterschiedliche Punkte in den Men
            &uuml;
            s aktiv.
          </para>
          <para>
            Darunter befindet sich die Leiste f
            &uuml;
            r die Simulationskontrolle (
            <xref linkend="simcontrol"/>
            ).
            Im Editiermodus sind alle Schaltfl
            &auml;
            chen inaktiv bis auf die Schaltfl
            &auml;
            che
            <guibutton>
              Simulieren            </guibutton>
            .          </para>
          <sect2 id="modi">
            <title>
              Editor-/Simulatormodus            </title>
            <para>
              RTeasy kann sich in zwei unterschiedlichen Zust
              &auml;
              nden befinden. Im
              <emphasis>
                Editiermodus              </emphasis>
              (
              <xref linkend="editmode"/>
              ) k
              &ouml;
              nnen Sie ein RTeasy-Programm bearbeiten und ver
              &auml;
              ndern. Ausserdem k
              &ouml;
              nnen Sie sich in diesem Modus die
              &Uuml;
              bersetzung in VHDL anzeigen lassen. Im
              <emphasis>
                Simulationsmodus              </emphasis>
              (
              <xref linkend="simmode"/>
              ) k
              &ouml;
              nnen Sie das Programm ausf
              &uuml;
              hren und sich die Register-, Bus- und Speicherinhalte anzeigen lassen. In diesem Modus k
              &ouml;
              nnen sie das Programm nicht ver
              &auml;
              ndern.
            </para>
            <sect3 id="editmode">
              <title>
                Editiermodus              </title>
              <para>
                Sie befinden sich standardm
                &auml;
                ssig in diesem Modus, wenn eine Datei geladen wird oder ein neues Programm erzeugt wird. Befindet sich RTeasy im Simultationsmodus, k
                &ouml;
                nnen Sie die Schaltfl
                &auml;
                che
                <guibutton>
                  Bearbeiten                </guibutton>
                benutzen, um in den Editiermodus zu gelangen.
              </para>
              <screenshot>
                <mediaobject>
                  <imageobject>
                    <imagedata fileref="pics/BearbeitenBtn.jpg" format="JPG"/>
                  </imageobject>
                </mediaobject>
              </screenshot>
              <sect4 id="filemenu">
                <title>
                  Dateimen
&uuml;                </title>
                <para>
                  <screenshot>
                    <mediaobject>
                      <imageobject>
                        <imagedata fileref="pics/dateimenue.jpg" format="JPG"/>
                      </imageobject>
                    </mediaobject>
                  </screenshot>
                </para>
                <para>
                  <note>
                    <para>
                      Diese Men
                      &uuml;
                      punkte beenden den Simulationsmodus und wechseln in den Editiermodus.
                    </para>
                  </note>                </para>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Neu                    </guimenuitem>
                  </title>
                  <para>
                    Schliesst das aktuelle RTeasy-Programm und
                    &ouml;
                    ffnet ein neues, leeres RT-Programm im  Editorfenster (
                    <xref linkend="editor"/>
                    ).
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      &Ouml;
                      ffnen
                      <accel>
                        (F3)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    L
                    &auml;
                    d ein Rteasy-Programm aus einer Datei in den Editor (
                    <xref linkend="editor"/>
                    ).
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Speichern
                      <accel>
                        (F2)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Speichert das Programm an den Ort, der in der Kopfzeile des Editorfensters angezeigt wird. Sollte die Datei noch nie abbespeichert worden sein,
                    &ouml;
                    ffnet sich ein Dialog, in dem Sie ausw
                    &auml;
                    hlen k
                    &ouml;
                    nnen an welchen Ort die Datei gespeichert werden soll.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Speichern als
                      <accel>
                        (STRG+F2)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Speichert das Programm an den im Dialog ausgew
                    &auml;
                    hlten Ort.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Beenden                    </guimenuitem>
                  </title>
                  <para>
                    Beendet RTeasy.
                  </para>
                </formalpara>
              </sect4>
              <sect4 id="editmenu">
                <title>
                  Bearbeiten-Men
&uuml;                </title>
                <para>
                  <screenshot>
                    <mediaobject>
                      <imageobject>
                        <imagedata fileref="pics/BearbeitenMenu.jpg" format="JPG"/>
                      </imageobject>
                    </mediaobject>
                  </screenshot>
                  <note>
                    <para>
                      Diese Men
                      &uuml;
                      punkte sind nur im Editiermodus aktiv.
                    </para>
                  </note>                </para>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Zur
                      &uuml;
                      ck
                      <accel>
                        (STRG+Z)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Macht die letzte(n)
                    &Auml;
                    nderung(en) im Editor r
                    &uuml;
                    ckg
                    &auml;
                    ngig sofern dies m
                    &ouml;
                    glich ist.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Wiederholen
                      <accel>
                        (STRG+Y)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Wiederholt die letzte(n) zur
                    &uuml;
                    ckgenommene(n)
                    &Auml;
                    nderung(en) im Editor.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Kopieren
                      <accel>
                        (STRG+C)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Kopiert den im Editor markierten Text in die Zwischenablage.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Ausschneiden
                      <accel>
                        (STRG+X)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    Entfernt den im Editor markierten Text und kopiert ihn in die Zwischenablage.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Einf
                      &uuml;
                      gen
                      <accel>
                        (STRG+V)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    F
                    &uuml;
                    gt den Inhalt der Zwischenablage an die aktuelle Cursorposition im Editorfenster ein.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Pretty Print                    </guimenuitem>
                  </title>
                  <para>
                    Dieser Men
                    &uuml;
                    punkt versucht das Programm zu kompilieren. Ist dies erfolgreich, dann wird
                    das Programm in eine einheitliche und
                    &uuml;
                    bersichliche Struktur gebracht. Warnungen werden
                    soweit wie m
                    &ouml;
                    glich behoben. Die auftretenden Fehler und Warnungen erscheinen im Logfenster (
                    <xref linkend="logwindow"/>
                    ).
                  </para>
                </formalpara>
                <formalpara id="settings">
                  <title>
                    <guimenuitem>
                      Einstellungen                    </guimenuitem>
                  </title>
                  <para>
                    Hier k
                    &ouml;
                    nnen sie das Verhalten und Aussehen von RTeasy anpassen.
                    <simplelist columns="1">
                      <member>
                        <guilabel>
                          Language / Sprache:                        </guilabel>
                        - Hier k
                        &ouml;
                        nnen Sie die Sprache f
                        &uuml;
                        r Schaltfl
                        &auml;
                        chenbeschriftungen, Fehlermeldungen usw. festlegen                      </member>
                      <member>
                        <guilabel>
                          Plug
                          &amp; 
                          Feel                        </guilabel>
                        - Legt das Aussehen der RTeasyfenster fest.                      </member>
                      <member>
                        <guilabel>
                          Ausf
                          &uuml;
                          hrliche Warnungen                        </guilabel>
                      </member>
                    </simplelist>
                    <note>
                      <para>
                        Dieser Men
                        &uuml;
                        punkt ist immer aktiv.                      </para>
                    </note>
                  </para>
                </formalpara>
              </sect4>
            </sect3>
            <sect3 id="simmode">
              <title>
                Simulationsmodus              </title>
              <para>
                Wenn sie sich im Simulationsmodus befinden, dann k
                &ouml;
                nnen sie das eingegebene RT-Programm ausf
                &uuml;
                hren und Informationen wie Inhalte von Registern und Bussen etc. anzeigen und bearbeiten. Wenn sie sich im Editiermodus befinden, dann k
                &ouml;
                nnen sie mittels
                <guibutton>
                  Simulieren                </guibutton>
                in den Simulationsmodus wechseln. (
                <xref linkend="modi"/>
                )
              </para>
              <para>
                <screenshot>
                  <mediaobject>
                    <imageobject>
                      <imagedata fileref="pics/SimulierenBtn.jpg" format="JPG"/>
                    </imageobject>
                  </mediaobject>
                </screenshot>
              </para>
              <sect4 id="simcontrol">
                <title>
                  Simulatorsteuerung                </title>
                <para>
                  <screenshot>
                    <mediaobject>
                      <imageobject>
                        <imagedata fileref="pics/simkontrolle.jpg" format="JPG"/>
                      </imageobject>
                    </mediaobject>
                  </screenshot>
                </para>
                <note>
                  <para>
                    Nur im Simulationsmodus sind die Schaltfl
                    &auml;
                    chen f
                    &uuml;
                    r die Simulationssteuerung aktiv.
                  </para>
                </note>
                <formalpara>
                  <title>
                    <guibutton>
                      Bearbeiten                    </guibutton>
                  </title>
                  <para>
                    wechselt in den Editiermodus (
                    <xref linkend="editmode"/>
                    )
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guibutton>
                      Reset                    </guibutton>
                  </title>
                  <para>
                    setzt Programmz
                    &auml;
                    hler und Inhalte von Registern und Bussen wieder auf die Initialwerte
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guibutton>
                      Step                    </guibutton>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt den n
                    &auml;
                    chsten parallel Anweisungsblock aus
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guibutton>
                      MicroStep                    </guibutton>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt die n
                    &auml;
                    chste Anweisung aus
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guibutton>
                      Run                    </guibutton>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt das Programm aus bis es terminiert oder unterbrochen wird
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guibutton>
                      Breakpoints                    </guibutton>
                  </title>
                  <para>
                    &ouml;
                    ffnet das Breakpoint-Fenster
                    <xref linkend="breakpointwindow"/>
                    in dem Haltepunkte hinzugef
                    &uuml;
                    gt oder gel
                    &ouml;
                    scht werden k
                    &ouml;
                    nnen
                  </para>
                </formalpara>
              </sect4>
              <sect4 id="simmenu">
                <title>
                  Simulator-Men
&uuml;                </title>
                <para>
                  <screenshot>
                    <mediaobject>
                      <imageobject>
                        <imagedata fileref="pics/SimulatorMenu.jpg" format="JPG"/>
                      </imageobject>
                    </mediaobject>
                  </screenshot>
                </para>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Reset
                      <accel>
                        (STRG+R)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    setzt Programmz
                    &auml;
                    hler und Inhalte von Registern und Bussen wieder auf die Initialwerte
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Step
                      <accel>
                        (STRG+S)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt den n
                    &auml;
                    chsten parallel Anweisungsblock aus
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      MicroStep
                      <accel>
                        (STRG+M)                      </accel>
                    </guimenuitem>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt die n
                    &auml;
                    chste Anweisung aus
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Run                    </guimenuitem>
                  </title>
                  <para>
                    f
                    &uuml;
                    hrt das Programm aus bis es terminiert oder unterbrochen wird
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Breakpoints                    </guimenuitem>
                  </title>
                  <para>
                    &ouml;
                    ffnet das Breakpoint-Fenster
                    <xref linkend="breakpointwindow"/>
                    in dem Haltepunkte hinzugef
                    &uuml;
                    gt oder gel
                    &ouml;
                    scht werden k
                    &ouml;
                    nnen
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Alle Speicher r
                      &uuml;
                      cksetzen                    </guimenuitem>
                  </title>
                  <para>
                    L
                    &ouml;
                    scht den Inhalt aller deklarierten Speicher.
                  </para>
                </formalpara>
                <formalpara>
                  <title>
                    <guimenuitem>
                      Log l
                      &ouml;
                      schen                    </guimenuitem>
                  </title>
                  <para>
                    L
                    &ouml;
                    scht den Inhalt des Logfensters. (
                    <xref linkend="logwindow"/>
                    )
                  </para>
                </formalpara>
              </sect4>
            </sect3>
            <sect3 id="designmenu">
              <title>
                Entwurf              </title>
              <para>
                Noch nicht implementiert
              </para>
            </sect3>
            <sect3 id="helpmenu">
              <title>
                Hilfe-Men
&uuml;              </title>
              <formalpara>
                <title>
                  <guimenuitem>
                    Hilfe                  </guimenuitem>
                </title>
                <para>
                  Zeigt die Hilfeseiten von RTeasy an.
                </para>
              </formalpara>
              <formalpara>
                <title>
                  <guimenuitem>
                    &Uuml;
                    ber                  </guimenuitem>
                </title>
                <para>
                  Zeigt Informationen
                  &uuml;
                  ber das Programm.
                </para>
              </formalpara>
            </sect3>
          </sect2>
        </sect1>
        <sect1 id="editor">
          <title>
            Editorfenster          </title>
          <para>
            <screenshot>
              <mediaobject>
                <imageobject>
                  <imagedata fileref="pics/editor.jpg" format="JPG"/>
                </imageobject>
              </mediaobject>
            </screenshot>
          </para>
          <para>
            Das Editorfenster zeigt den aktuellen Quelltext des RT-Programms an. Im Editiermodus (
            <xref linkend="editmode"/>
            ) kann man den Quelltext ver
            &auml;
            ndern. Im Simulationsmodus (
            <xref linkend="simmode"/>
            ) zeigt das Fenster die zuletzt 
            ausgef
            &uuml;
            hrte Anweisung an. Im kann der Quelltext nicht ver
            &auml;
            ndert werden.
          </para>
          <sect2>
            <title>
              Im Editiermodus            </title>
            <para>
              Der Editor funktioniert wie jeder beliebige Texteditor unter Windows.
            </para>
            <formalpara>
              Bedeutung
              <title>
                Markieren, Cut, Copy und Paste              </title>
              <para>
                Speicher
                &uuml;
                &auml;
                &uuml;
                Dieses Fenster zeigt den Inhalt eines deklarierten Speichers an. Mittels Mausklick auf das Feld Inhalt an der entsprechenden Adressposition kann man den Wert an dieser Adresse ver
                &auml;
                ndern. Die Eingabewerte f
                &uuml;
                r die entsprechende Basis entspechen denen des Simulationsfensters.
                &uuml;
                Basis
                &uuml;
                Hier kann man die f
                &uuml;
                r Ein- und Ausgabe des Inhalts angeben. Die Adresswerte sind immer hexadezimal.
                (
                )
                &gt;
                Go to
                &uuml;
                Springt zu der eingebenen Adresse (hexadezimal).
                &uuml;
                Reset
                &gt;
                Setzt alle Werte dieses Speichers zur
                &uuml;
                ck auf die Initialwerte.
                &auml;
                &auml;
                Laden
                &uuml;
                &auml;
                L
                &auml;
                d den Speicherinhalt aus einer Datei.
                &uuml;
                Speichern
                Speichert den Speicherinhalt in eine Datei.
                F
                &auml;
                &uuml;
                r den korrekten Aufbau eines solchen Speicherabbilds schauen sie sich bitte 
                die Grammatik in der Dokumentation an.
                &ouml;
                Breakpoint-Fenster
                &uuml;
                &ouml;
                Das Breakpointfenster zeigt die Haltepunkte, an denen die Ausf
                &uuml;
                hrung des RT-Programms gestoppt wird.
                &uuml;
                Hinzuf
                &uuml;
                gen
                F
                &uuml;
                &auml;
                gt einen Breakpoint hinzu. Dazu klicken Sie auf
                Hinzuf
                &uuml;
                gen
                und dann auf den Anweisungsblock im Editor, an dem Sie den Haltepunkt setzen wollen. Die Zustandsnummer wird im Breakpoint-Fenster hinzugef
                &uuml;
                gt und der Zustand wird farblich markiert. (
                )
                
                &auml;
                L
                &uuml;
                &ouml;
                schen
                &uuml;
                L
                &ouml;
                scht den im Breakpoint-Fenster markierten Haltepunkt.
                &lt;
                Markierung setzen
                Ein Mausklick auf den Haltepunkt markiert den Anweisungsblock im Editor.
                
                &uuml;
                Markierung aufheben
                &uuml;
                Diese Schaltfl
                &uuml;
                &auml;
                che l
                &ouml;
                scht die farbliche Kennzeichnung im Editorfenster.
                &ouml;
                Das Logfenster
                &auml;
                In diesem Fenster erscheinen alle Fehler und Warnungen die w
                &auml;
                hrend des Kompilierens und Ausf
                &uuml;
                hrens auftreten. Die Schaltfl
                &auml;
                che
                L
                &ouml;
                schen
                l
                &ouml;
                scht alle bisherigen Ausgaben.
                &ouml;
                Hexadezimaldarstellung mit Vorzeichen
                &ouml;
                Eingaben, die die Bitbreite des Busses/Registers
                &uuml;
                bersteigen werden von links abgeschnitten.
                &uuml;
                Speicher
                &uuml;
                &auml;
                Zu jedem deklarierten Speicher wird eine Schaltfl
                &auml;
                che angezeigt, mit der man das Fenster f
                &uuml;
                r den 
                jeweiligen Speicher anzeigen lassen kann. (
                )
                &auml;
              </para>
            </formalpara>
            <table>
              <title>
                Tastenbelegung              </title>
              <tgroup>
                <thead>
                  </thead>
                <row>
                    <entry>
                      Taste(n)                    </entry>
                    <entry>
                      Bedeutung                    </entry>
                </row>
                <tr>
                <td></thead></td></tr>
                <tbody>
                </tbody>
                <row>
                    <entry>
                      Pfeiltasten                    </entry>
                    <entry>
                      Im Text in die gew
                      &uuml;
                      nschte Richtung bewegen                    </entry>
                </row>
                  <row>
                    <entry>
                      Strg+Pfeiltasten                    </entry>
                    <entry>
                      w
                      &ouml;
                      rterweise in gew
                      &uuml;
                      nschte Richtung bewegen                    </entry>
                  </row>
                  <row>
                    <entry>
                      &lt;
                      -- Backspace                    </entry>
                    <entry>
                      Zeichen vor Cursor l
                      &ouml;
                      schen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Entf                    </entry>
                    <entry>
                      Zeichen hinter Cursor l
                      &ouml;
                      schen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Pos 1                    </entry>
                    <entry>
                      an Zeilenanfang springen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Ende                    </entry>
                    <entry>
                      an Zeilenende springen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+Pos 1                    </entry>
                    <entry>
                      an Programmanfang springen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+Ende                    </entry>
                    <entry>
                      an Programmende springen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Bild hoch/Bild runter                    </entry>
                    <entry>
                      Seitenweise durch Programmtext scrollen                    </entry>
                  </row>
                  <row>
                    <entry>
                      Tabulator                    </entry>
                    <entry>
                      Spezialzeichen, bewirkt Einr
                      &uuml;
                      ckung um 8 Zeichen, z
                      &auml;
                      hlt aber nur als eines                    </entry>
                  </row>
                  <row>
                    <entry>
                      Shift gedr
                      &uuml;
                      ckt halten und Richtungstasten                    </entry>
                    <entry>
                      Markier-Modus                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+A                    </entry>
                    <entry>
                      Alles markieren.                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+C                    </entry>
                    <entry>
                      Markierten Text kopieren                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+X                    </entry>
                    <entry>
                      Markierten Text ausschneiden                    </entry>
                  </row>
                  <row>
                    <entry>
                      Strg+V                    </entry>
                    <entry>
                      aus Zwischenablage einf
                      &uuml;
                      gen                    </entry>
                  </row>
                <tr>
                <td></tbody></td></tr>
              </tgroup>
            </table>
            <para>
              Der Editor wurde mit der Java-Klasse JTextArea implementiert, weitere Tastenbelegungen finden sich
              <ulink>
                hier              </ulink>
              .            </para>
          </sect2>
          <sect2>
            <title>
              Im Simulationsmodus            </title>
            <para>
              Wird ein RT-Programm simuliert werden die zuletzt ausgef
              &uuml;
              hrten Anweisungen farblich markiert:
            </para>
            <table>
              <title>
                Farbmarkierungen              </title>
              <tgroup>
                <thead>
                  </thead>
                <row>
                    <entry>
                      Farbe                    </entry>
                    <entry>
                      Beschreibung                    </entry>
                </row>
                <tr>
                <td></thead></td></tr>
                <tbody>
                </tbody>
                <row>
                    <entry>
                      <emphasis>
                        Block                      </emphasis>
                    </entry>
                    <entry>
                      Zuletzt ausgef
                      &uuml;
                      hrter Anweisungsblock (Step)                    </entry>
                </row>
                  <row>
                    <entry>
                      <emphasis>
                        Anweisung                      </emphasis>
                    </entry>
                    <entry>
                      Zuletzt ausgef
                      &uuml;
                      hrte Anweisung (MicroStep)                    </entry>
                  </row>
                  <row>
                    <entry>
                      <emphasis>
                        Block                      </emphasis>
                    </entry>
                    <entry>
                      Anweisungsblock, bei dem die Ausf
                      &uuml;
                      hrung abgebrochen wurde (Run)                    </entry>
                  </row>
                  <row>
                    <entry>
                      <emphasis>
                        Block                      </emphasis>
                    </entry>
                    <entry>
                      Derzeit ausgew
                      &auml;
                      hlter Breakpoint                    </entry>
                  </row>
                <tr>
                <td></tbody></td></tr>
              </tgroup>
            </table>
          </sect2>
        </sect1>
        <sect1>
          <title>
            Simulationsstatus-Fenster          </title>
          <para>
            <screenshot>
              <mediaobject>
                <imageobject>
                  <imagedata>
                </imageobject>
              </mediaobject>
            </screenshot>
          </para>
          <para>
            Das Simulationsstatus-Fenster wird nur w
            &auml;
            hrend der Simulation (
            <xref>
            ) angezeigt.
            In diesem Fenster befinden sich alle Informationen zu Registern, Bussen, Speichern und dem 
            Zustands- und Programmz
            &auml;
            hler.
          </para>
          <formalpara>
            <title>
              Zustand- und Programmz
              &auml;
              hler            </title>
            <para>
              <screenshot>
                <mediaobject>
                  <imageobject>
                    <imagedata>
                  </imageobject>
                </mediaobject>
              </screenshot>
              Hier werden der zuletzt ausgef
              &uuml;
              hrte Zustand sowie die Anzahl der vergangenen Takte angezeigt.
            </para>
          </formalpara>
          <formalpara>
            <title>
              Register und Busse            </title>
            <para>
              <screenshot>
                <mediaobject>
                  <imageobject>
                    <imagedata>
                  </imageobject>
                </mediaobject>
              </screenshot>
              Hier werden die Inhalte von Registern und Bussen angezeigt.
              <emphasis>
                Diese Farbe              </emphasis>
              signalisiert dass der Wert eines Registers bzw. eines Busses im letzten Schritt ver
              &auml;
              ndert wurde. Mit einem Mausklick auf den Wert des Registers kann man diesen ver
              &auml;
              ndern. Der Wert wird erst
              &uuml;
              bernommen, wenn der der Wert mit der ENTER-Taste oder mit einem Klick ausserhalb des Feldes best
              &auml;
              tigt wird. Mit ESC wird der eingegebene Wert verworfen Die dritte Spalte zeigt an, mit welcher Basis die Werte angezeigt und gesetzt werden.
            </para>
          </formalpara>
          <table>
            <title>
              Basisdarstellung            </title>
            <tgroup>
              <thead>
                </thead>
              <row>
                  <entry>
                    Wert                  </entry>
                  <entry>
                    Basis                  </entry>
                  <entry>
                    Eingabe Zeichen                  </entry>
                  <entry>
                    Bedeutung                  </entry>
              </row>
              <tr>
              <td></thead></td></tr>
              <tbody>
              </tbody>
              <row>
                  <entry>
                    BIN                  </entry>
                  <entry>
                    2                  </entry>
                  <entry>
                    0, 1                  </entry>
                  <entry>
                    Bin
                    &auml;
                    rdarstellung                  </entry>
              </row>
                <row>
                  <entry>
                    DEC                  </entry>
                  <entry>
                    10                  </entry>
                  <entry>
                    0,...,9                  </entry>
                  <entry>
                    Dezimaldarstellung ohne Vorzeichen                  </entry>
                </row>
                <row>
                  <entry>
                    HEX                  </entry>
                  <entry>
                    16                  </entry>
                  <entry>
                    0,..,9,A,...,F                  </entry>
                  <entry>
                    Hexadezimaldarstellung ohne Vorzeichen                  </entry>
                </row>
                <row>
                  <entry>
                    DEC2                  </entry>
                  <entry>
                    10                  </entry>
                  <entry>
                    0,..,9                  </entry>
                  <entry>
                    Dezimaldarstellung mit Vorzeichen                  </entry>
                </row>
                <row>
                  <entry>
                    HEX2                  </entry>
                  <entry>
                    16                  </entry>
                  <entry>
                    0,...,9,A,...,F                  </entry>
                  <entry>
                    Hexadezimaldarstellung mit Vorzeichen                  </entry>
                </row>
              <tr>
              <td></tbody></td></tr>
            </tgroup>
          </table>
          <note>
            <para>
              Eingaben, die die Bitbreite des Busses/Registers
              &uuml;
              bersteigen werden von links abgeschnitten.            </para>
          </note>
          <formalpara>
            <title>
              Speicher            </title>
            <para>
              <screenshot>
                <mediaobject>
                  <imageobject>
                    <imagedata>
                  </imageobject>
                </mediaobject>
              </screenshot>
              Zu jedem deklarierten Speicher wird eine Schaltfl
              &auml;
              che angezeigt, mit der man das Fenster f
              &uuml;
              r den 
              jeweiligen Speicher anzeigen lassen kann. (
              <xref>
              )
            </para>
          </formalpara>
        </sect1>
        <sect1>
          <title>
            Speicher          </title>
          <para>
            <screenshot>
              <mediaobject>
                <imageobject>
                  <imagedata>
                </imageobject>
              </mediaobject>
            </screenshot>
          </para>
          <para>
            Dieses Fenster zeigt den Inhalt eines deklarierten Speichers an. Mittels Mausklick auf das Feld Inhalt an der entsprechenden Adressposition kann man den Wert an dieser Adresse ver
            &auml;
            ndern. Die Eingabewerte f
            &uuml;
            r die entsprechende Basis entspechen denen des Simulationsfensters.
          </para>
          <formalpara>
            <title>
              <guibutton>
                Basis              </guibutton>
            </title>
            <para>
              Hier kann man die f
              &uuml;
              r Ein- und Ausgabe des Inhalts angeben. Die Adresswerte sind immer hexadezimal.
              (
              <xref>
              )
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Go to              </guibutton>
            </title>
            <para>
              Springt zu der eingebenen Adresse (hexadezimal).
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Reset              </guibutton>
            </title>
            <para>
              Setzt alle Werte dieses Speichers zur
              &uuml;
              ck auf die Initialwerte.
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Laden              </guibutton>
            </title>
            <para>
              L
              &auml;
              d den Speicherinhalt aus einer Datei.
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Speichern              </guibutton>
            </title>
            <para>
              Speichert den Speicherinhalt in eine Datei.
              <note>
                <para>
                  F
                  &uuml;
                  r den korrekten Aufbau eines solchen Speicherabbilds schauen sie sich bitte 
                  die Grammatik in der Dokumentation an.                </para>
              </note>
            </para>
          </formalpara>
        </sect1>
        <sect1>
          <title>
            Breakpoint-Fenster          </title>
          <para>
            <screenshot>
              <mediaobject>
                <imageobject>
                  <imagedata>
                </imageobject>
              </mediaobject>
            </screenshot>
            Das Breakpointfenster zeigt die Haltepunkte, an denen die Ausf
            &uuml;
            hrung des RT-Programms gestoppt wird.
          </para>
          <formalpara>
            <title>
              <guibutton>
                Hinzuf
                &uuml;
                gen              </guibutton>
            </title>
            <para>
              F
              &uuml;
              gt einen Breakpoint hinzu. Dazu klicken Sie auf
              <guibutton>
                Hinzuf
                &uuml;
                gen              </guibutton>
              und dann auf den Anweisungsblock im Editor, an dem Sie den Haltepunkt setzen wollen. Die Zustandsnummer wird im Breakpoint-Fenster hinzugef
              &uuml;
              gt und der Zustand wird farblich markiert. (
              <xref>
              )
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                L
                &ouml;
                schen              </guibutton>
            </title>
            <para>
              L
              &ouml;
              scht den im Breakpoint-Fenster markierten Haltepunkt.
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Markierung setzen              </guibutton>
            </title>
            <para>
              Ein Mausklick auf den Haltepunkt markiert den Anweisungsblock im Editor.
            </para>
          </formalpara>
          <formalpara>
            <title>
              <guibutton>
                Markierung aufheben              </guibutton>
            </title>
            <para>
              Diese Schaltfl
              &auml;
              che l
              &ouml;
              scht die farbliche Kennzeichnung im Editorfenster.
            </para>
          </formalpara>
        </sect1>
        <sect1>
          <title>
            Das Logfenster          </title>
          <para>
            <screenshot>
              <mediaobject>
                <imageobject>
                  <imagedata>
                </imageobject>
              </mediaobject>
            </screenshot>
            In diesem Fenster erscheinen alle Fehler und Warnungen die w
            &auml;
            hrend des Kompilierens und Ausf
            &uuml;
            hrens auftreten. Die Schaltfl
            &auml;
            che
            <guibutton>
              L
              &ouml;
              schen            </guibutton>
            l
            &ouml;
            scht alle bisherigen Ausgaben.
          </para>
        </sect1>
      </article>

</xsl:template>
</xsl:stylesheet>