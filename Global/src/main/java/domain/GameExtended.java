package domain;

import java.io.Serializable;
import java.util.ArrayList;

public class GameExtended implements Serializable {

    private static final long serialVersionUID = -7010078965463558136L;

    private final Game game;
    private final ArrayList<Card>cards;
    private final ArrayList<Player>players;

    public GameExtended(Game game) {
        this.game = game;
        this.cards=null;
        this.players=null;
    }

    public GameExtended(Game game, ArrayList<Card> cards, ArrayList<Player> players) {
        this.game = game;
        this.cards = cards;
        this.players = players;
    }

    public GameExtended(GameExtended gameExtended) {
        this.game=gameExtended.game;
        this.cards=gameExtended.cards;
        this.players=gameExtended.players;
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
}
