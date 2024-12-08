package com.example.tooltrip;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupID;
    private String nome;
    private String city;
    private String creatoreID; // Salviamo solo l'ID del creatore
    private User creatore; // Oggetto completo del creatore
    private List<String> membri; // Lista degli ID dei membri

    public Group() {
        // Costruttore vuoto richiesto da Firebase
    }

    public Group(String groupID, String nome, String city, User creatore) {
        this.groupID = groupID;
        this.nome = nome;
        this.city = city;
        this.creatore = creatore;
        this.creatoreID = creatore.getUserID(); // Salva l'ID del creatore
        this.membri = new ArrayList<>();
        this.membri.add(creatore.getUserID()); // Aggiungi l'ID del creatore alla lista membri
    }

    // Getter e Setter
    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCreatoreID() {
        return creatoreID;
    }

    public void setCreatoreID(String creatoreID) {
        this.creatoreID = creatoreID;
    }

    public User getCreatore() {
        return creatore;
    }

    public void setCreatore(User creatore) {
        this.creatore = creatore;
    }

    public List<String> getMembri() {
        return membri;
    }

    // Metodo per aggiungere un membro alla lista (con ID)
    public void aggiungiMembro(String membroID) {
        if (!membri.contains(membroID)) {
            membri.add(membroID);
        }
    }
}
