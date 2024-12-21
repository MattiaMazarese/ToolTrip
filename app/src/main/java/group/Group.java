package group;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupID;
    private String nome;
    private String creatoreID; // Salviamo solo l'ID del creatore
    private List<String> membri; // Lista degli ID dei membri
    private String codice;

    public Group() {
        // Costruttore vuoto richiesto da Firebase
    }

    public Group(String groupID, String nome, String creatoreID) {
        this.groupID = groupID;
        this.nome = nome;
        this.creatoreID = creatoreID; // Salva l'ID del creatore
        this.membri = new ArrayList<>();
        this.membri.add(creatoreID); // Aggiungi l'ID del creatore alla lista membri
        this.codice = null;
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

    public String getCreatoreID() {
        return creatoreID;
    }

    public void setCreatoreID(String creatoreID) {
        this.creatoreID = creatoreID;
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

    public String getCodice(){
        return this.codice;
    }

    public void setCodice(String codice){
        this.codice = codice;
    }

}