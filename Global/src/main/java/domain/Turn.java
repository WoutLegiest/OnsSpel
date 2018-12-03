package domain;

import java.io.Serializable;

public class Turn implements Serializable {

    private static final long serialVersionUID = 8826448052622567993L;

    private static final long serialVersionUID = 8826448052622567993L;

    private Player player;
    private Card card1,card2;
    private boolean correct;

    public Turn(Player player, Card card1, Card card2) {
        this.player = player;
        this.card1 = card1;
        this.card2 = card2;
    }

    public Turn(Turn turn) {
        this.player=turn.player;
        this.card1=turn.card1;
        this.card2=turn.card2;
        this.correct=turn.correct;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean checkTurn(){
        if(card1.getPath().equals(card2.getPath()))correct=true;
        else correct=false;
        return correct;
    }
}
