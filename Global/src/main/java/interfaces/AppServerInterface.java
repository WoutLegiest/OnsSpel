package interfaces;

import domain.Card;
import domain.Game;
import domain.GameExtended;
import domain.Player;
import exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

//Moeten allemaal throws RemoteException; hebben !!
public interface AppServerInterface extends Remote {

    String authenticatePlayer(String username, String token) throws SQLException, RemoteException;

    String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException;

    Player getPlayer(String username, String token)throws SQLException, RemoteException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<Game> getAllGames()throws RemoteException;

    ArrayList<Card> shuffleCards(int range, String theme)throws RemoteException;

    ArrayList<Card> cardsByTheme(String theme)throws RemoteException;

    int gameCreated( int owner, int maxNumberOfPlayer, int size) throws RemoteException;

    void gameCreatedExtended(GameExtended gameExtended) throws RemoteException;

}
