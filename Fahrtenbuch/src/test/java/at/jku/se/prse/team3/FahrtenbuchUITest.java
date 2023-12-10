package at.jku.se.prse.team3;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.*;
import static org.testfx.assertions.api.Assertions.assertThat;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;


// TODO: bugfix clickOn("fahrtenTabelle") funktioniert irgendwie nicht => es selektiert keine Zeile
public class FahrtenbuchUITest extends ApplicationTest {
    Fahrtenbuch fahrtenbuch = new Fahrtenbuch();
    FahrtenbuchUI app;

    @Override
    public void start(Stage stage) {
        try {
            fahrtenbuch.neueFahrt("Lv-254", LocalDate.now(), LocalTime.now(), LocalTime.now(), 10d, LocalTime.now(), FahrtStatus.ABSOLVIERT, Arrays.asList(""));
            fahrtenbuch.neueFahrt("GB-214", LocalDate.now(), LocalTime.now(), LocalTime.now(), 10d, LocalTime.now(), FahrtStatus.ABSOLVIERT, Arrays.asList(""));
        } catch (IOException e) {
            System.out.println("FahrtenbuchUITest: Konnte keine neue Fahrten zum Testen eintragen:" + e);
        }
        app = new FahrtenbuchUI(fahrtenbuch);
        app.start(stage);
    }


    @Test
    public void testEditButton() {
        verifyThat("#fahrtenTabelle", NodeMatchers.isVisible());
        clickOn("#fahrtenTabelle");

        TableView tableView = lookup("#fahrtenTabelle").queryAs(TableView.class);

        //clickOn(".table-row").clickOn(MouseButton.PRIMARY);
        if (!tableView.getSelectionModel().isEmpty()) {
            Fahrt selectedFahrt = (Fahrt) tableView.getSelectionModel().getSelectedItem();
            System.out.println("Fahrt ausgewählt:" + selectedFahrt.getKfzKennzeichen());

            clickOn("#editButton");
            verifyThat("#kfzKennzeichenEditField", TextInputControlMatchers.hasText(selectedFahrt.getKfzKennzeichen()));

        } else {
            System.out.println("Keine Fahrt ausgewählt.");
        }
    }

    /*@Test
    public void testAddNewTripDialog() {
        clickOn("#newTripButton");
        verifyThat("#saveButton", NodeMatchers.isVisible());

        //TODO befülle alle felder für eine neue Fahrt
        TextField kfzField = lookup("#kfzKennzeichenAddField").query();
        clickOn(kfzField);
        write("LVA-111");

        //klicke dann auf Speichern Button
        //  clickOn("#saveButton");

        //Überprüfe danach, ob die neue Fahrt tatsächlich, in der Tabelle erscheint
        TableView tableView = lookup("#fahrtenTabelle").queryAs(TableView.class);
        ObservableList<Fahrt> items = tableView.getItems();
        boolean neueFahrtVorhanden = items.stream()
                .anyMatch(fahrt -> fahrt.getKfzKennzeichen().equals("LVA-111"));

        //Test schlägt dann grün falls die neue Fahrt in der Tabelle gefunden wurde
        assertTrue(neueFahrtVorhanden);
    }
*/
    @Test
    public void testDeleteButton() {
        clickOn("#fahrtenTabelle");
        TableView tableView = lookup("#fahrtenTabelle").queryAs(TableView.class);
        if (!tableView.getSelectionModel().isEmpty()) {
            Fahrt ausgewaehlterFahrt = (Fahrt) tableView.getSelectionModel().getSelectedItem();
            clickOn("#editButton");
            clickOn("#deleteButton");
            verifyThat("Löschen bestätigen", NodeMatchers.isVisible());
            clickOn("Löschen");

            ObservableList<Fahrt> fahrten = tableView.getItems();
            boolean fahrtNichtVorhanden = fahrten.stream().noneMatch(fahrt ->
                            fahrt.getKfzKennzeichen().equals(ausgewaehlterFahrt.getKfzKennzeichen())
            );
            assertTrue(fahrtNichtVorhanden);
        } else {
            System.out.println("Keine Fahrt ausgewählt.");
        }
    }

    @Test
    public void testSettingsButton() {
        clickOn("#settingsButton");
    }

    @Test
    public void testBerechneKilometerProMonatUndKategorie() throws IOException {
        // Arrange
        Fahrtenbuch fahrtenbuch = new Fahrtenbuch();
        fahrtenbuch.neueFahrt("KFZ-123", LocalDate.of(2023, 1, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), 100.0, LocalTime.of(1, 0), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));
        fahrtenbuch.neueFahrt("KFZ-123", LocalDate.of(2023, 2, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), 150.0, LocalTime.of(1, 0), FahrtStatus.ABSOLVIERT, Arrays.asList("Privat"));

        // Act
        Map<YearMonth, Map<String, Double>> result = fahrtenbuch.berechneKilometerProMonatUndKategorie();

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey(YearMonth.of(2023, 1)));
        assertTrue(result.containsKey(YearMonth.of(2023, 2)));
        assertEquals(100.0, result.get(YearMonth.of(2023, 1)).get("Geschäftlich"), 0.01);
        assertEquals(150.0, result.get(YearMonth.of(2023, 2)).get("Privat"), 0.01);
    }

    @Test
    public void testZeigeKilometerDiagramm() {

        Platform.runLater(() -> app.zeigeKilometerDiagramm());


        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup(".bar-chart").queryAs(BarChart.class));
    }

    @Test
    public void testZeigeJahresKilometerDiagramm() {

        Platform.runLater(() -> app.zeigeJahresKilometerDiagramm());

        // Assert
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup(".bar-chart").queryAs(BarChart.class));
    }

    @Test
    public void testZeigeErweiterteKilometerStatistik() {

        Platform.runLater(() -> app.zeigeErweiterteKilometerStatistik());

        // Assert
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup(".table-view").queryAs(TableView.class));
    }
    @Test
    public void testZeigeJahresKilometerStatistik() {

        Platform.runLater(() -> app.zeigeJahresKilometerStatistik());

        // Assert
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup(".table-view").queryAs(TableView.class));
    }


}

