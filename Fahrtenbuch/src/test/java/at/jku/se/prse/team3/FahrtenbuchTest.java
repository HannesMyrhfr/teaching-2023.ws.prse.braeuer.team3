package at.jku.se.prse.team3;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import static org.junit.Assert.*;

public class FahrtenbuchTest {

    private Fahrtenbuch fahrtenbuch;

    @Before
    public void setUp() {
        fahrtenbuch = new Fahrtenbuch();
    }

    @Test
    public void testNeueFahrt() throws IOException {
        fahrtenbuch.neueFahrt("AB-1234", LocalDate.now(), LocalTime.now(), LocalTime.now(), 100.0, LocalTime.now(), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));
        assertEquals("Anzahl der Fahrten sollte 1 sein", 1, fahrtenbuch.listeFahrten().size());
    }

    @Test
    public void testBearbeiteFahrt() throws IOException {
        String kfzKennzeichen = "AB-1234";
        LocalDate datum = LocalDate.now();
        LocalTime abfahrtszeit = LocalTime.now();
        fahrtenbuch.neueFahrt(kfzKennzeichen, datum, abfahrtszeit, abfahrtszeit.plusHours(1), 100.0, LocalTime.of(1, 0), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));
        fahrtenbuch.bearbeiteFahrt(kfzKennzeichen, datum, abfahrtszeit, abfahrtszeit.plusHours(2), 200.0, LocalTime.of(2, 0));
        Fahrt fahrt = fahrtenbuch.listeFahrten().get(0);
        assertEquals("Gefahrene Kilometer sollten 200 sein", 200.0, fahrt.getGefahreneKilometer(), 0.0);
        assertEquals("Ankunftszeit sollte 2 Stunden nach Abfahrt sein", abfahrtszeit.plusHours(2), fahrt.getAnkunftszeit());
    }

    @Test
    public void testLoescheFahrten() throws IOException {
        String kfzKennzeichen = "AB-1234";
        LocalDate datum = LocalDate.now();
        LocalTime abfahrtszeit = LocalTime.now();
        fahrtenbuch.neueFahrt(kfzKennzeichen, datum, abfahrtszeit, abfahrtszeit.plusHours(1), 100.0, LocalTime.of(1, 0), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));
        fahrtenbuch.loescheFahrten(kfzKennzeichen, datum, abfahrtszeit);
        assertTrue("Fahrtenbuch sollte leer sein", fahrtenbuch.listeFahrten().isEmpty());
    }

    @Test
    public void testExportImportFahrt() throws IOException, CsvValidationException {
        fahrtenbuch.neueFahrt("AB-1234", LocalDate.now(), LocalTime.now(), LocalTime.now(), 100.0, LocalTime.now(), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));
        fahrtenbuch.exportFahrt();
        fahrtenbuch = new Fahrtenbuch(); // Erstellt ein neues Fahrtenbuch
        fahrtenbuch.importFahrt(); // Importiert die Daten
        assertEquals("Anzahl der importierten Fahrten soll 1 sein", 1, fahrtenbuch.listeFahrten().size());
    }

    @Test
    public void exportManual() throws IOException {
        fahrtenbuch.neueFahrt("AB-1234", LocalDate.now(), LocalTime.now(), LocalTime.now(), 100.0, LocalTime.now(), FahrtStatus.ABSOLVIERT, Arrays.asList("Geschäftlich"));

        fahrtenbuch.exportManual(Path.of(System.getProperty("user.home")+ File.separator + "Documents"));
    }



    @Test
    public void manualImport() {
        fahrtenbuch.manualImport(Path.of(System.getProperty("user.home")+ File.separator + "Documents"+File.separator+"Fahrtenbuch 0.0.3"));
    }
}
