package at.jku.se.prse.team3;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.dropbox.core.v2.files.UploadErrorException;
import javafx.animation.FadeTransition;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;


import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;

import javafx.scene.shape.Box;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.*;
import org.controlsfx.control.CheckComboBox;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class FahrtenbuchUI extends Application {
    private Image logo;
    private Image logoFull;
    private static final Logger LOGGER=Logger.getLogger(Fahrtenbuch.class.getName());
    private Button filterButton;
    private Fahrtenbuch fahrtenbuch;
    private Button setButton;
    private Button backButton;
    private Button newTripButton;
    private TableView<Fahrt> fahrtenTabelle;
    private Button speichernButton = new Button("\uD83D\uDCBE Fahrt speichern");

    private Button newEditButton;
    private Button statistikButton;
    private Button erweiterteStatistikButton;
    private Button jahresStatistikButton;
    private MenuButton statistikMenuButton;
    private MenuButton grafikMenuButton;
    private ObservableList<String> kategorienListe;
    private ObservableList<Fahrt> fahrtenListe; // Klassenvariable für die Fahrtenliste
    private ButtonType deleteButtonType = new ButtonType("Löschen", ButtonBar.ButtonData.APPLY);


    /**
     * Konstruktor
     *
     * @param fahrtenbuch sets the Fahrtenbuch Object the UI is working on
     */
    public FahrtenbuchUI(Fahrtenbuch fahrtenbuch) {
        this.fahrtenbuch = fahrtenbuch;
        // Initialisierung der fahrtenListe mit leeren Daten oder vorhandenen Daten aus fahrtenbuch
        this.fahrtenListe = FXCollections.observableArrayList();

    }

    public static void main(String[] args) {

        launch(args);
    }

    /**
     * This is the startup window of our Fahrtenbuch
     *
     * @param primaryStage JavaFX Stage
     * @throws InterruptedException
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        StackPane root = new StackPane();

        InputStream imageStream = getClass().getResourceAsStream("/logo.png");

        if (imageStream!=null) {
            logo = new Image(imageStream);
        } else {
            logo=new WritableImage(100,100);
        }


        ImageView logoView = new ImageView(logo);
        /*logoView.setOnMousePressed(event -> {
            primaryStage.hide();
            overview(primaryStage);
        });
*/


        logoView.setOpacity(0);
       // primaryStage.initStyle(StageStyle.UNDECORATED);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setStyle("-fx-accent: black;");
        progressBar.setMaxWidth(logo.getWidth());
        root.getChildren().addAll(logoView, progressBar);
        StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);

        // Create animation
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), logoView);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < 100; i++) {
                    updateProgress(i + 1, 100);
                    Thread.sleep(15);
                    if (i == 99) Thread.sleep(1000);
                }
                Platform.runLater(() ->
                {
                    primaryStage.hide();
                    overview(primaryStage);
                });

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);

        primaryStage.setWidth(logo.getWidth());
        primaryStage.setHeight(logo.getHeight());
        primaryStage.show();


    }

    /**
     * Fahrtentabelle, Buttons, Logo etc.. Überblick
     *
     * @param primaryStage
     */
    public void overview(Stage primaryStage) {


        InputStream logoPath=getClass().getResourceAsStream("/logoFull.png");
        if (logoPath!=null)logoFull = new Image(logoPath);
        else {
            logo=new WritableImage(100,100);
        }
        Stage overviewStage=new Stage(StageStyle.DECORATED);


        overviewStage.getIcons().add(logoFull);
        kategorienListe = fahrtenbuch.getKategorien(true);
        //start tabellerische Ansicht
        // Laden der vorhandenen Fahrten aus dem Fahrtenbuch und Initialisierung der fahrtenListe
        fahrtenListe.clear();
        fahrtenListe.addAll(fahrtenbuch.listeFahrten());
        // Erstellen und Konfigurieren der TableView und anderer UI-Komponenten
        fahrtenTabelle = new TableView<>(fahrtenListe);
        fahrtenTabelle.setId("fahrtenTabelle");
        fahrtenTabelle.setItems(fahrtenListe);
        // Weitere Konfigurationen des Buttons hier
        initializeStatistikMenuButton();
        initializeGrafikMenuButton();
        speichernButton.setId("saveButton");
        statistikButton = new Button("Statistik grafisch anzeigen");
        statistikButton.setOnAction(event -> zeigeKilometerDiagramm());
        erweiterteStatistikButton = new Button("Erweiterte Statistik anzeigen");
        erweiterteStatistikButton.setOnAction(event -> zeigeErweiterteKilometerStatistik());
        jahresStatistikButton = new Button("Jahresstatistik anzeigen");
        jahresStatistikButton.setOnAction(event -> zeigeJahresKilometerStatistik());
        filterButton = new Button("Filtern");
        filterButton.setOnAction(event -> oeffneFilter(fahrtenTabelle));
        Button info = new Button("Info Page");
        info.setOnAction(event ->openAboutPage());

        TableColumn<Fahrt, String> kfz = new TableColumn<>("KFZ-Kennzeichen");
        kfz.setCellValueFactory(new PropertyValueFactory<>("kfzKennzeichen"));

        TableColumn<Fahrt, LocalDate> date = new TableColumn<>("Datum");
        date.setCellValueFactory(new PropertyValueFactory<>("datum"));

        TableColumn<Fahrt, LocalTime> abf = new TableColumn<>("Abfahrtszeit");
        abf.setCellValueFactory(new PropertyValueFactory<>("abfahrtszeit"));

        TableColumn<Fahrt, LocalTime> ank = new TableColumn<>("Ankunftszeit");
        ank.setCellValueFactory(new PropertyValueFactory<>("ankunftszeit"));

        TableColumn<Fahrt, Double> gefKM = new TableColumn<>("gef. Kilometer");
        gefKM.setCellValueFactory(new PropertyValueFactory<>("gefahreneKilometer"));

        TableColumn<Fahrt, LocalTime> aktivFZ = new TableColumn<>("aktive Fahrzeit");
        aktivFZ.setCellValueFactory(new PropertyValueFactory<>("aktiveFahrzeit"));

        TableColumn<Fahrt, FahrtStatus> status = new TableColumn<>("Fahrtstatus");
        status.setCellValueFactory(new PropertyValueFactory<>("fahrtstatus"));

        TableColumn<Fahrt,Double> avgSpeed= new TableColumn<>(("Ø Geschwindigkeit" ));

        avgSpeed.setCellValueFactory(fahrtDoubleCellDataFeatures -> {
            Fahrt fahrt=fahrtDoubleCellDataFeatures.getValue();
            Double gefKM1= gefKM.getCellObservableValue(fahrt).getValue();

            LocalTime fahrzeit= aktivFZ.getCellObservableValue(fahrt).getValue();
            Double fZ= (fahrzeit.getHour() * 60 + fahrzeit.getMinute()) / 60.00;
            SimpleDoubleProperty rValue=new SimpleDoubleProperty(gefKM1/fZ);
            if (rValue.getValue().isNaN()) rValue=new SimpleDoubleProperty(0.00);
            return rValue.asObject() ;
        });

        TableColumn<Fahrt, String> kateg = new TableColumn<>("Kategorien");
        kateg.setCellValueFactory(cellData -> {
            List<String> categories = cellData.getValue().getKategorien();
            String catToString = String.join(", ", categories);
            return new SimpleStringProperty(catToString);
        });

        fahrtenTabelle.getColumns().addAll(kfz, date, abf, ank, gefKM, aktivFZ, status, kateg,avgSpeed);

        //ende tabellarische ansicht
        overviewStage.setTitle("Fahrtenbuch");
        setButton = new Button();
        setButton.setText("Settings");
        setButton.setId("settingsButton");

        Stage finalPrimaryStage = overviewStage;
        setButton.setOnAction(actionEvent -> {
            switchToSettings(finalPrimaryStage);

        });
        StackPane layoutFahrten = new StackPane();

        newEditButton = new Button();
        newEditButton.setText("Fahrt bearbeiten");
        newEditButton.setId("editButton");

        Stage finalPrimaryStage1 = overviewStage;
        newEditButton.setOnAction(event -> {
            Fahrt ausgewaehlteFahrt = fahrtenTabelle.getSelectionModel().getSelectedItem();
            if (ausgewaehlteFahrt != null) {
                try {
                    bearbeiteFahrt(ausgewaehlteFahrt, finalPrimaryStage1);
                } catch (DateTimeParseException d) {
                    Alert dateAlert = new Alert(Alert.AlertType.WARNING);
                    dateAlert.setContentText("Wrong Format! use: DD:MM:YYYY or HH:MM..");
                    d.printStackTrace();
                    dateAlert.showAndWait();

                } catch (NumberFormatException n) {
                    Alert numberAlert = new Alert(Alert.AlertType.WARNING);
                    numberAlert.setContentText("Numeric entries only..");
                    numberAlert.showAndWait();
                    n.printStackTrace();

                }

            } else {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Keine Auswahl");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wählen Sie zuerst eine Fahrt aus der Liste aus.");
                alert.showAndWait();
            }
        });

        newTripButton = new Button();
        newTripButton.setText("Neue Fahrt");
        newTripButton.setId("newTripButton");
        newTripButton.setStyle("-fx-background-colour: #00ff00");
        Stage finalPrimaryStage2 = overviewStage;
        newTripButton.setOnAction(actionEvent -> neueFahrt(finalPrimaryStage2));

        // HBox für die Buttons erstellen
        HBox leftButtonBox = new HBox(10);
        leftButtonBox.getChildren().addAll(newTripButton, setButton, newEditButton, statistikMenuButton, grafikMenuButton,filterButton, info);
        leftButtonBox.setAlignment(Pos.TOP_LEFT);
        leftButtonBox.setPadding(new javafx.geometry.Insets(4, 1, 10, 1));


        // Menü und Buttons im VBox platzieren
        VBox topBox = new VBox();
        topBox.getChildren().addAll(leftButtonBox);
        topBox.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(fahrtenTabelle); // Ersetzen Sie createTableView() durch Ihre TableView-Initialisierung
        root.setTop(topBox);

        // dies würde bei jedem Klick einer Zeile das Edit-Fenster aufmachen
