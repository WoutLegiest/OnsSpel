package domain;

public class Card {
    private final int idcard;
    private final String path;

    public Card(int idcard, String path) {
        this.idcard = idcard;
        this.path = path;
    }

    public Card(Card card) {
        this.idcard=card.idcard;
        this.path=card.path;
    }

    public int getIdcard() {
        return idcard;
    }

    public String getPath() {
        return path;
    }

}
