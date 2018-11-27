package domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Player implements Serializable {

    private static final long serialVersionUID = -6933077249723747340L;

    private final int id;
    private final String username;
    private final String password;
    private final String email;
    private final int totalScore;
    private final Timestamp joinDate;
    private final String token;

    public Player(int id, String username, String password, String email, int totalScore,
                  Timestamp joinDate, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.totalScore = totalScore;
        this.joinDate = joinDate;
        this.token = token;
    }

    public Player(String username, int totalScore, Timestamp joinDate, String email, String token) {
        id = 0;
        this.username = username;
        this.totalScore = totalScore;
        this.joinDate = joinDate;
        password = null;
        this.email = email;
        this.token = token;
    }

    public Player(Player other) {
        this.id = other.id;
        this.username = other.username;
        this.password = other.password;
        this.email = other.email;
        this.totalScore = other.totalScore;
        this.joinDate = other.joinDate;
        this.token = other.token;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
}
