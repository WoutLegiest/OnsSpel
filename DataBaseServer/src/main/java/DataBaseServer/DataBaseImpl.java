package DataBaseServer;

import global.exceptions.UserExistsException;
import global.domain.*;
import global.interfaces.DataBaseInterface;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
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
    private int portNumber;
    private ArrayList<DataBaseInterface> dataBaseServerList;

    /**
     * Constructor, sets up the connection with the database. A sqlite is used as database technology
     * @throws RemoteException
     */
    public DataBaseImpl() throws RemoteException {

        dataBaseServerList=new ArrayList<>();
        dataBaseServerList.add(this);
    }

    @Override
    public void settingUpDataBaseServer(int portNumber) throws RemoteException{
        this.portNumber=portNumber;
        try {
            String workingDir = System.getProperty("user.dir");
            String url = "jdbc:sqlite:" + workingDir + "/.db/database"+ portNumber +".db";

            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                stmt  = conn.createStatement();

            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("error in connect() methode");
        }

        System.out.println("connection opened");

        String s            = new String();
        StringBuffer sb = new StringBuffer();

        try
        {
            FileReader fr = new FileReader(new File("DataBaseServer/src/main/resources/SQLite scripts/databaseScript.sql"));
            // be sure to not have line starting with "--" or "/*" or any other non aplhabetical character

            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null)
            {
                sb.append(s);
            }
            br.close();

            // here is our splitter ! We use ";" as a delimiter for each request
            // then we are sure to have well formed statements
            String[] inst = sb.toString().split(";");

            stmt= conn.createStatement();

            for(int i = 0; i<inst.length; i++)
            {
                // we ensure that there is no spaces before or after the request string
                // in order to not execute empty statements
                if(!inst[i].trim().equals(""))
                {
                    stmt.executeUpdate(inst[i]);
                    System.out.println(">>"+inst[i]);
                }
            }

        }
        catch(Exception e)
        {
            System.out.println("*** Error : "+e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
            e.printStackTrace();
            System.out.println("################################################");
            System.out.println(sb.toString());
        }
    }

    @Override
    public void addDataBaseServer(DataBaseInterface dataBaseInterface) throws RemoteException {
        dataBaseServerList.add(dataBaseInterface);
    }

    @Override
    public void updateDataBase() throws RemoteException,SQLException {
        for(DataBaseInterface dataBaseInterface:dataBaseServerList){
            if(dataBaseInterface!=this){
                ArrayList<Game> existingGames=dataBaseInterface.getAllGames();
                ArrayList<PlayerDB>existingPlayers=dataBaseInterface.getAllPlayersDB();
                ArrayList<GamePlayerDB>existingGamePlayers=dataBaseInterface.getAllGamePlayers();
                ArrayList<CardGame>existingCardGames=dataBaseInterface.getAllCardGames();

                for(PlayerDB player : existingPlayers){
                    String sql = "INSERT INTO player (username,password,email, totalScore, joinDate) " +
                            "VALUES(?,?,?,?,?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, player.getUsername());
                    pstmt.setString(2, player.gethPasswd());
                    pstmt.setString(3, player.getEmail());
                    pstmt.setInt(4, player.getTotalScore());
                    pstmt.setTimestamp(5, player.getJoinDate());

                    pstmt.executeUpdate();

                }

                for(Game game :existingGames){
                    String sql = "INSERT INTO game (owner,maxNumberOfPlayers, size, curNumberOfPlayers, createDate ) " +
                            "VALUES(?,?,?,?,?)";


                    try {
                        PreparedStatement pstmt = conn.prepareStatement(sql);

                        pstmt.setInt(1, game.getOwner());
                        pstmt.setInt(2, game.getMaxNumberOfPlayers());
                        pstmt.setInt(3, game.getSize());
                        pstmt.setInt(4, game.getCurNumberOfPlayers());
                        pstmt.setLong(5, game.getCreateDate().getTime());

                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                for(GamePlayerDB gamePlayerDB : existingGamePlayers){

                    String sql = "INSERT INTO gameplayer (game_idgame,player_id,gameScore) " +
                            "VALUES(?,?,?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, gamePlayerDB.getGame_idGame());
                    pstmt.setInt(2, gamePlayerDB.getPlayer_id());
                    pstmt.setInt(3, gamePlayerDB.getScore());


                    pstmt.executeUpdate();
                }

                for(CardGame cardGame:existingCardGames){
                    String sql = "INSERT INTO cardgame (game_idgame,card_idcard,index,isTurned) " +
                            "VALUES(?,?,?,?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, cardGame.getGame_idgame());
                    pstmt.setInt(2, cardGame.getCard_idcard());
                    pstmt.setInt(3, cardGame.getIndex());
                    if(cardGame.getIsTurned()==0)pstmt.setBoolean(4,false);
                    else pstmt.setBoolean(4,true);

                    pstmt.executeUpdate();
                }



            }
        }
    }

    @Override
    public String getVariableDatabaseContent() throws RemoteException {

        return null;
    }

    @Override
    public void executeOwnStatement(PreparedStatement statement) throws RemoteException {
        for (DataBaseInterface dataBaseInterface:dataBaseServerList){
            dataBaseInterface.executeStatement(statement);
        }
    }

    @Override
    public void executeStatement(PreparedStatement statement) throws RemoteException {
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        executeOwnStatement(pstmt);
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
            executeOwnStatement(pstmt);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
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
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

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
    public ArrayList<PlayerDB> getAllPlayersDB() throws RemoteException {
        String sql = "SELECT * FROM player";

        ArrayList<PlayerDB> allPlayers = new ArrayList<>();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int id=rs.getInt("id");
                String username = rs.getString("username");
                int totalScore = rs.getInt("totalScore");
                Timestamp date = rs.getTimestamp("joinDate");
                String email = rs.getString("email");
                String token = rs.getString("token");
                String passws =rs.getString("password");
                Timestamp lastGameDate = rs.getTimestamp("lastGameDate");


                allPlayers.add(new PlayerDB(id,username,email,totalScore,date, token,lastGameDate,passws));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allPlayers;
    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {

        String sql = "SELECT * FROM game INNER JOIN player p on game.owner = p.id;";

        ArrayList<Game> allGames = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int idGame = rs.getInt("idgame");
                int owner = rs.getInt("owner");
                int maxNumberOfPlayers= rs.getInt("maxNumberOfPlayers");
                int curNumberOfPlayers=rs.getInt("curNumberOfPlayers");
                String username = rs.getString("username");
                long createDate=rs.getLong("createDate");
                int size = rs.getInt("size");

                allGames.add(new Game(idGame,owner,maxNumberOfPlayers,curNumberOfPlayers,size, username,new Timestamp(createDate)));
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
    public ArrayList<GamePlayerDB> getAllGamePlayers() throws RemoteException {
        String sql = "SELECT * FROM gameplayer";
        return processGamePlayers(sql);

    }

    @Override
    public ArrayList<CardGame> getAllCardGames() throws RemoteException {
        String sql = "SELECT * FROM cardgame";
        return processCardGames(sql);
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

    private ArrayList<GamePlayerDB> processGamePlayers(String sql) {
        ArrayList<GamePlayerDB> allGamePlayers = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int game_id = rs.getInt("game_idgame");
                int player_id = rs.getInt("player_id");
                int score = rs.getInt("gameScore");

                allGamePlayers.add(new GamePlayerDB(game_id,player_id, score));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allGamePlayers;
    }

    private ArrayList<CardGame> processCardGames(String sql) {
        ArrayList<CardGame> allGameCards = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int game_id = rs.getInt("game_idgame");
                int card_id = rs.getInt("card_idcard");
                int index = rs.getInt("index");
                int isTurned = rs.getInt("isTurned");

                allGameCards.add(new CardGame(game_id,card_id, index,isTurned));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allGameCards;
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

            executeOwnStatement(pstmt);
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
                executeOwnStatement(pstmt);
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
                executeOwnStatement(pstmt);
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
            executeOwnStatement(pstmt);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        String sql = "INSERT INTO gameplayer (game_idgame, player_id, gameScore) VALUES (?,?,0) ;";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameID);
            pstmt.setInt(2, playerID);

            executeOwnStatement(pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void updatePlayerScore(int localScore, int id) throws RemoteException {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        String sql = "UPDATE player SET totalScore = totalScore + ?, lastGameDate = ? WHERE id= ?; " ;


        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, localScore);
            pstmt.setTimestamp(2, now);
            pstmt.setInt(3, id);
            executeOwnStatement(pstmt);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void deleteGame(int GameId) throws RemoteException{

        String sqlGame = "DELETE FROM game WHERE idgame = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlGame);
            pstmt.setInt(1, GameId);

            executeOwnStatement(pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlCard = "DELETE FROM cardgame WHERE game_idgame = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlCard);
            pstmt.setInt(1, GameId);

            executeOwnStatement(pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlGamePlayer = "DELETE FROM gameplayer WHERE game_idgame= ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlGamePlayer);
            pstmt.setInt(1, GameId);

            executeOwnStatement(pstmt);
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
