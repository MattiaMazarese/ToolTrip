package item;

import java.time.LocalDate;

public class Prestito {

    private String prestioID;
    private String idUtente;
    private String idOggetto;
    private LocalDate dataRichiestaInizio;
    private LocalDate dataRichiestaFine;
    private LocalDate dataInizioEffettiva;
    private LocalDate dataFineEffettiva;
    private Boolean accettazione;

    public Prestito() {
    }

    public Prestito(String prestioID, String idUtente, String idOggetto, LocalDate dataRichiestaInizio, LocalDate dataRichiestaFine) {
        this.prestioID = prestioID;
        this.idUtente = idUtente;
        this.idOggetto = idOggetto;
        this.dataRichiestaInizio = dataRichiestaInizio;
        this.dataRichiestaFine = dataRichiestaFine;
        this.dataInizioEffettiva = null; // Sarà impostata solo quando il proprietario accetta
        this.dataFineEffettiva = null;
        this.accettazione = false; // Di default, il prestito è in attesa
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

    public LocalDate getDataRichiestaInizio() {
        return dataRichiestaInizio;
    }

    public void setDataRichiestaInizio(LocalDate dataRichiestaInizio) {
        this.dataRichiestaInizio = dataRichiestaInizio;
    }

    public LocalDate getDataRichiestaFine() {
        return dataRichiestaFine;
    }

    public void setDataRichiestaFine(LocalDate dataRichiestaFine) {
        this.dataRichiestaFine = dataRichiestaFine;
    }

    public LocalDate getDataInizioEffettiva() {
        return dataInizioEffettiva;
    }

    public void setDataInizioEffettiva(LocalDate dataInizioEffettiva) {
        this.dataInizioEffettiva = dataInizioEffettiva;
    }

    public LocalDate getDataFineEffettiva() {
        return dataFineEffettiva;
    }

    public void setDataFineEffettiva(LocalDate dataFineEffettiva) {
        this.dataFineEffettiva = dataFineEffettiva;
    }

    public Boolean getAccettazione() {
        return accettazione;
    }

    public void setAccettazione(Boolean accettazione) {
        this.accettazione = accettazione;
    }
}

