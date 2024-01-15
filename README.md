





# **Inhalt**
[1.	Einleitung	3](#_toc156240255)

[Architektur der Applikation	3](#_toc156240256)

[2.	Umgesetzte Anforderungen	4](#_toc156240257)

[1. Verwaltung von Fahrtdaten	4](#_toc156240258)

[2. Anzeige und Filterung von Fahrtdaten	4](#_toc156240259)

[3. Statistische Auswertungen	4](#_toc156240260)

[4.  Datenexport und -import	4](#_toc156240261)

[5.	Benutzeranpassungen und Einstellungen	4](#_toc156240262)

[3. Überblick über das System aus Entwicklersicht	5](#_toc156240263)

[4. Sonarcloud & PMD	5](#_toc156240264)

[5.  JavaDoc für wichtige Klassen, Interfaces und Methoden	5](#_toc156240265)

[Klasse Fahrtenbuch UI:	6](#_toc156240266)

[Kernfunktionalitäten	6](#_toc156240267)

[·	Anfangsbildschirm (Startfenster): Initialisiert und zeigt den Startbildschirm der Applikation an.	6](#_toc156240268)

[·	Übersichtsbildschirm (Hauptbildschirm): Stellt eine Übersichtstabelle aller Fahrten dar und ermöglicht Benutzerinteraktionen wie das Filtern von Daten und das Öffnen weiterer Fenster für detaillierte Funktionen.	6](#_toc156240269)

[·	Neue Fahrt anlegen: Erlaubt dem Benutzer, eine neue Fahrt mit allen relevanten Details anzulegen.	6](#_toc156240270)

[·	Fahrt bearbeiten: Ermöglicht das Bearbeiten der Details einer ausgewählten Fahrt.	6](#_toc156240271)

[·	Statistische Auswertungen anzeigen: Zeigt verschiedene statistische Ansichten, wie Jahresstatistik und erweiterte Kilometerstatistik.	6](#_toc156240272)

[·	Export/Import von Daten: Implementiert Funktionen zum Speichern und Laden der Fahrtdaten in bzw. aus CSV-Dateien.	6](#_toc156240273)

[·	Einstellungen verwalten: Bietet einen Einstellungsbildschirm, in dem Benutzer die Kategorien verwalten und Einstellungen wie den Speicherpfad anpassen können.	6](#_toc156240274)

[Wichtige Methoden	6](#_toc156240275)

[public void start(Stage primaryStage)	6](#_toc156240276)

[private void overview(Stage primaryStage)	7](#_toc156240277)

[private void neueFahrt(Stage primaryStage)	7](#_toc156240278)

[private void bearbeiteFahrt(Fahrt ausgewaehlteFahrt, Stage primaryStage)	7](#_toc156240279)

[private void switchToSettings(Stage primaryStage)	7](#_toc156240280)

[private void initializeStatistikMenuButton()	8](#_toc156240281)

[private void initializeGrafikMenuButton()	8](#_toc156240282)

[void zeigeKilometerDiagramm()	8](#_toc156240283)

[void zeigeJahresKilometerDiagramm()	8](#_toc156240284)

[private TableView<Map.Entry<Integer, Map<String, Double>>> erstelleJahresKilometerTableView(Set<String> kategorien)	8](#_toc156240285)

[void zeigeErweiterteKilometerStatistik()	9](#_toc156240286)

[private TableView<Map.Entry<YearMonth, Map<String, Double>>> erstelleErweiterteKilometerTableView(Set<String> kategorien)	9](#_toc156240287)

[private void aktualisiereErweiterteKilometerTabelle(TableView<Map.Entry<YearMonth, Map<String, Double>>> tableView)	9](#_toc156240288)

[void zeigeJahresKilometerStatistik()	9](#_toc156240289)

[void oeffneFilter(TableView fahrtenTabelle)	9](#_toc156240290)

[Klasse Fahrtenbuch:	10](#_toc156240291)

[1. Konstruktoren	10](#_toc156240292)

[2. Methoden zur Verwaltung von Fahrten	10](#_toc156240293)

[Methoden zur Datenabfrage und -aufbereitung	10](#_toc156240294)

[Kategorienverwaltung und Filterung	11](#_toc156240295)

[Export und Import von Daten	11](#_toc156240296)

[5. Installationsanleitung	11](#_toc156240297)



#
1. # <a name="_toc156237760"></a><a name="_toc156240255"></a>**Einleitung**
## <a name="_toc156240256"></a><a name="_hlk156238332"></a>**Architektur der Applikation**
- Das Model besteht aus den Klassen Fahrtenbuch, Fahrt und FahrtStatus die die Kernlogik und die Datenstruktur der Anwendung darstellen. Diese Klassen sind für die Verwaltung der Fahrtdaten verantwortlich, einschließlich Operationen wie Speichern, Laden, Bearbeiten und Löschen von Einträgen sowie die Bereitstellung von statistischen Daten.
- Die View wird durch die Klasse FahrtenbuchUI repräsentiert, die die grafische Benutzeroberfläche (GUI) der Anwendung bildet. Diese Klasse nutzt JavaFX-UI-Komponenten, um die Daten in einer benutzerfreundlichen Form darzustellen. Dazu gehören Tabellenansichten für die Auflistung der Fahrten, Formulare für die Dateneingabe, Diagramme für statistische Darstellungen und verschiedene Steuerelemente wie Buttons und Menüs für die Interaktion mit der Anwendung.
- Der Controller ist in der FahrtenbuchUI-Klasse integriert und handhabt die Benutzerinteraktionen. Er reagiert auf Benutzereingaben, leitet die Anfragen an das Model weiter und aktualisiert die View basierend auf den Rückmeldungen und Datenänderungen. Diese Schicht sorgt dafür, dass die Benutzeroberfläche und das Datenmodell synchronisiert und konsistent bleiben.
##
#
1. # <a name="_toc156237766"></a><a name="_toc156240257"></a>**Umgesetzte Anforderungen**
### <a name="_toc156240258"></a>**1. Verwaltung von Fahrtdaten**
- **Erfassung neuer Fahrten:** Man kann über ein spezielles Eingabeformular neue Fahrten hinzufügen. Dieses Formular erfasst relevante Informationen wie KFZ-Kennzeichen, Datum, Uhrzeit, gefahrene Kilometer und Kategorien.
- **Bearbeitung bestehender Fahrten:** Aus der Übersichtstabelle heraus können Benutzer eine Fahrt auswählen und über ein Dialogfenster bearbeiten. Änderungen können an verschiedenen Datenpunkten, wie Datum oder Kilometer, vorgenommen werden.
- **Löschen von Fahrten:** Benutzer haben die Möglichkeit, ausgewählte Fahrten zu löschen.
### <a name="_toc156240259"></a>**2. Anzeige und Filterung von Fahrtdaten**
- **Übersichtliche Darstellung:** Die Hauptansicht zeigt eine Tabelle aller erfassten Fahrten. Diese Tabelle bietet eine klare und strukturierte Übersicht.
- **Filterfunktionen:** Benutzer können Fahrtdaten nach verschiedenen Kriterien wie Datum, Durchschnittsgeschwindigkeit und Kategorien filtern. Diese Funktionalität verbessert die Benutzerfreundlichkeit, insbesondere bei einer großen Anzahl von Fahrten.
### <a name="_toc156240260"></a>**3. Statistische Auswertungen**
- **Grafische Darstellungen:** Die Applikation bietet die Möglichkeit, statistische Daten in Form von Balkendiagrammen zu visualisieren. Dies umfasst beispielsweise die Anzeige der gefahrenen Kilometer pro Monat oder Jahr.
- **Erweiterte Statistiken:** Neben den Basisstatistiken können Benutzer auch detaillierte statistische Auswertungen einsehen, die eine tiefere Analyse der Fahrtdaten ermöglichen.

### <a name="_toc156240261"></a>**4.  Datenexport und -import**
- **Speichern von Daten:** Die Applikation bietet eine Exportfunktion, mit der die erfassten Fahrtdaten in einer CSV-Datei gespeichert werden können. Dies ist nützlich für die Datensicherung und weitere Verarbeitung außerhalb der Applikation.
- **Laden von Daten:** Ebenso können Benutzer Daten aus einer CSV-Datei importieren, was den Datenaustausch mit anderen Systemen erleichtert.

- ### <a name="_toc156240262"></a>**Benutzeranpassungen und Einstellungen**
- **Kategorienverwaltung:** Benutzer können Kategorien hinzufügen, bearbeiten oder entfernen. Dies ermöglicht eine individuelle Anpassung der Kategorisierung von Fahrten.
- **Anpassung von Einstellungen:** Es können Einstellungen wie den Speicherpfad für den Export angepasst werden, was zusätzliche Flexibilität bietet.


# <a name="_toc156240263"></a>**3. Überblick über das System aus Entwicklersicht**

##
##
##
##










# <a name="_toc156237768"></a><a name="_toc156240264"></a>**4. Sonarcloud & PMD**
**Sonarcloud:**

PMD:

PMD-Output vor den Fixes:

PMD-Output nach den Fixes:

# <a name="_toc156237769"></a><a name="_toc156240265"></a>**5.  JavaDoc für wichtige Klassen, Interfaces und Methoden**
- **FahrtenbuchUI:** Ist eine zentrale Klasse in der Fahrtenbuch-Applikation, die für die Gestaltung und Interaktivität der grafischen Benutzeroberfläche (GUI) verantwortlich ist. Sie verwendet JavaFX zur Erstellung und Steuerung verschiedener Bildschirmelemente und ermöglicht Benutzerinteraktionen wie das Anzeigen, Erstellen, Bearbeiten und Löschen von Fahrten sowie das Anzeigen statistischer Daten.
- **Fahrtenbuch:** Diese Klasse ist für die Kernlogik und Datenhaltung für die Fahrten zu verwalten. Sie ist verantwortlich für die Speicherung, Filterung und Berechnung von Statistiken bezüglich der Fahrten.
- **Fahrt:** Eine Klasse, die die Datenstruktur für eine einzelne Fahrt darstellt, einschließlich Details wie Datum, Uhrzeit, gefahrene Kilometer und Kategorien.
- **FahrtStatus**: FahrtStatus ist ein enum, das verschiedene Zustände einer Fahrt repräsentiert. Als enum bietet es eine festgelegte Menge von Konstanten, die den Status einer Fahrt klar und konsistent definieren.

## <a name="_toc156237770"></a><a name="_toc156240266"></a>**Klasse Fahrtenbuch UI:**
## <a name="_toc156237771"></a><a name="_toc156240267"></a>**Kernfunktionalitäten**
- # <a name="_toc156237772"></a><a name="_toc156240268"></a>Anfangsbildschirm (Startfenster): Initialisiert und zeigt den Startbildschirm der Applikation an.
- # <a name="_toc156237773"></a><a name="_toc156240269"></a>Übersichtsbildschirm (Hauptbildschirm): Stellt eine Übersichtstabelle aller Fahrten dar und ermöglicht Benutzerinteraktionen wie das Filtern von Daten und das Öffnen weiterer Fenster für detaillierte Funktionen.
- # <a name="_toc156237774"></a><a name="_toc156240270"></a>Neue Fahrt anlegen: Erlaubt dem Benutzer, eine neue Fahrt mit allen relevanten Details anzulegen.
- # <a name="_toc156237775"></a><a name="_toc156240271"></a>Fahrt bearbeiten: Ermöglicht das Bearbeiten der Details einer ausgewählten Fahrt.
- # <a name="_toc156237776"></a><a name="_toc156240272"></a>Statistische Auswertungen anzeigen: Zeigt verschiedene statistische Ansichten, wie Jahresstatistik und erweiterte Kilometerstatistik.
- # <a name="_toc156237777"></a><a name="_toc156240273"></a>Export/Import von Daten: Implementiert Funktionen zum Speichern und Laden der Fahrtdaten in bzw. aus CSV-Dateien.
- # <a name="_toc156237778"></a><a name="_toc156240274"></a>Einstellungen verwalten: Bietet einen Einstellungsbildschirm, in dem Benutzer die Kategorien verwalten und Einstellungen wie den Speicherpfad anpassen können.
## <a name="_toc156237779"></a><a name="_toc156240275"></a>**Wichtige Methoden**
### <a name="_toc156237780"></a><a name="_toc156240276"></a>**public void start(Stage primaryStage)**
Diese Methode ist der Einstiegspunkt für die JavaFX-Anwendung. Sie initialisiert und zeigt den Startbildschirm der Applikation an.

- **Ablauf**:
  - Ein **StackPane** als Root-Element und ein **ProgressBar** für den Ladevorgang werden erstellt.
  - Ein Logo wird geladen und auf dem Bildschirm angezeigt.
  - Eine **FadeTransition** wird verwendet, um das Logo sanft einzublenden.
  - Nach Abschluss des Ladevorgangs wird das Hauptfenster (**overview**) aufgerufen.

### <a name="_toc156237781"></a><a name="_toc156240277"></a>**private void overview(Stage primaryStage)**
Erstellt und zeigt den Hauptbildschirm der Applikation, inklusive der Fahrtentabelle und verschiedener Steuerungselemente.

- **Ablauf**:
  - Erstellt eine **TableView**, um die Fahrtendaten anzuzeigen.
  - Initialisiert verschiedene Steuerelemente wie Buttons für das Hinzufügen, Bearbeiten und Löschen von Fahrten, sowie für den Zugriff auf Einstellungen und Statistiken.
  - Richtet Event-Handler für diese Steuerelemente ein, um entsprechende Aktionen auszulösen.
  - Verwendet **BorderPane** und **VBox**/**HBox** für das Layout.
###
### <a name="_toc156237782"></a><a name="_toc156240278"></a>**private void neueFahrt(Stage primaryStage)**
Öffnet ein Fenster, in dem der Benutzer eine neue Fahrt anlegen kann.

- **Ablauf**:
  - Zeigt ein Formular mit Eingabefeldern für Details wie KFZ-Kennzeichen, Datum, Uhrzeiten, gefahrene Kilometer und Kategorien.
  - Enthält einen 'Speichern'-Button, der die eingegebenen Daten validiert und an das Fahrtenbuch zur Speicherung weiterleitet.
  - Verwendet **TextField**, **DatePicker** und andere JavaFX-Komponenten für die Dateneingabe.


### <a name="_toc156237783"></a><a name="_toc156240279"></a>**private void bearbeiteFahrt(Fahrt ausgewaehlteFahrt, Stage primaryStage)**
Ermöglicht das Bearbeiten einer ausgewählten Fahrt.

- **Ablauf**:
  - Öffnet ein Dialogfenster, das mit den Details der ausgewählten Fahrt vorbefüllt ist.
  - Ermöglicht dem Benutzer, Änderungen vorzunehmen und diese zu speichern.
  - Implementiert eine Funktion zum Löschen der ausgewählten Fahrt.
  - Verwendet **Dialog**-Klasse für die Darstellung und **TextField**, **DatePicker** für die Eingabe.


### <a name="_toc156237784"></a><a name="_toc156240280"></a>**private void switchToSettings(Stage primaryStage)**
Zeigt ein Einstellungsfenster für die Verwaltung von Kategorien und Einstellungen wie dem Speicherpfad.

- **Ablauf**:
  - Stellt ein Benutzerinterface bereit, in dem Kategorien hinzugefügt, bearbeitet oder entfernt werden können.
  - Erlaubt dem Benutzer via **Button** die Programmdaten entweder zu importieren oder zu exportieren, dies öffnet einen DirectoryChooser um den gewünschten Zielordner auszuwählen. 
  - Nutzt **ListView** und **TextField** für die Anzeige, das Hinzufügen und die Bearbeitung der Kategorien sowie Eingabefelder für den Pfad.


### <a name="_toc156237785"></a><a name="_toc156240281"></a>**private void initializeStatistikMenuButton()**
1. Diese Methode initialisiert einen Menü-Button für statistische Ansichten.
1. Sie fügt Menüeinträge hinzu, die es dem Benutzer ermöglichen, verschiedene statistische Ansichten wie Jahresstatistik und erweiterte Statistik aufzurufen.
1. Jeder Menüeintrag hat einen Event-Handler, der die entsprechende statistische Ansicht anzeigt.


### <a name="_toc156237786"></a><a name="_toc156240282"></a>**private void initializeGrafikMenuButton()**
1. Ähnlich wie **initializeStatistikMenuButton**, aber für grafische Darstellungen der Statistiken.
1. Fügt Menüeinträge hinzu für die Anzeige von Kilometern pro Monat und Jahr in Diagrammform.
1. Jeder Eintrag löst eine Methode aus, die das entsprechende Diagramm erstellt und anzeigt.



### <a name="_toc156237787"></a><a name="_toc156240283"></a>**void zeigeKilometerDiagramm()**
1. Erstellt und zeigt ein Balkendiagramm der gefahrenen Kilometer pro Monat und Kategorie.
1. Nutzt die Daten aus **fahrtenbuch**, um die Diagramme zu generieren.
1. Verwendet JavaFX-Komponenten wie **BarChart**, **CategoryAxis**, und **NumberAxis** für die Diagrammerstellung.


### <a name="_toc156237788"></a><a name="_toc156240284"></a>**void zeigeJahresKilometerDiagramm()**
1. Ähnlich wie **zeigeKilometerDiagramm**, aber zeigt die Gesamtkilometer pro Jahr und Kategorie.
1. Stellt die jährliche Fahrleistung in Balkendiagrammform dar.
###

### <a name="_toc156237789"></a><a name="_toc156240285"></a>**private TableView<Map.Entry<Integer, Map<String, Double>>> erstelleJahresKilometerTableView(Set<String> kategorien)**
1. Erstellt eine **TableView** für die Darstellung von Jahreskilometern pro Kategorie.
1. Generiert Spalten für jedes Jahr und jede Kategorie basierend auf den bereitgestellten Daten.


### <a name="_toc156237790"></a><a name="_toc156240286"></a>**void zeigeErweiterteKilometerStatistik()**
1. Zeigt eine detaillierte Statistik der gefahrenen Kilometer pro Monat und Kategorie in einer **TableView**.
1. Nutzt **erstelleErweiterteKilometerTableView** für die Erstellung der Tabelle und aktualisiert sie mit aktuellen Daten.


### <a name="_toc156237791"></a><a name="_toc156240287"></a>**private TableView<Map.Entry<YearMonth, Map<String, Double>>> erstelleErweiterteKilometerTableView(Set<String> kategorien)**
1. Erstellt eine **TableView** für die detaillierte Darstellung von Kilometern pro Monat und Kategorie.
1. Ähnlich wie **erstelleJahresKilometerTableView**, aber mit einer feineren Aufteilung auf Monatsebene.


### <a name="_toc156237792"></a><a name="_toc156240288"></a>**private void aktualisiereErweiterteKilometerTabelle(TableView<Map.Entry<YearMonth, Map<String, Double>>> tableView)**
1. Aktualisiert die Tabelle der erweiterten Kilometerstatistik mit den neuesten Daten aus dem Fahrtenbuch.


### <a name="_toc156237793"></a><a name="_toc156240289"></a>**void zeigeJahresKilometerStatistik()**
1. Zeigt eine Statistik der gefahrenen Kilometer pro Jahr und Kategorie in einer **TableView**.
1. Überprüft, ob Daten vorhanden sind, und zeigt dann die Statistik in tabellarischer Form an.


### <a name="_toc156237794"></a><a name="_toc156240290"></a>**void oeffneFilter(TableView fahrtenTabelle)**
1. Öffnet ein Dialogfenster zur Filterung von Fahrten nach verschiedenen Kriterien.
1. Der Benutzer kann Fahrten nach Datum, Durchschnittsgeschwindigkeit und Kategorien filtern.
1. Stellt eine dynamische Filterfunktion bereit, die die **TableView** mit den gefilterten Ergebnissen aktualisiert.

`     `**private void exporterMethod(String exportPath) throws IOException**

1. Nimmt einen Path String und exportiert mittels CSVWritter die Daten aus Fahrtenbuch.fahrten und Fahrtenbuch.kategorien.
1. Daten werden über Schleife gesammelt und formattiert und dem CSVWritter übergeben

`     `**private void importerMethod(Path importPath)**

- Nimmt einen Path String und importiert mittels CSVReader die Daten aus dem angegebenen Verzeichnis und speichert sie in Fahrtenbuch.fahrten und Fahrtenbuch.kategorien.
- Checkt ob das Hauptverzeichnis existiert und erstellt es wenn nicht.
- Erstellt variablen mit den Daten und erstellt damit neue Fahrten.


## <a name="_toc156237795"></a><a name="_toc156240291"></a>**Klasse Fahrtenbuch:**
## <a name="_toc156237796"></a><a name="_toc156240292"></a>**1. Konstruktoren**
- **Fahrtenbuch(List<String> kategorien, List<Fahrt> fahrten)**
  - **Zweck**: Erstellt ein Fahrtenbuch mit vorgegebenen Kategorien und Fahrten.
  - **Parameter**:
    - **kategorien**: Eine Liste der Kategorien.
    - **fahrten**: Eine Liste der Fahrten.
- **Fahrtenbuch()**
  - **Zweck**: Erstellt ein leeres Fahrtenbuch, nützlich beim ersten Start der Applikation.
## <a name="_toc156237797"></a><a name="_toc156240293"></a>**2. Methoden zur Verwaltung von Fahrten**
- **neueFahrt(...)**
  - **Zweck**: Fügt eine neue Fahrt hinzu.
  - **Parameter**: Detaillierte Informationen über die Fahrt wie KFZ-Kennzeichen, Datum, Zeiten, Kilometer, Fahrtstatus und Kategorien.
  - **Ausnahmen**: **IOException**, falls beim Speichern Probleme auftreten.
- **bearbeiteFahrt(...)**
  - **Zweck**: Ermöglicht die Bearbeitung einer bestehenden Fahrt.
  - **Parameter**: Ähnlich wie bei **neueFahrt**, jedoch mit dem Ziel der Aktualisierung.
- **loescheFahrten(...)**
  - **Zweck**: Löscht eine spezifische Fahrt basierend auf Schlüsselinformationen.
  - **Parameter**: KFZ-Kennzeichen, Datum und Abfahrtszeit.
- **planeZukuenftigeFahrten(...)**
  - **Zweck**: Plant wiederkehrende Fahrten.
  - **Parameter**: Daten für zukünftige Fahrten, KFZ-Kennzeichen, Abfahrtszeit und Kategorien.
  - **Ausnahmen**: **IOException** bei Speicherproblemen.
## ` `**<a name="_toc156237798"></a><a name="_toc156240294"></a>Methoden zur Datenabfrage und -aufbereitung**
- **listeFahrten()**
  - **Zweck**: Gibt eine Liste aller erfassten Fahrten zurück.
- **berechneKilometerProMonat()**
  - **Zweck**: Berechnet die insgesamt gefahrenen Kilometer pro Monat.
  - **Rückgabe**: Eine **Map** von **YearMonth** zu **Double** (Kilometern).
- **berechneKilometerProMonatUndKategorie()**
  - **Zweck**: Berechnet die gefahrenen Kilometer pro Monat und Kategorie.
  - **Rückgabe**: Eine verschachtelte **Map** mit **YearMonth** und Kategorienamen zu Kilometern.
- **berechneKilometerProJahrUndKategorie()**
  - **Zweck**: Berechnet die gefahrenen Kilometer pro Jahr und Kategorie.
  - **Rückgabe**: Eine verschachtelte **Map** mit Jahreszahlen und Kategorienamen zu Kilometern.
## <a name="_toc156237799"></a><a name="_toc156240295"></a>**Kategorienverwaltung und Filterung**
- **addKategorie(String kategorie)**

Fügt eine neue Kategorie hinzu.

**Parameter**: Der Name der Kategorie.

- **getKategorien() und getKategorien(Boolean x)**

Gibt eine Liste aller einzigartigen Kategorien zurück, entweder als **Set** oder **ObservableList**.

- **addKategories(Collection kategorienNeu)**

Fügt eine Sammlung von Kategorien zur Liste hinzu.

- **Filtermethoden** (**filterByDate**, **filterByAvgVUnder**, **filterByAvgVOver**, **filterByKategorie**)

Ermöglichen es, Fahrten nach verschiedenen Kriterien wie Datum, Durchschnittsgeschwindigkeit und Kategorien zu filtern.
## <a name="_toc156237800"></a><a name="_toc156240296"></a>**Export und Import von Daten**
- exporterMethod

Speichert Daten in ausgewähltem Pfad.

- importerMethod

Importiert Daten von ausgewähltem Pfad.

- **exportFahrt()**

Ruft exporterMethod mit Pfad des standard Speicherortes auf.

**IOException** bei Schreibproblemen.

- **importFahrt()**

Ruft importerMethod mit Pfad des standard Speicherortes auf.

- **manualImport(Path path)**

Ruft importerMethod mit Pfad eines ausgewähltem Speicherortes auf.

- ExportManual(Path path)

Ruft exporterMethod mit Pfad eines ausgewähltem Speicherortes auf.

# <a name="_toc156237801"></a><a name="_toc156240297"></a>**5. Installationsanleitung**
Nachdem Sie das Maven-Projekt gebaut haben, können Sie die Applikation wie folgt ausführen:

1. Öffnen Sie ein Terminalfenster.
1. Wechseln Sie in das Verzeichnis, in dem sich die generierte JAR-Datei befindet. Dies ist typischerweise das target-Verzeichnis im Projektordner:

c**d Pfad/zum//Projekt/target**

1. Führen Sie die Applikation aus, indem Sie den folgenden Befehl eingeben:

**Fahrtenbuch-1.0-SNAPSHOT.jar**

Ersetzen Sie Pfad/zu/Ihrem/Projekt mit dem tatsächlichen Pfad zu Ihrem Projekt und Fahrtenbuch-1.0-SNAPSHOT.jar mit dem Namen der JAR-Datei, die Maven erstellt hat



` `Jänner 2024	Projektdokumentation	 12/12
