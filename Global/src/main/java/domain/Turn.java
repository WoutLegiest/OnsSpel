package domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Turn implements Serializable {

    private static final long serialVersionUID = 8826448052622567993L;

    private Player player;
    private int cardIndexOne,cardIndexTwo;
    private boolean correct;

    public Turn(Player player, int cardIndexOne, int cardIndexTwo) {
        this.player = player;
        this.cardIndexOne = cardIndexOne;
        this.cardIndexTwo = cardIndexTwo;
    }

    public Turn(Player player, int cardIndexOne) {
        this.player = player;
        this.cardIndexOne = cardIndexOne;
        this.cardIndexTwo = -1;
    }

    public Turn(Turn turn) {
        this.player=turn.player;
        this.cardIndexOne=turn.cardIndexOne;
        this.cardIndexTwo=turn.cardIndexTwo;
        this.correct=turn.correct;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCard1() {
        return cardIndexOne;
    }

    public void setCard1(int cardIndexOne) {
        this.cardIndexOne = cardIndexOne;
    }

    public int getCard2() {
        return cardIndexTwo;
    }

    public void setCard2(int cardIndexTwo) {
        this.cardIndexTwo = cardIndexTwo;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean checkTurn(ArrayList<Card>cards){

        correct = cards.get(cardIndexOne).getPath().equals(cards.get(cardIndexTwo).getPath());

        return correct;
    }
}
