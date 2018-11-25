package interfaces;

import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DispatcherInterface extends Remote {

    void registerDataBaseServer() throws RemoteException;

    String authenticatePlayer(String username, String token) throws SQLException, RemoteException;

    String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<Game> getAllGames()throws RemoteException;

    ArrayList<Card> shuffleCards(int range)throws RemoteException;

}
