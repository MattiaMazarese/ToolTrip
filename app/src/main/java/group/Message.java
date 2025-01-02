package group;

public class Message {
    private String sender;
    private String senderName; // Nome del mittente
    private String text;
    private long timestamp;

    // Costruttore senza parametri (necessario per Firebase)
    public Message() {
    }

    // Costruttore completo
    public Message(String sender, String senderName, String text, long timestamp) {
        this.sender = sender;
        this.senderName = senderName;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getter e setter
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
