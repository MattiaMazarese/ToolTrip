package com.example.tooltrip;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupID;
    private String nome;
    private User creatore;
    private List<User> membri;  // Lista di membri del gruppo

    // Costruttore
    public Group(String groupID, String nome, User creatore) {
        this.groupID = groupID;
        this.nome = nome;
        this.creatore = creatore;
        this.membri = new ArrayList<>();
        this.membri.add(creatore);  // Il creatore è automaticamente un membro
    }

    // Aggiungi un membro
    public void aggiungiMembro(User membro) {
        if (!membri.contains(membro)) {
            membri.add(membro);
        }
    }

    // Rimuovi un membro
    public void rimuoviMembro(User membro) {
        membri.remove(membro);
    }

    // Getter e Setter
    public List<User> getMembri() {
        return membri;
    }

    public void setMembri(List<User> membri) {
        this.membri = membri;
    }

    // Getter e Setter per groupID
    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    // Getter e Setter per nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter e Setter per creatore (oggetto User)
    public User getCreatore() {
        return creatore;
    }

    public void setCreatore(User creatore) {
        this.creatore = creatore;
    }


    public List<User> getListaMembri() {
        return membri;
    }

    public void addMembro(User m) {this.membri.add(m);}
}
