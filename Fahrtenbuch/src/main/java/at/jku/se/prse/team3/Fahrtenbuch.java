package at.jku.se.prse.team3;
import java.util.logging.Logger;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class Fahrtenbuch {
    private static final Logger LOGGER=Logger.getLogger(Fahrtenbuch.class.getName());
    Path path = Paths.get(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Fahrtenbuch 0.0.3");

    private List<String> kategorien;
    private List<Fahrt> fahrten;

    /**
     * Filled Constructor
     *
     * @param kategorien List of available Categories
     * @param fahrten    List of available Driving records
     */
    public Fahrtenbuch(List<String> kategorien, List<Fahrt> fahrten) {
        this.kategorien = new ArrayList<>(kategorien);
        this.fahrten = new ArrayList<>(fahrten);
    }

    /**
     * empty Constructor in Case first start
     */
    public Fahrtenbuch() {
        kategorien = new ArrayList<>();
        fahrten = new ArrayList<>();
    }

    /**
     * Method to crete a new Fahrt Issue #1
     *
     * @param kfzKennzeichen         kfzKennzeichen
     * @param datum                  datum
     * @param abfahrtszeit           abfahrtszeit
     * @param neueAnkunftszeit       neueAnkunftszeit
     * @param neueGefahreneKilometer neueGefahreneKilometer
     * @param neueAktiveFahrzeit     neueAktiveFahrzeit
     * @param fahrtStatus            fahrtStatus
     * @param category               category
     * @throws IOException
     */

    public void neueFahrt(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit,
                          LocalTime neueAnkunftszeit, Double neueGefahreneKilometer,
                          LocalTime neueAktiveFahrzeit, FahrtStatus fahrtStatus, List<String> category) throws IOException {
        fahrten.add(new Fahrt(kfzKennzeichen, datum, abfahrtszeit, neueAnkunftszeit, neueGefahreneKilometer, neueAktiveFahrzeit, category, fahrtStatus));

    }


    /**
     * Bearbeitungsvorgang einer Fahrt Issue #3
     *
     * @param kfzKennzeichen
     * @param datum
     * @param abfahrtszeit
     * @param neueAnkunftszeit
     * @param neueGefahreneKilometer
     * @param neueAktiveFahrzeit
     */
    public void bearbeiteFahrt(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit,
                               LocalTime neueAnkunftszeit, Double neueGefahreneKilometer,
                               LocalTime neueAktiveFahrzeit) {
        for (Fahrt fahrt : fahrten) {
            // Finde die spezifische Fahrt basierend auf dem Schlüssel
            if (fahrt.getKfzKennzeichen().equals(kfzKennzeichen) &&
                    fahrt.getDatum().equals(datum) &&
                    fahrt.getAbfahrtszeit().equals(abfahrtszeit)) {

                // Update der Eigenschaften der gefundenen Fahrt
                fahrt.setAnkunftszeit(neueAnkunftszeit);
                fahrt.setGefahreneKilometer(neueGefahreneKilometer);
                fahrt.setAktiveFahrzeit(neueAktiveFahrzeit);

                // Breche die Schleife ab, da die Fahrt gefunden und bearbeitet wurde
                break;
            }
        }
    }

    /**
     * Hilffunktion zum Hionzufügen einer Kategorie
     *
     * @param kategorie
     */
    public void addKategorie(String kategorie) {
        kategorien.add(kategorie);
    }

    public boolean deleteKategorie(String kategorie){
        boolean found = false;
        for(Fahrt f:fahrten){
            if(f.hasKategorie(kategorie)){
                found = true;
            }
        }

        if(found){
            System.out.println("Kann Kategorie "+ kategorie + " nicht löschen, da sie in einer Fahrt verwendet wird.");
            return false;
        }else{
            return this.kategorien.remove(kategorie);
        }
    }

    public void renameKategorie(int index, String newName){
        fahrten.forEach(f->{
            if(f.hasKategorie(kategorien.get(index))){
                f.renameKategorie(kategorien.get(index),newName);
            }
        });
        kategorien.set(index, newName);
    }
    /**
     * Fahrt löschen
     *
     * @param kfzKennzeichen
     * @param datum
     * @param abfahrtszeit
     */

    public void loescheFahrten(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit) {
        fahrten.removeIf(fahrt ->
                fahrt.getKfzKennzeichen().equals(kfzKennzeichen) &&
                        fahrt.getDatum().equals(datum) &&
                        fahrt.getAbfahrtszeit().equals(abfahrtszeit));

    }

    /**
     * Issue #5 wiederkehrende Fahrten
     *
     * @param reoccurances
     * @param kfzKennzeichen
     * @param abfahrtszeit
     * @param fahrtKategorie
     * @throws IOException
     */
    public void planeZukuenftigeFahrten(List<LocalDate> reoccurances,
                                        String kfzKennzeichen,
                                        LocalTime abfahrtszeit,
                                        List<String> fahrtKategorie) throws IOException {

        for (LocalDate d : reoccurances
        ) {
            LocalTime neueAnkunftszeit = LocalTime.of(0, 0, 0);
            double neueGefahreneKilometer = 0;
            LocalTime neueAktiveFahrzeit = LocalTime.of(0, 0, 0);

            fahrten.add(new Fahrt(
                    kfzKennzeichen,
                    d,
                    abfahrtszeit,
                    neueAnkunftszeit,
                    neueGefahreneKilometer,
                    neueAktiveFahrzeit,
                    fahrtKategorie,
                    FahrtStatus.ZUKUENFTIG));
        }

        exportFahrt();


    }

    /**
     * returns all entered Fahrten
     *
     * @return
     */
    public List<Fahrt> listeFahrten() {


        return fahrten;
    }

    /**
     * Filtert Fahrten anhand spezifischer Kriterien.
     *
     * @return Eine Liste gefilterter Fahrten.
     */
    public List<Fahrt> filtereFahrten() {

        //List<Fahrt> fahrten = new ArrayList<Fahrt>();

        return fahrten;
    }

    /**
     * Sortiert die Fahrten im Fahrtenbuch.
     */
    public void sortiereFahrten() {
        fahrten.sort(Comparator.comparing(Fahrt::getDatum)
                .thenComparing(Fahrt::getAbfahrtszeit));
    }

    /**
     * Ermittelt den Status einer spezifischen Fahrt.
     *
     * @param fahrt Die Fahrt, für die der Status ermittelt werden soll.
     * @return Der Status der Fahrt.
     */
    public FahrtStatus getFahrtstatus(Fahrt fahrt) {
        return fahrt.getFahrtstatus();
    }

    /**
     * Exportiert die Fahrtendaten in eine CSV-Datei.
     *
     * @throws IOException Wenn beim Schreiben der Datei ein Fehler auftritt.
     */
    public void exportFahrt() throws IOException {
        //export Fahrten&Kategorien as CSV.
        String realExport = path.toString();

        exporterMethod(realExport);
    }
    public void exportManual(Path manualExpPath) throws IOException {
        String manualExport=manualExpPath.toString();
        exporterMethod(manualExport);
    }

    private void exporterMethod(String exportPath) throws IOException {
        String exportFahrten = exportPath + File.separator + "fahrten.csv";
        String exportKategorien = exportPath + File.separator + "Kategorien.csv";
        CSVWriter csvWriter = new CSVWriter(new FileWriter(exportFahrten));


        for (Fahrt f : fahrten
        ) {
            String[] data = {f.getKfzKennzeichen(),
                    String.valueOf(f.getDatum()),
                    String.valueOf(f.getAbfahrtszeit()),
                    String.valueOf(f.getAnkunftszeit()),
                    String.valueOf(f.getGefahreneKilometer()),
                    String.valueOf(f.getAktiveFahrzeit()),
                    String.valueOf(f.getFahrtstatus()),
                    f.getKategorien().toString().replace("[", "").replace("]", "")};

            csvWriter.writeNext(data);
        }
        csvWriter.close();
        CSVWriter csvWriter2 = new CSVWriter(new FileWriter(exportKategorien));
        for (String k : kategorien
        ) {
            String[] data = {k};
            csvWriter2.writeNext(data);
        }

        csvWriter2.close();

    }

    /**
     * Importiert Fahrtendaten aus einer CSV-Datei.
     *
     * @throws IOException Wenn beim Lesen der Datei ein Fehler auftritt.
     * @throws CsvValidationException Wenn die CSV-Daten ungültig sind.
     */
    public void importFahrt() throws IOException, CsvValidationException {
        //export Fahrten&Kategorien as CSV.

        path = Paths.get(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Fahrtenbuch 0.0.3");
        Path realImport = path;
        importerMethod(realImport);

    }

    public void manualImport(Path path){
        importerMethod(path);

    }


    private void importerMethod(Path importPath){
        fahrten.clear();
        kategorien.clear();
        Path importFahrten = Paths.get(importPath + File.separator + "fahrten.csv");

        Path importKategorien = Paths.get(importPath + File.separator + "Kategorien.csv");
        try {

            Files.createDirectory(Path.of(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Fahrtenbuch 0.0.3"));
            LOGGER.info("Neues System...");
            try (CSVWriter writer = new CSVWriter(new FileWriter(importFahrten.toFile()))) {
            }
            try (CSVWriter writer2 = new CSVWriter(new FileWriter(importKategorien.toFile()))) {
            }
            LOGGER.info("Initialisiere Datensätze...");
        } catch (FileAlreadyExistsException f) {
           LOGGER.info("Willkommen zurück!");

            String[] data;
            String kFZKennzeichen;
            LocalDate datum;
            LocalTime abfahrtszeit;
            LocalTime ankunftszeit;
            Double gefahreneKilometer;
            LocalTime aktiveFahrzeit;
            List<String> kategorien;
            FahrtStatus fahrtstatus;


            try (CSVReader reader = new CSVReader(new FileReader(importFahrten.toFile()))) {

                while ((data = reader.readNext()) != null) {


                    kFZKennzeichen = data[0];


                    datum = LocalDate.parse(data[1]);


                    abfahrtszeit = LocalTime.parse(data[2]);


                    ankunftszeit = LocalTime.parse(data[3]);
                    gefahreneKilometer = Double.valueOf(data[4]);
                    aktiveFahrzeit = LocalTime.parse(data[5]);
                    if (FahrtStatus.ZUKUENFTIG.toString().equals(data[6])) fahrtstatus = FahrtStatus.ZUKUENFTIG;
                    else if (FahrtStatus.ABSOLVIERT.toString().equals(data[6])) fahrtstatus = FahrtStatus.ABSOLVIERT;
                    else fahrtstatus = FahrtStatus.AUF_FAHRT;
                    kategorien = Arrays.asList(data[7]);
                    neueFahrt(kFZKennzeichen, datum, abfahrtszeit, ankunftszeit, gefahreneKilometer, aktiveFahrzeit, fahrtstatus, kategorien);

                }

            } catch (NullPointerException | FileNotFoundException nullPointerException) {


            } catch (IOException | CsvValidationException e) {
                throw new InExportExc("An unexpected Error occured during Import",e);
            }

            try (CSVReader reader2 = new CSVReader(new FileReader(importKategorien.toFile()))) {
                List<String[]> allKat = reader2.readAll();
                for (String[] d : allKat) {
                    for (String cat : d) {
                        this.kategorien.add(cat.trim());
                    }
                }
            } catch (CsvException | FileNotFoundException e) {
                throw new InExportExc("An Error occured during Import",e);
            } catch (IOException e) {
                throw new InExportExc("An unexpected Error occured during Import",e);
            }


        } catch (IOException e) {
            throw new InExportExc("An unexpected Error occured during Import",e);
        }

    }

    /**
     * Berechnet die insgesamt gefahrenen Kilometer pro Monat.
     *
     * @return Eine Map, die jedem Monat (YearMonth) die insgesamt gefahrenen Kilometer zuordnet.
     */
    public Map<YearMonth, Double> berechneKilometerProMonat() {
        Map<YearMonth, Double> kilometerProMonat = new TreeMap<>();
        for (Fahrt fahrt : fahrten) {
            YearMonth yearMonth = YearMonth.from(fahrt.getDatum());
            kilometerProMonat.merge(yearMonth, fahrt.getGefahreneKilometer(), Double::sum);
        }
        return kilometerProMonat;
    }


    /**
     * Berechnet die gefahrenen Kilometer pro Monat und Kategorie.
     *
     * @return Eine Map, die jedem Monat (YearMonth) eine Map von Kategorien und den jeweiligen Kilometern zuordnet.
     */
    public Map<YearMonth, Map<String, Double>> berechneKilometerProMonatUndKategorie() {
        Map<YearMonth, Map<String, Double>> kilometerProMonatUndKategorie = new TreeMap<>();

        for (Fahrt fahrt : fahrten) {
            YearMonth yearMonth = YearMonth.from(fahrt.getDatum());
            List<String> fahrtKategorien = fahrt.getKategorien();

            fahrtKategorien.forEach(kategorie -> {
                kilometerProMonatUndKategorie
                        .computeIfAbsent(yearMonth, k -> new HashMap<>())
                        .merge(kategorie, fahrt.getGefahreneKilometer(), Double::sum);
            });
        }
        return kilometerProMonatUndKategorie;
    }


    /**
     * Berechnet die gefahrenen Kilometer pro Jahr und Kategorie.
     *
     * @return Eine Map, die jedem Jahr eine Map von Kategorien und den jeweiligen Kilometern zuordnet.
     */
    public Map<Integer, Map<String, Double>> berechneKilometerProJahrUndKategorie() {
        Map<Integer, Map<String, Double>> kilometerProJahrUndKategorie = new TreeMap<>();

        for (Fahrt fahrt : fahrten) {
            int jahr = fahrt.getDatum().getYear();
            List<String> fahrtKategorien = fahrt.getKategorien();

            fahrtKategorien.forEach(kategorie -> {
                kilometerProJahrUndKategorie
                        .computeIfAbsent(jahr, k -> new HashMap<>())
                        .merge(kategorie, fahrt.getGefahreneKilometer(), Double::sum);
            });
        }
        return kilometerProJahrUndKategorie;
    }

    /**
     * Gibt eine Liste aller einzigartigen Kategorien zurück.
     *
     * @return Ein Set mit einzigartigen Kategorien.
     */
    public Set<String> getKategorien() {
        Set<String> uniqueKategorien = new HashSet<>();
        for (Fahrt fahrt : fahrten) {
            uniqueKategorien.addAll(fahrt.getKategorien());
        }
        return uniqueKategorien;
    }

    /**
     * Gibt alle Kategorien als ObservableList zurück.
     * Diese Methode bietet die gleiche Funktionalität wie getKategorien(), gibt jedoch eine ObservableList zurück.
     *
     * @param x Aktiviert die Methode, wenn true. Kann true oder false sein.
     * @return Eine ObservableList aller Kategorien.
     */
    public ObservableList<String> getKategorien(Boolean x) {
        ObservableList<String> uniqueKategorien = FXCollections.observableArrayList();


        uniqueKategorien.addAll(kategorien);

        return uniqueKategorien;
    }

    /**
     * Fügt eine Sammlung von Kategorien zur Liste der Kategorien in der Logikklasse hinzu.
     *
     * @param kategorienNeu Eine Sammlung von Kategorien.
     */
    public void addKategories(Collection kategorienNeu) {
        kategorien.addAll(kategorienNeu);
    }

    /**
     * Filtert Fahrten nach einem spezifischen Datum.
     *
     * @param date Das Datum, nach dem gefiltert werden soll.
     * @return Eine Liste von Fahrten, die am angegebenen Datum stattgefunden haben.
     */
    public List<Fahrt> filterByDate(LocalDate date) {
        if (date == null) {
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = fahrten.stream().filter(f ->
                f.getDatum().getYear()==
                date.getYear() && f.getDatum().getMonth() ==date.getMonth() && f.getDatum().getDayOfMonth() == date.getDayOfMonth()).collect(Collectors.toList());
        return gefilterteFahrten;
    }


    /**
     * Filtert Fahrten, deren Durchschnittsgeschwindigkeit unter oder gleich dem angegebenen Wert ist.
     *
     * @param avg Die Vergleichsvariable für die Durchschnittsgeschwindigkeit.
     * @return Eine Liste von Fahrten, deren Durchschnittsgeschwindigkeit unter oder gleich dem angegebenen Wert ist.
     */
    public List<Fahrt> filterByAvgVUnder(double avg){
        if (avg<0.00){
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();

        for(Fahrt fahrt: fahrten){
            double gefahreneKilometer = fahrt.getGefahreneKilometer();
            LocalTime aktiveFahrzeit = fahrt.getAktiveFahrzeit();
            double time = aktiveFahrzeit.getHour() + (aktiveFahrzeit.getMinute()/60.0);
            double instanceAvg = gefahreneKilometer/time;
            if(instanceAvg<=avg){
                gefilterteFahrten.add(fahrt);
            }
        }
        return gefilterteFahrten;
    }

    /**
     * Filtert Fahrten, deren Durchschnittsgeschwindigkeit über oder gleich dem angegebenen Wert ist.
     *
     * @param avg Die Vergleichsvariable für die Durchschnittsgeschwindigkeit.
     * @return Eine Liste von Fahrten, deren Durchschnittsgeschwindigkeit über oder gleich dem angegebenen Wert ist.
     */
    public List<Fahrt> filterByAvgVOver(double avg){
        if (avg<0.00){
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();

        for(Fahrt fahrt: fahrten){
            double gefahreneKilometer = fahrt.getGefahreneKilometer();
            LocalTime aktiveFahrzeit = fahrt.getAktiveFahrzeit();
            double time = aktiveFahrzeit.getHour() + (aktiveFahrzeit.getMinute()/60.0);
            double instanceAvg = gefahreneKilometer/time;
            if(instanceAvg>=avg){
                gefilterteFahrten.add(fahrt);
            }
        }
        return gefilterteFahrten;
    }

    /**
     * Filtert Fahrten nach bestimmten Kategorien.
     *
     * @param filterKategorien Eine Liste von Kategorien, nach denen gefiltert werden soll.
     * @return Eine Liste von Fahrten, die mindestens eine der angegebenen Kategorien enthalten.
     */
    public List<Fahrt> filterByKategorie(List<String> filterKategorien) {
        if (kategorien == null || filterKategorien.isEmpty()) {
            return this.fahrten;
        }
        List<Fahrt> gefilterteFahrten = new ArrayList<>();
        for (String filterKategorie : filterKategorien) {
            gefilterteFahrten.addAll(this.fahrten.stream()
                    .filter(f -> f.getKategorien().contains(filterKategorie))
                    .collect(Collectors.toList()));
        }
        return gefilterteFahrten;
    }
    public class InExportExc extends RuntimeException{
        public InExportExc(String msg, Throwable cause){
            super(msg,cause);
        }
    }
}
