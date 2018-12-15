package global.domain;

import java.io.Serializable;

public class GamePlayerDB implements Serializable {

    private static final long serialVersionUID = 8605807460467083433L;
    private int game_idGame;
    private int player_id;
    private int score;

    public GamePlayerDB(int game_idGame, int player_id, int score) {
        this.game_idGame = game_idGame;
        this.player_id = player_id;
        this.score = score;
    }

    public int getGame_idGame() {
        return game_idGame;
    }

    public void setGame_idGame(int game_idGame) {
        this.game_idGame = game_idGame;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
