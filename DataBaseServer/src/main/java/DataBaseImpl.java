import domain.*;
import exceptions.UserExistsException;
import interfaces.DataBaseInterface;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class DataBaseImpl extends UnicastRemoteObject implements DataBaseInterface{

    Connection conn;
    Statement stmt;

    /**
     * Constructor, sets up the connection with the database. A sqlite is used as database technology
     * @throws RemoteException
     */
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
    public String registerPlayer(String username, String password, String email) throws RemoteException,
            UserExistsException, SQLException {

        if(isValidUsername(username))
            throw new UserExistsException();

        Timestamp today = new Timestamp(System.currentTimeMillis());

        String passwordHashed = generateStrongPasswordHash(password);
        String sql = "INSERT INTO player (username,password,email, totalScore, joinDate) " +
                "VALUES(?,?,?,?,?)";

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
            return rs.next();
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
                Timestamp lastGameDate = rs.getTimestamp("lastGameDate");

                String[] parts= fullToken.split(":");

                allPlayers.add(new Player(username,totalScore,date, email ,parts[1],lastGameDate));
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

        try{
            for(Game game: allGames){
                int owner = game.getOwner();

                sql = "SELECT username FROM player WHERE id=" + owner + ";";

                ResultSet rs = stmt.executeQuery(sql);

                rs.next();
                game.setOwnerUsername(rs.getString("username"));

            }
        }catch (SQLException e) {
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

    /**
     * Method that is used to handel a query to retrive a list of cards
     * @param sql
     * @return
     */
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
    public Player getPlayer(String username) throws RemoteException {

        String sql = "SELECT * FROM player WHERE username='" + username + "';";

        try {
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            int id = rs.getInt("id");
            String usrnme = rs.getString("username");
            int totalScore = rs.getInt("totalScore");
            Timestamp date = rs.getTimestamp("joinDate");
            String fullToken = rs.getString("token");
            String email = rs.getString("email");
            Timestamp lastGameDate = rs.getTimestamp("lastGameDate");

            String[] parts= fullToken.split(":");

            return new Player(id, usrnme, email, totalScore, date, parts[1], lastGameDate );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getUsername(int idOwn) throws RemoteException{

        String sql = "SELECT * FROM player WHERE id='" + idOwn + "';";

        try {
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            return rs.getString("username");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public int registerGame(int owner, int maxNumberOfPlayer, int size) throws RemoteException{

        String sql = "INSERT INTO game (owner,maxNumberOfPlayers, size, curNumberOfPlayers, createDate ) " +
                "VALUES(?,?,?,?,?)";

        long now = System.currentTimeMillis();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, owner);
            pstmt.setInt(2, maxNumberOfPlayer);
            pstmt.setInt(3, size);
            pstmt.setInt(4, 1);
            pstmt.setLong(5, now);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "SELECT * FROM game WHERE createDate='" + now + "';";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            return rs.getInt("idgame");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

    }

    @Override
    public void saveGameExtended(GameExtended gameExtended) throws RemoteException {

        //insert GamePlayers
        PreparedStatement pstmt;
        for(int i = 0; i<gameExtended.getPlayers().size(); i++){
            pstmt = null;
            String sqlGamePlayer = "INSERT INTO gameplayer (game_idgame,player_id) VALUES(?,?)";
            try {
                pstmt = conn.prepareStatement(sqlGamePlayer);
                pstmt.setInt(1, gameExtended.getGame().getIdGame());
                pstmt.setInt(2, gameExtended.getPlayers().get(i).getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Insert GameCards
        for(int i=0;i<gameExtended.getCards().size();i++){
            pstmt = null;
            String sqlCardgGame = "INSERT INTO cardgame (game_idgame,card_idcard,`index`) VALUES(?,?,?)";
            try {
                pstmt = conn.prepareStatement(sqlCardgGame);
                pstmt.setInt(1, gameExtended.getGame().getIdGame());
                pstmt.setInt(2, gameExtended.getCards().get(i).getIdcard());
                pstmt.setInt(3, i);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public GameExtended getGameExtended(int gameID) throws RemoteException{

        Game game = null;
        ArrayList<GamePlayer> players = new ArrayList<>();
        ArrayList<Card> gameCards = new ArrayList<>();

        //Getten van Game

        String sqlGame = "SELECT * FROM game WHERE idgame = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlGame);
            pstmt.setInt(1,gameID);
            ResultSet rs = pstmt.executeQuery(sqlGame);

            rs.next();

            int owner = rs.getInt("owner");
            int maxNumberOfPlayers= rs.getInt("maxNumberOfPlayers");
            int curNumberOfPlayers=rs.getInt("curNumberOfPlayers");
            int size=rs.getInt("size");

            game = new Game(gameID, owner, maxNumberOfPlayers, curNumberOfPlayers, size);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Getten van Players

        String sqlPlayers = "SELECT * FROM gameplayer WHERE game_idgame = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlPlayers);
            pstmt.setInt(1,gameID);
            ResultSet rs = pstmt.executeQuery(sqlPlayers);

            while(rs.next()){

                int player_id = rs.getInt("player_id");
                int score = rs.getInt("gameScore");

                players.add(new GamePlayer(player_id, score));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Getten van Cards


        String sqlCard = "SELECT * FROM cardgame\n" +
                "  INNER JOIN card c on cardgame.card_idcard = c.idcard\n" +
                "WHERE game_idgame = ?;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlCard);
            pstmt.setInt(1,gameID);
            ResultSet rs = pstmt.executeQuery(sqlCard);

            while(rs.next()){

                int idCard = rs.getInt("card_idcard");
                String path = rs.getString("path");
                //int themaString = rs.getString("theme");


                //gameCards.add(new Card(idCard,path, thema));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new GameExtended(game, null,gameCards,players, null);

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

    @Override
    public void addPlayer(int gameID, int playerID) throws RemoteException{

        String sqlGame = "UPDATE game SET curNumberOfPlayers = curNumberOfPlayers + 1  WHERE idgame= ?;";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlGame);
            pstmt.setInt(1, gameID);
            pstmt.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        String sql = "INSERT INTO gameplayer (game_idgame, player_id, gameScore) VALUES (?,?,0) ;";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.setInt(2, playerID);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
