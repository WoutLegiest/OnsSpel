package global.interfaces;

import global.domain.Card;
import global.domain.Game;
import global.domain.GameExtended;
import global.domain.Player;
import global.exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataBaseInterface extends Remote {

    String authenticatePlayer(String username, String password) throws RemoteException, SQLException;

    String registerPlayer(String username, String password, String email) throws RemoteException,
            UserExistsException, SQLException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<Game> getAllGames() throws RemoteException;

    ArrayList<Card> getAllCards()throws RemoteException;

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

