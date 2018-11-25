package domain;

import javafx.scene.control.Button;

public class Game {
    private final int idGame;
    private final int owner;
    private final int maxNumberOfPlayers;
    private final int curNumberOfPlayers;
    private final int size;
    private final Button join;

    public Button getJoin() {
        return join;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers, int size, Button join) {
        this.idGame = idGame;
        this.owner = owner;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size=size;
        this.join = join;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int size) {
        this.idGame = idGame;
        this.owner = owner;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.join = new Button();
        this.curNumberOfPlayers=0;
        this.size=size;
    }

    public Game(int owner, int maxNumberOfPlayers, int size) {
        this.join = new Button();
        idGame=-1;
        this.owner = owner;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers=0;
        this.size = size;

    }

    public Game(Game game) {
        this.idGame=game.idGame;
        this.owner=game.owner;
        this.maxNumberOfPlayers=game.maxNumberOfPlayers;
        this.curNumberOfPlayers=game.curNumberOfPlayers;
        this.size=game.size;
        this.join = game.join;
    }

    public int getIdGame() {
        return idGame;
    }

    public int getOwner() {
        return owner;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public int getCurNumberOfPlayers() {
        return curNumberOfPlayers;
    }

    public int getSize() {
        return size;
    }
}