/*        fahrtenTabelle.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
             bearbeiteFahrt(newSelection, primaryStage);
            }
        });*/

        Scene fahrten = new Scene(root, 720, 400);

        overviewStage.setScene(fahrten);


        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        overviewStage.show();
        //when closing this stage data will be saved to csv
        overviewStage.setOnCloseRequest(windowEvent -> {
            try {
                fahrtenbuch.exportFahrt();
            } catch (IOException e) {
                throw new InExportExc("An Error occurred during Export",e);
            }
        });
    }

    /**
     * Screen zum anlegen einer neuen Fahrt
     *
     * @param primaryStage
     */
    private void neueFahrt(Stage primaryStage) {
        //Liste von zukünftigen LocalDates von wiederkehrenden Fahrten
        List<LocalDate> futureDates = new ArrayList<>();
        List<String> additionalCategories = new ArrayList<>();

        TextField kfzKennzeichen = new TextField();
        kfzKennzeichen.setPromptText("KFZ-Kennzeichen:");
        kfzKennzeichen.setMaxWidth(200);
        kfzKennzeichen.setId("kfzKennzeichenAddField");

        DatePicker datum = new DatePicker();
        datum.setPromptText("Datum der Fahrt");
        datum.getEditor().setDisable(true);
        datum.setMaxWidth(200);
        datum.setOnAction(event -> {
            datum.getValue();

        });

        TextField abfahrtsZeit = new TextField();
        abfahrtsZeit.setPromptText("Abfahrtszeit im Format HH:MM");
        abfahrtsZeit.setMaxWidth(200);
        abfahrtsZeit.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm")));

        TextField ankunftsZeit = new TextField();
        ankunftsZeit.setPromptText("Ankunftszeit im Format HH:MM");
        ankunftsZeit.setMaxWidth(200);
        ankunftsZeit.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm")));

        TextField gefahreneKilometer = new TextField();
        gefahreneKilometer.setPromptText("gefahrene Kilometer");
        gefahreneKilometer.setMaxWidth(200);
        gefahreneKilometer.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        TextField aktiveFahrzeit = new TextField();
        aktiveFahrzeit.setPromptText("Fahrzeit in HH:MM");
        aktiveFahrzeit.setMaxWidth(200);
        aktiveFahrzeit.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm")));
        TextArea angezeigteKategorien = new TextArea();
// UI-Komponenten für die Kategorie
        ComboBox kategorienInput = new ComboBox(kategorienListe);
        kategorienInput.setPromptText("Kategorien auswählen:");

        kategorienInput.setMaxWidth(200);
        kategorienInput.setOnAction(event -> {
            String kategorie = kategorienInput.getValue().toString();
            if (!kategorie.isEmpty()) {
                additionalCategories.add(kategorie);
                angezeigteKategorien.setVisible(true); // TextArea sichtbar machen
                angezeigteKategorien.appendText(kategorie + "; "); // Kategorie zur TextArea hinzufügen

            }
        });

        angezeigteKategorien.setEditable(false);
        angezeigteKategorien.setVisible(false); // Anfangs nicht sichtbar machen
        angezeigteKategorien.setPrefHeight(50); // Höhe der TextArea anpassen

// Füge die Kategorien-Komponenten zur Benutzeroberfläche hinzu
        VBox kategorienBox = new VBox(10);
        kategorienBox.setSpacing(4);
        kategorienBox.getChildren().addAll(kategorienInput, angezeigteKategorien);

        DatePicker future = new DatePicker();
        future.getEditor().setDisable(true);
        future.setPromptText("Zukünftige Fahrten");

        TextArea selectedDates = new TextArea();
        selectedDates.setDisable(true);
        selectedDates.setVisible(false);
        selectedDates.setPrefWidth(84);

        future.setOnAction(event -> {
            LocalDate date = future.getValue();
            addToReoccurances(date, futureDates::add);
            selectedDates.setVisible(true);
            selectedDates.setText(selectedDates.getText() + future.getValue().toString() + "; ");
            selectedDates.setPrefWidth(84 * futureDates.size());
        });

        selectedDates.setPrefHeight(20);

        HBox futureDatesBox = new HBox(10);
        futureDatesBox.setSpacing(5);
        futureDatesBox.getChildren().addAll(future, selectedDates);

        futureDatesBox.setVisible(false);

        ComboBox fahrtstatus = new ComboBox<>();
        fahrtstatus.setItems(FXCollections.observableArrayList(FahrtStatus.values()));
        fahrtstatus.setPromptText("Fahrtstatus");

        fahrtstatus.setOnAction(event -> {
            if (FahrtStatus.ZUKUENFTIG.equals(fahrtstatus.getValue())) {

                futureDatesBox.setVisible(true);
            } else if (FahrtStatus.ABSOLVIERT.equals(fahrtstatus.getValue())) {
                futureDatesBox.setVisible(false);
            } else if (FahrtStatus.AUF_FAHRT.equals(fahrtstatus.getValue())) {
                futureDatesBox.setVisible(false);
            }

        });
        fahrtstatus.setDisable(true);
        datum.setOnAction(event -> {
            datum.getValue();
            if (datum.getValue().isBefore(LocalDate.now())) {
                fahrtstatus.setValue(FahrtStatus.ABSOLVIERT);
            } else if (datum.getValue().isAfter(LocalDate.now())) {
                fahrtstatus.setValue(FahrtStatus.ZUKUENFTIG);
                addToReoccurances(datum.getValue(), futureDates::add);
            } else {
                fahrtstatus.setValue(FahrtStatus.AUF_FAHRT);
            }
        });
        //SPACER BOX
        Box box = new Box(10, 30, 720);
        box.setVisible(true);

        VBox fahrtTextinputboxen = new VBox(1);
        fahrtTextinputboxen.setSpacing(4);
        fahrtTextinputboxen.getChildren().addAll(box, kfzKennzeichen, datum, abfahrtsZeit, ankunftsZeit,
                gefahreneKilometer, aktiveFahrzeit, fahrtstatus, futureDatesBox, kategorienBox
        );

        backButton = new Button();
        backButton.setText("<- Zurück");
        backButton.setOnAction(actionEvent -> {
            overview(primaryStage);
            primaryStage.hide();
        });
        ScrollPane scrollPane = new ScrollPane(fahrtTextinputboxen);
        scrollPane.setFitToWidth(true); // Passt die Breite der ScrollPane an die Breite der VBox an
        scrollPane.setPrefHeight(400); // Setzen Sie eine bevorzugte Höhe
        scrollPane.setPadding(new Insets(10,10,10,10));
        Label info = new Label("    Fahrtinformartionen unten eingeben");
        primaryStage.setTitle("Neue Fahrt");
        StackPane layoutNewTrip = new StackPane();
        //layoutNewTrip.setPadding(new Insets(10,10,10,10));
        layoutNewTrip.getChildren().add(scrollPane);
        backButton = new Button("<- Zurück");
        backButton.setOnAction(event -> {
            overview(primaryStage);
            primaryStage.hide();
        });
        layoutNewTrip.getChildren().add(backButton);
        layoutNewTrip.getChildren().add(speichernButton);
        layoutNewTrip.getChildren().add(info);

        speichernButton.setOnAction(event -> {
            // Setze die ID für den Speichern-Button
            speichernButton.setId("saveButton");

            // Extrahiere den ausgewählten Fahrtstatus
            FahrtStatus ausgewaehlterStatus = (FahrtStatus) fahrtstatus.getValue();

            // Überprüfe, ob ein Fahrtstatus ausgewählt wurde
            if (ausgewaehlterStatus == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Bitte wählen Sie einen Fahrtstatus aus.");
                alert.showAndWait();
                return;
            }

            // Überprüfe die Eingabefelder basierend auf dem ausgewählten Fahrtstatus
            if (ausgewaehlterStatus == FahrtStatus.ZUKUENFTIG) {
                // Für zukünftige Fahrten müssen das KFZ-Kennzeichen, das Datum und die Liste der zukünftigen Termine vorhanden sein
                if (kfzKennzeichen.getText().isEmpty() || futureDates.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Pflichtdaten für Erstellung zukünftiger Fahrt nicht eingetragen!");
                    alert.showAndWait();
                    return;
                }
                // Plane zukünftige Fahrten
                try {
                    fahrtenbuch.planeZukuenftigeFahrten(futureDates, kfzKennzeichen.getText(), LocalTime.parse(abfahrtsZeit.getText()), additionalCategories);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Für die Status 'AUF_FAHRT' und 'ABSOLVIERT' müssen alle Felder ausgefüllt sein
                if (kfzKennzeichen.getText().isEmpty() || datum.getValue() == null || abfahrtsZeit.getText().isEmpty() ||
                        ankunftsZeit.getText().isEmpty() || gefahreneKilometer.getText().isEmpty() ||
                        aktiveFahrzeit.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Pflichtdaten für Erstellung neuer Fahrt nicht eingetragen!");
                    alert.showAndWait();
                    return;
                }
                // Speichere die neue Fahrt
                try {
                    fahrtenbuch.neueFahrt(
                            kfzKennzeichen.getText(),
                            datum.getValue(),
                            LocalTime.parse(abfahrtsZeit.getText()),
                            LocalTime.parse(ankunftsZeit.getText()),
                            Double.parseDouble(gefahreneKilometer.getText()),
                            LocalTime.parse(aktiveFahrzeit.getText()),
                            ausgewaehlterStatus,
                            additionalCategories
                    );
                } catch (IOException | NumberFormatException | DateTimeParseException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setContentText("Ein Fehler ist aufgetreten: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
            // Aktualisiere die Liste der Fahrten in der UI
            fahrtenListe.clear();
            fahrtenListe.addAll(fahrtenbuch.listeFahrten());
        });

        StackPane.setAlignment(speichernButton, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(backButton, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(info, Pos.TOP_LEFT);

        Scene neueFahrt = new Scene(layoutNewTrip, 720, 400);

        primaryStage.setScene(neueFahrt);
        primaryStage.show();
        //when closing this stage data will be saved to csv
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                fahrtenbuch.exportFahrt();
            } catch (IOException e) {
                throw new InExportExc("An Error occurred during Export",e);
            }
        });


    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Benutzer einen Eintrag zum Bearbeiten auswählt.
     *
     * @param ausgewaehlteFahrt ausgewaehlte Fahrt aus der Fahrtentabelle
     * @param primaryStage
     */

    private void bearbeiteFahrt(Fahrt ausgewaehlteFahrt, Stage primaryStage) {
        // Prüfen, ob eine Fahrt ausgewählt wurde
        if (ausgewaehlteFahrt == null) {
            // Fehlermeldung anzeigen
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Keine Fahrt ausgewählt");
            alert.setContentText("Bitte wählen Sie eine Fahrt aus der Liste aus.");
            alert.showAndWait();
            return;
        }

        // Dialogfenster für die Bearbeitung erstellen
        Dialog<Fahrt> dialog = new Dialog<>();
        dialog.setTitle("Fahrt bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie die Details der ausgewählten Fahrt.");

        // Buttons setzen
        ButtonType speichernButtonType = new ButtonType(" Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(speichernButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType);


        // GridPane für die Eingabefelder
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField kfzKennzeichenField = new TextField(ausgewaehlteFahrt.getKfzKennzeichen());
        kfzKennzeichenField.setId("kfzKennzeichenEditField");
        DatePicker datumPicker = new DatePicker(ausgewaehlteFahrt.getDatum());
        datumPicker.setConverter(new LocalDateStringConverter());
        datumPicker.getEditor().setDisable(true);

        TextField abfahrtszeitField = new TextField(ausgewaehlteFahrt.getAbfahrtszeit().toString());
        TextField aktiveFahrzeitField = new TextField(ausgewaehlteFahrt.getAktiveFahrzeit().toString());
        aktiveFahrzeitField.setOnMouseClicked(event -> aktiveFahrzeitField.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm"))));

        abfahrtszeitField.setOnMouseClicked(event -> abfahrtszeitField.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm"))));

        TextField ankunftszeitField = new TextField(ausgewaehlteFahrt.getAnkunftszeit().toString());

        ankunftszeitField.setOnMouseClicked(mouseEvent -> ankunftszeitField.setTextFormatter(new TextFormatter<>(new TimeStringConverter("HH:mm"))));


        TextField gefahreneKilometerField = new TextField(String.valueOf(ausgewaehlteFahrt.getGefahreneKilometer()));
        gefahreneKilometerField.setOnAction(event -> gefahreneKilometerField.setTextFormatter(new TextFormatter<>(new NumberStringConverter())));

        grid.add(new Label("KFZ-Kennzeichen:"), 0, 0);
        grid.add(kfzKennzeichenField, 1, 0);
        grid.add(new Label("Datum:"), 0, 1);
        grid.add(datumPicker, 1, 1);
        grid.add(new Label("Abfahrtszeit:"), 0, 2);
        grid.add(abfahrtszeitField, 1, 2);
        grid.add(new Label("Ankunftszeit:"), 0, 3);
        grid.add(ankunftszeitField, 1, 3);
        grid.add(new Label("Gefahrene Kilometer:"), 0, 4);
        grid.add(gefahreneKilometerField, 1, 4);
        grid.add(new Label("Aktive Fahrzeit:"), 0, 5);
        grid.add(aktiveFahrzeitField, 1, 5);
        // GridPane zum Dialog hinzufügen
        dialog.getDialogPane().setContent(grid);

        // Request focus auf das erste Eingabefeld
        Platform.runLater(kfzKennzeichenField::requestFocus);


        // Ergebnis des Dialogs konvertieren, wenn der Benutzer "Speichern" klickt
        dialog.setResultConverter(dialogButton -> {

            if (dialogButton == speichernButtonType) {

                ausgewaehlteFahrt.setKfzKennzeichen(kfzKennzeichenField.getText());

                try {
                    LocalDate temp = datumPicker.getValue();
                    ausgewaehlteFahrt.setDatum(temp);
                    ausgewaehlteFahrt.setAbfahrtszeit(LocalTime.parse(abfahrtszeitField.getText()));
                    ausgewaehlteFahrt.setAnkunftszeit(LocalTime.parse(ankunftszeitField.getText()));
                    ausgewaehlteFahrt.setGefahreneKilometer(Double.parseDouble(gefahreneKilometerField.getText()));
                    ausgewaehlteFahrt.setAktiveFahrzeit(LocalTime.parse(aktiveFahrzeitField.getText()));
                } catch (DateTimeParseException d) {
                    Alert dateAlert = new Alert(Alert.AlertType.WARNING);
                    dateAlert.setContentText("Wrong Format! use: DD:MM:YYYY or HH:MM..");
                    dateAlert.showAndWait();

                } catch (NumberFormatException n) {
                    Alert numberAlert = new Alert(Alert.AlertType.WARNING);
                    numberAlert.setContentText("Numeric entries only..");
                    numberAlert.showAndWait();


                }
                try {
                    fahrtenbuch.exportFahrt();
                } catch (IOException e) {
                    throw new InExportExc("An Error occurred during Export",e);
                }

            } else if (dialogButton == deleteButtonType) {
                //Bestätigungsdialog
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Bestätigung");
                confirmationDialog.setHeaderText("Löschen bestätigen");
                confirmationDialog.setContentText("Möchten Sie die ausgewählte Fahrt wirklich löschen?");

                confirmationDialog.initModality(Modality.APPLICATION_MODAL);
                confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                ButtonType result = confirmationDialog.showAndWait().orElse(ButtonType.NO);
                if (result == ButtonType.YES) {
                    fahrtenbuch.loescheFahrten(ausgewaehlteFahrt.getKfzKennzeichen(), ausgewaehlteFahrt.getDatum(), ausgewaehlteFahrt.getAbfahrtszeit());
                    fahrtenTabelle.getItems().remove(ausgewaehlteFahrt);
                    try {
                        fahrtenbuch.exportFahrt();
                    } catch (IOException e) {
                        throw new InExportExc("An Error occurred during Export",e);
                    }
                }
            }

            return null;
        });

        // Dialog anzeigen und warten, bis der Benutzer ihn schließt


        Optional<Fahrt> result = dialog.showAndWait();

        result.ifPresent(fahrt -> {
            // Aktualisierte Fahrt in der Liste und in der TableView anzeigen
            fahrtenTabelle.refresh();
            dialog.close();
            // Eventuell Änderungen im Fahrtenbuch speichern oder weitere Aktionen ausführen
        });

//when closing this stage data will be saved to csv
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                fahrtenbuch.exportFahrt();
            } catch (IOException e) {
                throw new InExportExc("An Error occurred during Export",e);
            }
        });


    }

    private void addToReoccurances(LocalDate date, Consumer<LocalDate> addFutureDate) {
        addFutureDate.accept(date);
    }

    private void addToKategories(String kate, Consumer<String> addKategorie) {
        addKategorie.accept(kate);
    }

    /**
     * Settings Screen including Category Management
     *
     * @param primaryStage
     */
    private void switchToSettings(Stage primaryStage) {
        ObservableList<String> addedCategories = FXCollections.observableArrayList();

        DirectoryChooser enterSavePath= new DirectoryChooser();
        enterSavePath.titleProperty().set("Path");
        enterSavePath.setInitialDirectory(new File(System.getProperty("user.home")));

        Button pfad = new Button("Import Data");
        pfad.setStyle("-fx-background-color: red;");
        pfad.setOnAction(event -> {
            File selectedFile=enterSavePath.showDialog(primaryStage);
            if (selectedFile != null) {


            Path importPath= Path.of(selectedFile.getAbsolutePath());
            fahrtenbuch.manualImport(importPath);
            }
        });
        Button exportButton = new Button();
        exportButton.setText("Export Data");
        exportButton.setStyle("-fx-background-color: green;");
        exportButton.setOnAction(event -> {
                    File selectedFile=enterSavePath.showDialog(primaryStage);
            if (selectedFile != null) {
                Path path= Path.of(selectedFile.getAbsolutePath());

            try {
                fahrtenbuch.exportManual(path);
            } catch (IOException e) {
                throw new InExportExc("An Error occurred during Export",e);
            }
            }
        });

        TextField kategorienInput = new TextField();
        kategorienInput.setPromptText("Kategorien eingeben:");
        kategorienInput.setMaxWidth(200);
        kategorienListe.clear();
        kategorienListe.addAll(fahrtenbuch.getKategorien(true));

        HBox angezeigteKategorien = new HBox();
        angezeigteKategorien.setSpacing(10);

        ObservableList<String> kategorienObservableList = FXCollections.observableArrayList(fahrtenbuch.getKategorien(true));
        ListView<String> angezeigteKategorienList = new ListView<>(kategorienObservableList);

        // Anfangs nicht sichtbar machen
        angezeigteKategorien.setPrefHeight(70); // Höhe der TextArea anpassen
        angezeigteKategorienList.setEditable(true);
        angezeigteKategorienList.setCellFactory(TextFieldListCell.forListView());
        angezeigteKategorienList.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                angezeigteKategorienList.getItems().set(t.getIndex(), t.getNewValue());

                //updating table in case the category of any Fahrt has changed
                fahrtenbuch.renameKategorie(t.getIndex(),t.getNewValue());
                fahrtenListe.clear();
                fahrtenListe = FXCollections.observableArrayList(fahrtenbuch.listeFahrten());
                fahrtenTabelle.setItems(fahrtenListe);
                fahrtenTabelle.refresh();

                kategorienListe.clear();
                kategorienListe = fahrtenbuch.getKategorien(true);
                angezeigteKategorienList.setVisible(true); // TextArea sichtbar machen
                angezeigteKategorienList.refresh();
            }
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(actionEvent -> {
            String selectedKategorie = angezeigteKategorienList.getSelectionModel().getSelectedItem();
            if(selectedKategorie != null && !selectedKategorie.isEmpty()) {
                boolean deleted = fahrtenbuch.deleteKategorie(selectedKategorie);
                if(deleted) {
                    // Entferne die Kategorie aus der ObservableList
                    kategorienObservableList.remove(selectedKategorie);
                }
            }
        });
        angezeigteKategorien.getChildren().addAll(angezeigteKategorienList,deleteBtn);

        Button kategorieHinzufuegenButton = new Button("Kategorie hinzufügen");
        kategorieHinzufuegenButton.setOnAction(event -> {
            String kategorie = kategorienInput.getText().trim();
            if (!kategorie.isEmpty()) {
                // Füge die neue Kategorie zur ObservableList hinzu
                kategorienObservableList.add(kategorie);
                kategorienInput.clear(); // Eingabefeld leeren
                fahrtenbuch.addKategories(FXCollections.observableArrayList(kategorie)); // Füge Kategorie zum Fahrtenbuch hinzu
            }
        });

        VBox kategorieInp = new VBox();
        kategorieInp.setSpacing(4);
        kategorieInp.getChildren().addAll(kategorienInput, kategorieHinzufuegenButton);

        Label tokenLabel = new Label("Enter Dropbox Access Token:");
        TextField tokenTextField = new TextField();
        tokenTextField.setStyle("-fx-text-fill: grey;");
        Button uploadButton = new Button("Upload to Dropbox");

        backButton = new Button();
        backButton.setText("<- Zurück");
        backButton.setOnAction(actionEvent -> {
            overview(primaryStage);
            primaryStage.hide();
        });

        tokenLabel.setAlignment(Pos.CENTER_LEFT); // Align the label to the left
        GridPane.setConstraints(tokenLabel, 0, 0);

        tokenTextField.setAlignment(Pos.CENTER_LEFT); // Align the text field to the left
        GridPane.setConstraints(tokenTextField, 1, 0);

        uploadButton.setAlignment(Pos.CENTER_LEFT); // Align the button to the left
        GridPane.setConstraints(uploadButton, 2, 0);


        uploadButton.setOnAction(event -> {
            String accessToken = tokenTextField.getText();
            Path path1 = Paths.get(System.getProperty("user.home"), "Documents", "Fahrtenbuch 0.0.3", "fahrten.csv");
            String fahrtenIn = path1.toString();
            Path path2 = Paths.get(System.getProperty("user.home"), "Documents", "Fahrtenbuch 0.0.3", "Kategorien.csv");
            String kategorienIn = path2.toString();
            String cloudPathFahrten = "/Apps/SEPR_Team_3/fahrten.csv";
            String cloudPathKategorien = "/Apps/SEPR_Team_3/kategorien.csv";
            try {
                CloudBackup.uploadDB(fahrtenIn, cloudPathFahrten, accessToken);
            } catch (CloudBackup.DropboxUploadException | CloudBackup.FileUploadException e) {
                throw new CloudBackup.CloudBackupException("Error uploading Fahrten", e);
            }

            try {
                CloudBackup.uploadDB(kategorienIn, cloudPathKategorien, accessToken);
            } catch (CloudBackup.DropboxUploadException | CloudBackup.FileUploadException e) {
                throw new CloudBackup.CloudBackupException("Error uploading Kategorien", e);
            }
        });

        primaryStage.setTitle("Einstellungen");
        GridPane gridSettings = new GridPane();
        gridSettings.setPadding(new Insets(10,10,10,10));
        gridSettings.setHgap(10);
        gridSettings.setVgap(10);
        gridSettings.getChildren().addAll(exportButton, backButton, pfad, angezeigteKategorien, kategorieInp,tokenLabel,tokenTextField,uploadButton);

        gridSettings.setAlignment(Pos.CENTER);
        GridPane.setConstraints(backButton, 0, 5);
        GridPane.setConstraints(pfad, 0, 1);
        GridPane.setConstraints(exportButton, 1, 1);
        GridPane.setConstraints(angezeigteKategorien, 1, 2);

        GridPane.setConstraints(kategorieInp, 0, 2);
        gridSettings.setGridLinesVisible(false);


        gridSettings.requestFocus();


        Scene einstellungen = new Scene(gridSettings, 720, 400);
        primaryStage.setScene(einstellungen);
        Platform.runLater(gridSettings::requestFocus);
        primaryStage.show();
        //when closing this stage data will be saved to csv
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                fahrtenbuch.exportFahrt();
            } catch (IOException e) {
                throw new InExportExc("An Error occurred during Export",e);
            }
        });
    }
    /**
     * Initialisiert den Menü-Button für Statistiken.
     * Diese Methode erstellt und konfiguriert den Menü-Button für den Zugriff auf verschiedene
     * statistische Ansichten. Dazu gehören Menüeinträge für die Jahresstatistik, erweiterte Statistik
     * und weitere statistische Auswertungen.
     */
    private void initializeStatistikMenuButton() {
        statistikMenuButton = new MenuButton("Statistik");

        MenuItem jahresStatistikItem = new MenuItem("Jahresstatistik anzeigen");
        jahresStatistikItem.setOnAction(event -> zeigeJahresKilometerStatistik());

        MenuItem erweiterteStatistikItem = new MenuItem("Erweiterte Statistik anzeigen");
        erweiterteStatistikItem.setOnAction(event -> zeigeErweiterteKilometerStatistik());

        statistikMenuButton.getItems().addAll(jahresStatistikItem, erweiterteStatistikItem);
    }
    /**
     * Initialisiert den Menü-Button für grafische Ansichten.
     * Diese Methode erstellt und konfiguriert den Menü-Button, der den Zugriff auf verschiedene
     * grafische Darstellungen der Statistiken ermöglicht. Dazu gehören Menüeinträge für die Anzeige
     * der Kilometer pro Monat und Kilometer pro Jahr.
     */
    private void initializeGrafikMenuButton() {
        grafikMenuButton = new MenuButton("Grafische Ansicht");

        MenuItem kilometerProMonatItem = new MenuItem("Kilometer pro Monat anzeigen");
        kilometerProMonatItem.setOnAction(event -> zeigeKilometerDiagramm());
        MenuItem kilometerProJahrItem = new MenuItem("Kilometer pro Jahr anzeigen");
        kilometerProJahrItem.setOnAction(event -> zeigeJahresKilometerDiagramm());

        grafikMenuButton.getItems().addAll(
                kilometerProMonatItem, kilometerProJahrItem

        );
    }
    /**
     * Zeigt ein Balkendiagramm mit den gefahrenen Kilometern pro Monat.
     * Diese Methode erstellt und zeigt ein Balkendiagramm, das die gefahrenen Kilometer pro Monat
     * und Kategorie anzeigt. Die Daten werden aus dem Fahrtenbuch bezogen und grafisch aufbereitet.
     */
    void zeigeKilometerDiagramm() {
        Stage stage = new Stage();
        stage.setTitle("Kilometerstatistik");

        // Vorbereitung der Achsen für das Diagramm
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Zeitraum");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Kilometer");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Gefahrene Kilometer Pro Monat und Jahr");

        // Daten für das Diagramm abrufen und hinzufügen
        Map<YearMonth, Map<String, Double>> kilometerProMonatUndKategorie = fahrtenbuch.berechneKilometerProMonatUndKategorie();

        // Für jede Kategorie eine Serie hinzufügen
        for (String kategorie : fahrtenbuch.getKategorien()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(kategorie);

            for (YearMonth ym : kilometerProMonatUndKategorie.keySet()) {
                String period = ym.getMonth().toString() + " " + ym.getYear();
                Double km = kilometerProMonatUndKategorie.get(ym).getOrDefault(kategorie, 0.0);
                series.getData().add(new XYChart.Data<>(period, km));
            }

            barChart.getData().add(series);
        }

        Scene scene = new Scene(barChart, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Zeigt ein Balkendiagramm mit den gesamten gefahrenen Kilometern pro Jahr.
     * Diese Methode generiert ein Balkendiagramm, das die jährliche Fahrleistung nach Kategorien
     * aufschlüsselt. Die Diagrammdaten werden aus dem Fahrtenbuch abgerufen und visuell dargestellt.
     */
    void zeigeJahresKilometerDiagramm() {
        Stage stage = new Stage();
        stage.setTitle("Jahreskilometerstatistik");

        // Vorbereitung der Achsen für das Diagramm
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Jahr");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Kilometer");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Gesamte Kilometer pro Jahr und Kategorie");

        // Daten für das Diagramm abrufen
        Map<Integer, Map<String, Double>> kilometerProJahrUndKategorie = fahrtenbuch.berechneKilometerProJahrUndKategorie();

        // Für jede Kategorie eine Serie hinzufügen
        for (String kategorie : fahrtenbuch.getKategorien()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(kategorie);

            for (Map.Entry<Integer, Map<String, Double>> entry : kilometerProJahrUndKategorie.entrySet()) {
                String year = Integer.toString(entry.getKey());
                Double km = entry.getValue().getOrDefault(kategorie, 0.0);
                series.getData().add(new XYChart.Data<>(year, km));
            }

            barChart.getData().add(series);
        }

        Scene scene = new Scene(barChart, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Erstellt eine TableView für die Darstellung von Jahreskilometern pro Kategorie.
     *
     * @param kategorien Eine Menge von Kategorienamen, für die Spalten erstellt werden sollen.
     * @return Eine TableView, die Map-Einträge von Jahreszahlen zu Karten von Kategorien und Kilometern darstellt.
     */
    private TableView<Map.Entry<Integer, Map<String, Double>>> erstelleJahresKilometerTableView(Set<String> kategorien) {
        TableView<Map.Entry<Integer, Map<String, Double>>> tableView = new TableView<>();

        TableColumn<Map.Entry<Integer, Map<String, Double>>, Integer> yearColumn = new TableColumn<>("Jahr");
        yearColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getKey()));
        tableView.getColumns().add(yearColumn);

        // Spalte für die Gesamtkilometer pro Jahr hinzufügen
        TableColumn<Map.Entry<Integer, Map<String, Double>>, Double> gesamtKilometerColumn = new TableColumn<>("Gesamtkilometer");
        gesamtKilometerColumn.setCellValueFactory(cellData -> {
            double gesamtKilometer = cellData.getValue().getValue().values().stream().mapToDouble(Double::doubleValue).sum();
            return new SimpleObjectProperty<>(gesamtKilometer);
        });
        tableView.getColumns().add(gesamtKilometerColumn);

        for (String kategorie : kategorien) {
            TableColumn<Map.Entry<Integer, Map<String, Double>>, Double> categoryColumn = new TableColumn<>(kategorie);
            categoryColumn.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getValue().getOrDefault(kategorie, 0.0)));
            tableView.getColumns().add(categoryColumn);
        }

        return tableView;
    }

    /**
     * Zeigt eine Statistik der gefahrenen Kilometer pro Monat und Kategorie in einer TableView.
     */
    void zeigeErweiterteKilometerStatistik() {
        Stage stage = new Stage();
        stage.setTitle("Erweiterte Kilometerstatistik");

        // Kategorien aus dem Fahrtenbuch holen
        Set<String> kategorien = fahrtenbuch.getKategorien();

        TableView<Map.Entry<YearMonth, Map<String, Double>>> tableView = erstelleErweiterteKilometerTableView(kategorien);
        aktualisiereErweiterteKilometerTabelle(tableView);

        Scene scene = new Scene(tableView, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Erstellt eine TableView für die detaillierte Darstellung von Kilometern pro Monat und Kategorie.
     *
     * @param kategorien Eine Menge von Kategorienamen, für die Spalten erstellt werden sollen.
     * @return Eine TableView, die Map-Einträge von YearMonth zu Karten von Kategorien und Kilometern darstellt.
     */
    private TableView<Map.Entry<YearMonth, Map<String, Double>>> erstelleErweiterteKilometerTableView(Set<String> kategorien) {
        TableView<Map.Entry<YearMonth, Map<String, Double>>> tableView = new TableView<>();

        TableColumn<Map.Entry<YearMonth, Map<String, Double>>, String> monthColumn = new TableColumn<>("Monat");
        monthColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKey().toString()));
        tableView.getColumns().add(monthColumn);

        // Spalte für die Gesamtkilometer hinzufügen
        TableColumn<Map.Entry<YearMonth, Map<String, Double>>, Double> gesamtKilometerColumn = new TableColumn<>("Gesamtkilometer");
        gesamtKilometerColumn.setCellValueFactory(cellData -> {
            double gesamtKilometer = cellData.getValue().getValue().values().stream().mapToDouble(Double::doubleValue).sum();
            return new SimpleObjectProperty<>(gesamtKilometer);
        });
        tableView.getColumns().add(gesamtKilometerColumn);

        for (String kategorie : kategorien) {
            TableColumn<Map.Entry<YearMonth, Map<String, Double>>, Double> categoryColumn = new TableColumn<>(kategorie);
            categoryColumn.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getValue().getOrDefault(kategorie, 0.0)));
            tableView.getColumns().add(categoryColumn);
        }

        return tableView;
    }

    /**
     * Aktualisiert die Tabelle der erweiterten Kilometerstatistik mit den neuesten Daten.
     *
     * @param tableView Die TableView, die aktualisiert werden soll.
     */
    private void aktualisiereErweiterteKilometerTabelle(TableView<Map.Entry<YearMonth, Map<String, Double>>> tableView) {
        Map<YearMonth, Map<String, Double>> data = fahrtenbuch.berechneKilometerProMonatUndKategorie();
        ObservableList<Map.Entry<YearMonth, Map<String, Double>>> tableData = FXCollections.observableArrayList(data.entrySet());
        tableView.setItems(tableData);
    }

    /**
     * Zeigt eine Statistik der gefahrenen Kilometer pro Jahr und Kategorie in einer TableView.
     */
    void zeigeJahresKilometerStatistik() {
        Set<String> kategorien = fahrtenbuch.getKategorien(); // Angenommen, diese Methode gibt die Kategorien zurück
        Map<Integer, Map<String, Double>> data = fahrtenbuch.berechneKilometerProJahrUndKategorie();

        if (data.isEmpty()) {
            LOGGER.info("Keine Daten für die Jahresstatistik vorhanden.");
        } else {
            TableView<Map.Entry<Integer, Map<String, Double>>> tableView = erstelleJahresKilometerTableView(kategorien);
            ObservableList<Map.Entry<Integer, Map<String, Double>>> tableData = FXCollections.observableArrayList(data.entrySet());
            tableView.setItems(tableData); // Hier wird die TableView mit Daten gefüllt
            tableView.refresh(); // Stellen Sie sicher, dass die TableView aktualisiert wird

            Stage stage = new Stage();
            stage.setTitle("Jahreskilometerstatistik");
            Scene scene = new Scene(tableView, 800, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Öffnet ein Dialogfenster zur Filterung von Fahrten nach verschiedenen Kriterien.
     *
     * @param  date
     *
     */
    private List<Fahrt> filterFahrten(LocalDate date, List<String> selectedCategories, double avg, boolean avgUnder, boolean avgOver) {
        List<Fahrt> filteredFahrten = fahrtenListe;

        // Filter by date
        if (date != null) {
            filteredFahrten = filteredFahrten.stream()
                    .filter(f -> f.getDatum().getYear() == date.getYear() &&
                            f.getDatum().getMonth() == date.getMonth() &&
                            f.getDatum().getDayOfMonth() == date.getDayOfMonth())
                    .collect(Collectors.toList());
        }

        // Filter by selected categories
        if (!selectedCategories.isEmpty()) {
            filteredFahrten = filteredFahrten.stream()
                    .filter(f -> f.getKategorien().containsAll(selectedCategories))
                    .collect(Collectors.toList());
        }

        // Filter by average speed
        if (avg >= 0.0) {
            filteredFahrten = filteredFahrten.stream()
                    .filter(f -> {
                        double gefahreneKilometer = f.getGefahreneKilometer();
                        LocalTime aktiveFahrzeit = f.getAktiveFahrzeit();
                        double time = aktiveFahrzeit.getHour() + (aktiveFahrzeit.getMinute() / 60.0);
                        double instanceAvg = gefahreneKilometer / time;

                        return (!avgUnder || instanceAvg < avg)
                                && (!avgOver || instanceAvg >= avg);
                    })
                    .collect(Collectors.toList());
        }

        return filteredFahrten;
    }

    private void oeffneFilter(TableView fahrtenTabelle) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Fahrt filtern");
        ButtonType filterButtonType = new ButtonType("Filtern", ButtonBar.ButtonData.OK_DONE);
        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(filterButtonType, resetButtonType, ButtonType.CANCEL);
        Label datumL = new Label("Datum");
        DatePicker datum = new DatePicker();
        datum.setPromptText("Datum der Fahrt");
        datum.getEditor().setDisable(true);
        datum.setMaxWidth(200);
        Label avgLabel = new Label("Avg V");
        TextField avgTF = new TextField();
        avgTF.setPromptText("avg V eingeben");
        avgTF.setMaxWidth(200);
        Label categoryLabel = new Label("Kategorie");
        categoryLabel.setMaxWidth(200);

        final CheckComboBox<String> categoryFilter = new CheckComboBox<>(fahrtenbuch.getKategorien(true));
        CheckBox avgUnderCheckBox = new CheckBox("Unterhalb des Durchschnitts");
        CheckBox avgOverCheckBox = new CheckBox("Über oder gleich dem Durchschnitt");

        HBox datumBox = new HBox(5);
        datumBox.getChildren().addAll(datumL, datum);

        HBox categoryBox = new HBox(5);
        categoryBox.getChildren().addAll(categoryLabel, categoryFilter);

        HBox avgBox = new HBox(5);
        avgBox.getChildren().addAll(avgLabel, avgTF, avgUnderCheckBox, avgOverCheckBox);

        VBox fahrtTextinputboxen = new VBox(10);
        fahrtTextinputboxen.getChildren().addAll(datumBox, categoryBox, avgBox);

        ScrollPane scrollPane = new ScrollPane(fahrtTextinputboxen);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        StackPane layoutFilter = new StackPane();
        layoutFilter.getChildren().add(scrollPane);
        dialog.getDialogPane().setContent(layoutFilter);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filterButtonType) {
                try {
                    LocalDate date = datum.getValue();
                    List<String> selectedCategories = categoryFilter.getCheckModel().getCheckedItems().stream().toList();
                    double avg = avgTF.getText().isEmpty() ? -1.0 : Double.parseDouble(avgTF.getText());

                    List<Fahrt> filteredFahrten = filterFahrten(date, selectedCategories, avg, avgUnderCheckBox.isSelected(), avgOverCheckBox.isSelected());

                    fahrtenTabelle.setItems(FXCollections.observableArrayList(filteredFahrten));
                } catch (DateTimeParseException | NumberFormatException e) {
                    showAlert("Wrong Format!");
                }
                return true;
            } else if (dialogButton == resetButtonType) {
                // Reset button is pressed, clear filters and reset the table
                fahrtenTabelle.setItems(FXCollections.observableArrayList(fahrtenListe));
                return true;
            }
            return false;
        });

        Optional<Boolean> result = dialog.showAndWait();
        result.ifPresent(filter -> {
            fahrtenTabelle.refresh();
            dialog.close();
        });
    }

    private void showAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public class InExportExc extends RuntimeException{
        public InExportExc(String msg, Throwable cause){
            super(msg,cause);
        }
    }

    private void openAboutPage() {
        Stage aboutStage = new Stage();
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.setTitle("About Page");

        VBox aboutLayout = new VBox(10);

        Label appInfo = new Label("Fahrtenbuch      Version: 0.3.0");

        Label contributorsLabel = new Label("Contributors: \n1. Hannes Mayrhofer\n2. Markus Lindner\n3. Milan Keta\n4. Sara Kheribi");

        Label professorLabel = new Label("Professor: Dr. Johannes Bräuer");

        Label subjectLabel = new Label("LVA: Software Engineering PR");

        Label universityLabel = new Label("Universität: JKU");

        Label locationTime = new Label("Jänner 2023.           Linz,Austria");

        Image logo;
        InputStream imageStream = getClass().getResourceAsStream("/logo.png");

        if (imageStream != null) {
            logo = new Image(imageStream);
        } else {
            logo = new WritableImage(100, 100);
        }

        double targetWidth = 150;
        double targetHeight = 100;
        ImageView logoImageView = new ImageView(logo);
        logoImageView.setFitWidth(targetWidth);
        logoImageView.setFitHeight(targetHeight);

        aboutLayout.getChildren().addAll(logoImageView, appInfo,contributorsLabel, professorLabel, subjectLabel, universityLabel, locationTime);
        aboutLayout.setAlignment(Pos.CENTER);

        Scene aboutScene = new Scene(aboutLayout, 300, 400);
        aboutStage.setScene(aboutScene);
        aboutStage.showAndWait();
    }

        }
