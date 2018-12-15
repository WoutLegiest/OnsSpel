package global.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Game implements Serializable {

    private static final long serialVersionUID = 4081852501232595249L;

    private final int idGame;
    private final int owner;
    private String ownerUsername;
    private final int maxNumberOfPlayers;
    private int curNumberOfPlayers;
    private final int size;
    private final Timestamp createDate;

    public Game(int idGame, int owner, String ownerUsername, int maxNumberOfPlayers, int curNumberOfPlayers, int size, Timestamp createDate) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size = size;
        this.createDate = createDate;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers, int size) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = null;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size=size;
        createDate = null;
    }

    public Game(int idGame, String ownerUsername, int maxNumberOfPlayers, int curNumberOfPlayers) {
        this.idGame = idGame;
        this.owner = -1;
        this.ownerUsername = ownerUsername;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers=curNumberOfPlayers;
        this.size=-1;
        createDate = null;
    }

    public Game(Game game) {
        this.idGame=game.idGame;
        this.owner=game.owner;
        this.ownerUsername = game.ownerUsername;
        this.maxNumberOfPlayers=game.maxNumberOfPlayers;
        this.curNumberOfPlayers=game.curNumberOfPlayers;
        this.size=game.size;
        createDate = game.createDate;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = null;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size = -1;
        createDate = null;
    }

    public Game(int idGame, int owner, int maxNumberOfPlayers, int curNumberOfPlayers, String username) {
        this.idGame = idGame;
        this.owner = owner;
        this.ownerUsername = username;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.curNumberOfPlayers = curNumberOfPlayers;
        this.size = -1;
        createDate = null;
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

    void increaseNumberOfPlayers(){
        curNumberOfPlayers++;
    }

    public void setCurNumberOfPlayers(int curNumberOfPlayers) {
        this.curNumberOfPlayers = curNumberOfPlayers;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
