package com.example.tooltrip;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String itemId;
    private String nome;
    private String descrizione;
    private String categoria;
    private User possesore;
    private Boolean pubblico;
    private List<Review> recensioni;

    public Item() {
        // Costruttore vuoto richiesto da Firebase
    }

    // Costruttore con tutti i parametri
    public Item(String itemId, String nome, String descrizione, String categoria, User possesore, Boolean pubblico) {
        this.itemId = itemId;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.possesore = possesore;
        this.pubblico = pubblico;
        this.recensioni = new ArrayList<>();
    }

    // Getter e Setter per itemId
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    // Getter e Setter per nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter e Setter per descrizione
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    // Getter e Setter per categoria
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Getter e Setter per possesore (oggetto User)
    public User getPossesore() {
        return possesore;
    }

    public void setPossesore(User possesore) {
        this.possesore = possesore;
    }

    // Getter e Setter per pubblico
    public Boolean getPubblico() {
        return pubblico;
    }

    public void setPubblico(Boolean pubblico) {
        this.pubblico = pubblico;
    }

    public List<Review> getListaRecensioni() {
        return recensioni;
    }

    public void addRecensione(Review recensione) {this.recensioni.add(recensione);}

}
