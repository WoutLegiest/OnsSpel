import domain.Card;
import domain.Game;
import domain.GameExtended;
import domain.Player;
import exceptions.UserExistsException;
import interfaces.DataBaseInterface;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;


public class DataBaseImpl extends UnicastRemoteObject implements DataBaseInterface{

    Connection conn;
    Statement stmt;

    public DataBaseImpl() throws RemoteException {

        try {
            String workingDir = System.getProperty("user.dir");
            String url = "jdbc:sqlite:" + workingDir + "/.db/database1.db";

            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                stmt  = conn.createStatement();

            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("error in connect() methode");
        }
        System.out.println("connection opened");
    }

    @Override
    public String authenticatePlayer(String username, String password) throws SQLException {

        String sql = "SELECT * FROM player WHERE username='" + username + "';";
        //System.out.println(sql);
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            if(validatePassword(password,rs.getString("password"))){

                if(getTokenValid(username) == null)
                    return addToken(username);
                else
                    return getTokenValid(username);

            }
        }

      return null;
    }

    /**
     *
     * Register the player at the databank
     * Hashes the password with PBKDF2WithHmacSHA1
     *
     *  @param username
     * @param password
     */
    @Override
    public String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException {

        if(isValidUsername(username))
            throw new UserExistsException();

        Timestamp today = new Timestamp(System.currentTimeMillis());

        String passwordHashed = generateStrongPasswordHash(password);
        String sql = "INSERT INTO player (username,password,email, totalScore, joinDate) VALUES(?,?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, passwordHashed);
        pstmt.setString(3, email);
        pstmt.setInt(4, 0);
        pstmt.setTimestamp(5, today);
        pstmt.executeUpdate();
        //System.out.println("Goed toegevoegd");


        return addToken(username);
    }

    private boolean isValidUsername(String username) {

        String sql = "SELECT * FROM player WHERE username='" + username + "';";

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String addToken(String username){

        long now = System.currentTimeMillis();
        int tokenNumber = new Random().nextInt(999);
        String token = String.valueOf(now) + ':' + tokenNumber;

        String sql = "UPDATE player SET token = ? WHERE username= ?;";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, token);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return String.valueOf(tokenNumber);
    }

    private String getTokenValid(String username) {

        String sql = "SELECT * FROM player WHERE username='" + username + "';";

        ResultSet rs = null;
        long now = System.currentTimeMillis();
        String[] parts = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String token = rs.getString("token");
                parts = token.split(":");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(now - Long.parseLong(parts[0]) < 86400000)
            return parts[1];
        else
            return null;


    }

    @Override
    public ArrayList<Player> getAllPlayers() throws RemoteException {

        String sql = "SELECT * FROM player";

        ArrayList<Player> allPlayers = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                String username = rs.getString("username");
                int totalScore = rs.getInt("totalScore");
                Timestamp date = rs.getTimestamp("joinDate");
                String fullToken = rs.getString("token");
                String email = rs.getString("email");

                String[] parts= fullToken.split(":");

                allPlayers.add(new Player(username,totalScore,date, email ,parts[1]));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allPlayers;


    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {
        String sql = "SELECT * FROM game";

        ArrayList<Game> allGames = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int idGame = rs.getInt("idgame");
                int owner = rs.getInt("owner");
                int maxNumberOfPlayers= rs.getInt("maxNumberOfPlayers");
                int curNumberOfPlayers=rs.getInt("curNumberOfPlayers");

                allGames.add(new Game(idGame,owner,maxNumberOfPlayers,curNumberOfPlayers));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allGames;
    }

    @Override
    public ArrayList<Card> getAllCards() throws RemoteException {
        String sql = "SELECT * FROM card";
        return processCards(sql);
    }

    @Override
    public ArrayList<Card> getCardsByTheme(String theme) throws RemoteException{
        String sql = "SELECT * FROM card WHERE theme='" + theme + "';";
        return processCards(sql);
    }

    private ArrayList<Card> processCards(String sql) {

        ArrayList<Card> allCards = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int idCard = rs.getInt("idcard");
                String path = rs.getString("path");
                int thema = rs.getInt("theme");

                allCards.add(new Card(idCard,path, thema));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allCards;
    }

    @Override
    public Player getPlayer(String username, String token) throws RemoteException {
        ArrayList<Player>allPlayers=getAllPlayers();
        //TODO: change this horrible written function (Sorry Wouter my bad).
        for(Player player: allPlayers){
            if(player.getUsername().equals(username)&&player.getToken().equals(token)){
                return player;
            }
        }
        return null;
    }

    @Override
    public void saveGame(GameExtended gameExtended) throws RemoteException {
        String sql = "INSERT INTO game (owner,maxNumberOfPlayers,curNumberOfPlayers, size) VALUES(?,?,?,?)";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameExtended.getGame().getOwner());
            pstmt.setInt(2, gameExtended.getGame().getMaxNumberOfPlayers());
            pstmt.setInt(3, gameExtended.getGame().getCurNumberOfPlayers());
            pstmt.setInt(4,gameExtended.getGame().getSize());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    /**
     * Shout out to: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     * @param password
     * @return
     */
    private String generateStrongPasswordHash(String password) throws RemoteException {
        int iterations = 200;
        char[] chars = password.toCharArray();
        try {
            byte[] salt = getSalt();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return iterations + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }

    }

    private boolean validatePassword(String originalPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        try {
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for(int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static byte[] fromHex(String hex){
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static String toHex(byte[] array){
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        else
            return hex;

    }

}
