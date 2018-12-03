package domain;

import javafx.scene.control.Button;

import java.io.Serializable;

public class Game implements Serializable {

    private static final long serialVersionUID = 4081852501232595249L;

    private final int idGame;
    private final int owner;
    private String ownerUsername;
    private final int maxNumberOfPlayers;
    private int curNumberOfPlayers;
    private final int size;

    public Game(int idGame, int owner, String ownerUsername, int maxNumberOfPlayers, int curNumberOfPlayers, int size) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size = size;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers, int size) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = null;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size=size;
    }

    public Game(int idGame, String ownerUsername, int maxNumberOfPlayers, int curNumberOfPlayers) {
        this.idGame = idGame;
        this.owner = -1;
        this.ownerUsername = ownerUsername;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers=curNumberOfPlayers;
        this.size=-1;
    }

    public Game(Game game) {
        this.idGame=game.idGame;
        this.owner=game.owner;
        this.ownerUsername = game.ownerUsername;
        this.maxNumberOfPlayers=game.maxNumberOfPlayers;
        this.curNumberOfPlayers=game.curNumberOfPlayers;
        this.size=game.size;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = null;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size = -1;
    }

    public int getIdGame() {
        return idGame;
    }

    public int getOwner() {
        return owner;
    }

    public String getOwnerUsername() {
        return ownerUsername;
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

    public void increaseNumberOfPlayers(){
        curNumberOfPlayers++;
    }

    public void setCurNumberOfPlayers(int curNumberOfPlayers) {
        this.curNumberOfPlayers = curNumberOfPlayers;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
