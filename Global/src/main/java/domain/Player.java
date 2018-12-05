package domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Player implements Serializable {

    private static final long serialVersionUID = -6933077249723747340L;

    private final int id;
    private final String username;
    private final String email;
    private final int totalScore;
    private final Timestamp joinDate;
    private final String token;
    private Timestamp lastGameDate;

    public Player(int id, String username, String email, int totalScore,
                  Timestamp joinDate, String token, Timestamp lastGameDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.totalScore = totalScore;
        this.joinDate = joinDate;
        this.token = token;
        this.lastGameDate = lastGameDate;
    }

    public Player(String username, int totalScore, Timestamp joinDate,
                  String email, String token, Timestamp lastGameDate) {
        id = 0;
        this.username = username;
        this.totalScore = totalScore;
        this.joinDate = joinDate;
        this.email = email;
        this.token = token;
        this.lastGameDate= lastGameDate;
    }

    public Player(int id) {
        this.id = id;

        username = null;
        email = null;
        totalScore = 0;
        joinDate = null;
        token = null;
    }

    public Player(Player other) {
        this.id = other.id;
        this.username = other.username;
        this.email = other.email;
        this.totalScore = other.totalScore;
        this.joinDate = other.joinDate;
        this.token = other.token;
        this.lastGameDate = other.lastGameDate;

    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public String getToken() {
        return token;
    }

    public Timestamp getLastGameDate() {
        return lastGameDate;
    }

    public void setLastGameDate(Timestamp lastGameDate) {
        this.lastGameDate = lastGameDate;
    }
}
