package item;

import java.time.LocalDate;

public class Prestito {

    private String prestioID;
    private String idUtente;
    private String idOggetto;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private Boolean accettazione;

    public Prestito(){

    }

    public Prestito(String prestioID,String idUtente, String idOggetto) {
        this.prestioID= prestioID;
        this.idUtente = idUtente;
        this.idOggetto = idOggetto;
        this.dataInizio = LocalDate.now(); // Data corrente al momento della creazione
        this.dataFine = dataInizio.plusDays(7);
        this.accettazione=false;
    }

    public String getPrestitoID() {
        return prestioID;
    }

    public void setPrestioID(String prestitoID) {
        this.prestioID= idUtente;
    }


    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getIdOggetto() {
        return idOggetto;
    }

    public void setIdOggetto(String idOggetto) {
        this.idOggetto = idOggetto;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public Boolean getAccettazione() {
        return accettazione;
    }

    public void setAccettazione(Boolean accettazione) {
        this.accettazione = accettazione;
    }
}

