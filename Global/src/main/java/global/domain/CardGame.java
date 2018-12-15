package global.domain;

import java.io.Serializable;

public class CardGame implements Serializable {


    private static final long serialVersionUID = -8959916681214740626L;
    private int game_idgame;
    private int card_idcard;
    private int index;
    private int isTurned;

    public CardGame(int game_idgame, int card_idcard, int index, int isTurned) {
        this.game_idgame = game_idgame;
        this.card_idcard = card_idcard;
        this.index = index;
        this.isTurned = isTurned;
    }

    public int getGame_idgame() {
        return game_idgame;
    }

    public void setGame_idgame(int game_idgame) {
        this.game_idgame = game_idgame;
    }

    public int getCard_idcard() {
        return card_idcard;
    }

    public void setCard_idcard(int card_idcard) {
        this.card_idcard = card_idcard;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIsTurned() {
        return isTurned;
    }

    public void setIsTurned(int isTurned) {
        this.isTurned = isTurned;
    }
}
