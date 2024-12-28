package item;

import java.time.LocalDate;

public class Recensione {
    private String recensioneID;
    private String idUtente;
    private String idOggetto;
    private String recensioneText;

    public Recensione(){

    }

    public Recensione(String recensioneID,String idUtente, String idOggetto, String recensioneText) {
        this.recensioneID= recensioneID;
        this.idUtente = idUtente;
        this.idOggetto = idOggetto;
        this.recensioneText=recensioneText;
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

    public String getRecensioneID() {
        return recensioneID;
    }

    public String getRecensioneText() {
        return recensioneText;
    }
    public void setRecensioneID(String recensioneID) {
        this.recensioneID = recensioneID;
    }

    public void setRecensioneText(String recensioneText) {
        this.recensioneText = recensioneText;
    }
}
