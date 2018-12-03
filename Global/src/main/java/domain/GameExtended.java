package domain;

import java.io.Serializable;
import java.util.ArrayList;

public class GameExtended implements Serializable {

    private static final long serialVersionUID = -7010078965463558136L;

    private final Game game;
    private final ArrayList<Card>cards;
    private final ArrayList<Player>players;
    private Player currentPlayerTurn;
    private ArrayList<Turn> turns;

    public GameExtended(Game game) {
        this.game = game;
        this.cards=null;
        this.players=null;
        this.currentPlayerTurn=null;
        turns=new ArrayList<>();
    }

    public GameExtended(Game game, ArrayList<Card> cards, ArrayList<Player> players, Player currentPlayerTurn) {
        this.game = game;
        this.cards = cards;
        this.players = players;
        this.currentPlayerTurn=currentPlayerTurn;
        turns=new ArrayList<>();
    }

    public GameExtended(GameExtended gameExtended) {
        this.game=gameExtended.game;
        this.cards=gameExtended.cards;
        this.players=gameExtended.players;
        this.currentPlayerTurn=gameExtended.currentPlayerTurn;
        this.turns=gameExtended.turns;
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public void addPlayer(Player player) {
        players.add(player);
        game.increaseNumberOfPlayers();
    }

    public Player getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(Player currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public void addTurn(Turn turn){
        turns.add(turn);
    }
}
