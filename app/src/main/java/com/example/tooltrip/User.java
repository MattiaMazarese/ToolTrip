package com.example.tooltrip;

public class User {
    private String userID;
    private String nome;
    private String cognome;
    private String annoNascita;
    private Address addressID;
    private String numTelefono;

    public User() {
        // Firebase richiede questo costruttore
    }

    public User(String userID, String nome, String cognome, String annoNascita, Address address, String numTelefono, Object o){
        // Costruttore vuoto richiesto da Firebase
    }

    // Costruttore con tutti i parametri
    public User(String userID, String nome, String cognome, String annoNascita, Address address, String numTelefono) {
        this.userID = userID;
        this.nome = nome;
        this.cognome = cognome;
        this.annoNascita = annoNascita;
        this.addressID = address;
        this.numTelefono = numTelefono;
    }

    // Getter e Setter per userID
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Getter e Setter per nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter e Setter per cognome
    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    // Getter e Setter per anno di nascita
    public String getAnnoNascita() {
        return annoNascita;
    }

    public void setAnnoNascita(String annoNascita) {
        this.annoNascita = annoNascita;
    }

    // Getter e Setter per addressID
    public Address getAddress() {
        return addressID;
    }

    public void setAddress(Address address) {
        this.addressID = address;
    }

    // Getter e Setter per numTelefono
    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }
}
