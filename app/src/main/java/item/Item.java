package item;

import login.Review;
import login.User;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String itemId;
    private String nome;
    private String descrizione;
    private String categoriaId; // Cambiato da "categoria" a "categoriaId"
    private User possesore;
    private Boolean pubblico;

    private Boolean visualizzaSoloGruppi;
    private List<Review> recensioni;

    public Item() {
        // Costruttore vuoto richiesto da Firebase
    }

    // Costruttore con tutti i parametri
    public Item(String itemId, String nome, String descrizione, String categoriaId, User possesore, Boolean pubblico,boolean visualizzaSoloGruppi) {
        this.itemId = itemId;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoriaId = categoriaId;
        this.possesore = possesore;
        this.pubblico = pubblico;
        this.visualizzaSoloGruppi=visualizzaSoloGruppi;
        this.recensioni = new ArrayList<>();
    }

    public Boolean getVisualizzaSoloGruppi() {
        return visualizzaSoloGruppi;
    }

    public void setVisualizzaSoloGruppi(Boolean visualizzaSoloGruppi) {
        this.visualizzaSoloGruppi = visualizzaSoloGruppi;
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

    // Getter e Setter per categoriaId
    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    // Getter e Setter per possesore (oggetto User)
    public User getPossesore() {
        return possesore;
    }

    public void setPossesore(User possesore) {
        this.possesore = possesore;
    }

    // Getter e Setter per pubblico
    public Boolean isPubblico() {
        return pubblico;
    }

    public void setPubblico(Boolean pubblico) {
        this.pubblico = pubblico;
    }

    public List<Review> getListaRecensioni() {
        return recensioni;
    }

    public void addRecensione(Review recensione) {
        this.recensioni.add(recensione);
    }
}
