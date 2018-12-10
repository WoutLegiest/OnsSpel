package global.domain;

public class GamePlayer extends Player {

    private static final long serialVersionUID = -410566214722294423L;

    private int localScore;
    private int turnsPlayed;

    public GamePlayer(Player other) {
        super(other);
        localScore=0;
        turnsPlayed=0;
    }

    public GamePlayer(int id, int score){
        super(id);
        localScore = score;
    }

    public int getLocalScore() {
        return localScore;
    }

    public void setLocalScore(int localScore) {
        this.localScore = localScore;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    public void setTurnsPlayed(int turnsPlayed) {
        this.turnsPlayed = turnsPlayed;
    }

    void increaseScore(){
        localScore++;
    }

    void increaseTurns(){
        turnsPlayed++;
    }
}
