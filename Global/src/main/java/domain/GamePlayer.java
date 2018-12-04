package domain;

public class GamePlayer extends Player {
    int localScore;
    int turnsPlayed;

    public GamePlayer(Player other) {
        super(other);
        localScore=0;
        turnsPlayed=0;
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

    public void increaseScore(){
        localScore++;
    }

    public void increaseTurns(){
        turnsPlayed++;
    }
}
