package domain;

import java.io.Serializable;
import java.util.ArrayList;

public class GameExtended implements Serializable {

    private static final long serialVersionUID = -7010078965463558136L;

    private final Game game;
    private final ArrayList<Card>cards;
    private final ArrayList<Player>players;
    private ArrayList<ArrayList<Turn>> turns;

    public GameExtended(Game game) {
        this.game = game;
        this.cards=null;
        this.players=null;
        turns=new ArrayList<>();
    }

    public GameExtended(Game game, ArrayList<Card> cards, ArrayList<Player> players) {
        this.game = game;
        this.cards = cards;
        this.players = players;
        turns=new ArrayList<>();
    }

    public GameExtended(GameExtended gameExtended) {
        this.game=gameExtended.game;
        this.cards=gameExtended.cards;
        this.players=gameExtended.players;
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

    public ArrayList<ArrayList<Turn>> getTurns() {
        return turns;
    }

    public void setTurns(ArrayList<ArrayList<Turn>> turns) {
        this.turns = turns;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}
