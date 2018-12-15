package global.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class PlayerDB extends Player implements Serializable {

    private static final long serialVersionUID = 4249549259368991006L;
    private String hPasswd;

    public PlayerDB(int id, String username, String email, int totalScore, Timestamp joinDate, String token, Timestamp lastGameDate, String hPasswd) {
        super(id, username, email, totalScore, joinDate, token, lastGameDate);
        this.hPasswd = hPasswd;
    }

    public String gethPasswd() {
        return hPasswd;
    }

    public void sethPasswd(String hPasswd) {
        this.hPasswd = hPasswd;
    }
}
