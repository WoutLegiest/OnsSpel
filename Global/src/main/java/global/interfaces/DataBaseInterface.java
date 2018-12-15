package global.interfaces;

import global.domain.*;
import global.exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public interface DataBaseInterface extends Remote {

    void settingUpDataBaseServer(int portNumber) throws RemoteException;

    void addDataBaseServer(DataBaseInterface dataBaseInterface) throws RemoteException;

    void updateDataBase() throws RemoteException,SQLException;

    String getVariableDatabaseContent()throws RemoteException;

    void executeOwnStatement(PreparedStatement statement) throws RemoteException;

    void executeStatement(PreparedStatement statement) throws RemoteException;

    String authenticatePlayer(String username, String password) throws RemoteException, SQLException;

    String registerPlayer(String username, String password, String email) throws RemoteException,
            UserExistsException, SQLException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<PlayerDB> getAllPlayersDB() throws RemoteException;

    ArrayList<Game> getAllGames() throws RemoteException;

    ArrayList<Card> getAllCards()throws RemoteException;

    ArrayList<GamePlayerDB> getAllGamePlayers()throws RemoteException;

    ArrayList<CardGame>getAllCardGames()throws RemoteException;

    ArrayList<Card> getCardsByTheme(String theme)throws RemoteException;

    Player getPlayer(String username)throws RemoteException;

    void saveGameExtended(GameExtended gameExtended) throws RemoteException;

    String getUsername(int id) throws RemoteException;

    int registerGame(int owner, int maxNumberOfPlayer, int size) throws RemoteException;

    GameExtended getGameExtended(int gameID) throws RemoteException;

    void addPlayer(int gameID, int playerID) throws RemoteException;

    void updatePlayerScore(int localScore, int id) throws RemoteException;

    void deleteGame(int GameId) throws RemoteException;


}

