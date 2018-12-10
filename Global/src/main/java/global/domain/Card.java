package global.domain;

import java.io.Serializable;

public class Card implements Serializable {

    private static final long serialVersionUID = 7582974674626563766L;

    private final int idcard;
    private final String path;
    private final int thema;

    public Card(int idcard, String path, int thema) {
        this.idcard = idcard;
        this.path = path;
        this.thema = thema;
    }

    public Card(Card card) {
        this.idcard=card.idcard;
        this.path=card.path;
        this.thema=card.thema;
    }

    public int getIdcard() {
        return idcard;
    }

    public String getPath() {
        return path;
    }

    public int getThema() {
        return thema;
    }
}
