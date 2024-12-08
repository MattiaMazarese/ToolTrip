package com.example.tooltrip;

public class Address {
    private String addressID;
    private String citta;
    private String via;
    private String civico;
    private String CAP;
    private String provincia;

    public Address(){
        // Costruttore vuoto richiesto da Firebase
    }

    // Costruttore con tutti i parametri
    public Address(String addressID, String citta, String via, String civico, String CAP, String provincia) {
        this.addressID = addressID;
        this.citta = citta;
        this.via = via;
        this.civico = civico;
        this.CAP = CAP;
        this.provincia = provincia;
    }

    // Getter e Setter per addressID
    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    // Getter e Setter per città
    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    // Getter e Setter per via
    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    // Getter e Setter per civico
    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    // Getter e Setter per CAP
    public String getCAP() {
        return CAP;
    }

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    // Getter e Setter per provincia
    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

}
