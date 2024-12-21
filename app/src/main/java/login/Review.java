package login;

public class Review {
    private String reviewID;
    private int stelle;
    private String descrizione;
    private User userID;

    public Review() {
        // Costruttore vuoto richiesto da Firebase
    }


    // Costruttore con tutti i parametri
    public Review(String reviewID, int stelle, String descrizione, User userID) {
        this.reviewID = reviewID;
        this.stelle = stelle;
        this.descrizione = descrizione;
        this.userID = userID;
    }

    // Getter e Setter per reviewID
    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    // Getter e Setter per itemID

    // Getter e Setter per stelle
    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    // Getter e Setter per descrizione
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    // Getter e Setter per userID (utente che ha scritto la recensione)
    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }
}
