package at.jku.se.prse.team3;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

public class Fahrt {

    private final StringProperty kfzKennzeichen;
    private final ObjectProperty<LocalDate> datum;
    private final ObjectProperty<LocalTime> abfahrtszeit;
    private final ObjectProperty<LocalTime> ankunftszeit;
    private final DoubleProperty gefahreneKilometer;
    private final ObjectProperty<LocalTime> aktiveFahrzeit;
    private final ListProperty<String> kategorien;
    private final ObjectProperty<FahrtStatus> fahrtstatus;

    public Fahrt(String kfzKennzeichen, LocalDate datum, LocalTime abfahrtszeit, LocalTime ankunftszeit, Double gefahreneKilometer, LocalTime aktiveFahrzeit, List<String> kategorien, FahrtStatus fahrtstatus) {
        this.kfzKennzeichen = new SimpleStringProperty(kfzKennzeichen);
        this.datum = new SimpleObjectProperty<>(datum);
        this.abfahrtszeit = new SimpleObjectProperty<>(abfahrtszeit);
        this.ankunftszeit = new SimpleObjectProperty<>(ankunftszeit);
        this.gefahreneKilometer = new SimpleDoubleProperty(gefahreneKilometer);
        this.aktiveFahrzeit = new SimpleObjectProperty<>(aktiveFahrzeit);
        this.kategorien = new SimpleListProperty<>(FXCollections.observableArrayList(kategorien));
        this.fahrtstatus = new SimpleObjectProperty<>(fahrtstatus);
    }

    // Standard-Getters und Setters für Properties
    public String getKfzKennzeichen() {
        return kfzKennzeichen.get();
    }

    public StringProperty kfzKennzeichenProperty() {
        return kfzKennzeichen;
    }

    public void setKfzKennzeichen(String kfzKennzeichen) {
        this.kfzKennzeichen.set(kfzKennzeichen);
    }

    public LocalDate getDatum() {
        return datum.get();
    }

    public ObjectProperty<LocalDate> datumProperty() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum.set(datum);
    }

    public LocalTime getAbfahrtszeit() {
        return abfahrtszeit.get();
    }

    public ObjectProperty<LocalTime> abfahrtszeitProperty() {
        return abfahrtszeit;
    }

    public void setAbfahrtszeit(LocalTime abfahrtszeit) {
        this.abfahrtszeit.set(abfahrtszeit);
    }

    public LocalTime getAnkunftszeit() {
        return ankunftszeit.get();
    }

    public ObjectProperty<LocalTime> ankunftszeitProperty() {
        return ankunftszeit;
    }

    public void setAnkunftszeit(LocalTime ankunftszeit) {
        this.ankunftszeit.set(ankunftszeit);
    }

    public double getGefahreneKilometer() {
        return gefahreneKilometer.get();
    }

    public DoubleProperty gefahreneKilometerProperty() {
        return gefahreneKilometer;
    }

    public void setGefahreneKilometer(double gefahreneKilometer) {
        this.gefahreneKilometer.set(gefahreneKilometer);
    }

    public LocalTime getAktiveFahrzeit() {
        return aktiveFahrzeit.get();
    }

    public ObjectProperty<LocalTime> aktiveFahrzeitProperty() {
        return aktiveFahrzeit;
    }

    public void setAktiveFahrzeit(LocalTime aktiveFahrzeit) {
        this.aktiveFahrzeit.set(aktiveFahrzeit);
    }

    public List<String> getKategorien() {
        return kategorien.get();
    }

    public ListProperty<String> kategorienProperty() {
        return kategorien;
    }

    public void setKategorien(List<String> kategorien) {
        this.kategorien.setAll(kategorien);
    }

    public FahrtStatus getFahrtstatus() {
        return fahrtstatus.get();
    }

    public ObjectProperty<FahrtStatus> fahrtstatusProperty() {
        return fahrtstatus;
    }

    public void setFahrtstatus(FahrtStatus fahrtstatus) {
        this.fahrtstatus.set(fahrtstatus);
    }

    // Hinzufügen einer Kategorie
    public void addKategorie(String kategorie) {
        this.kategorien.add(kategorie);
    }


    // Entfernen einer Kategorie
    public void removeKategorie(String kategorie) {
        this.kategorien.remove(kategorie);
    }

    // Überprüfen, ob eine Kategorie bereits zugeordnet ist
    public boolean hasKategorie(String kategorie) {
        return this.kategorien.contains(kategorie);
    }

    public void renameKategorie(String oldKategorie, String newKategorie){
        for(int i=0; i<kategorien.size()-1; i++){
            if(kategorien.get(i).equals(oldKategorie)){
                kategorien.set(i,newKategorie);
            }
        }
    }
    // Ersetzen der Kategorienliste
    public void setKategorienList(List<String> neueKategorien) {
        this.kategorien.clear();
        this.kategorien.addAll(neueKategorien);
    }

    // Abrufen der Kategorienliste als unveränderliche Liste
    public ObservableList<String> getUnmodifiableKategorien() {
        return FXCollections.unmodifiableObservableList(this.kategorien.get());
    }


}
