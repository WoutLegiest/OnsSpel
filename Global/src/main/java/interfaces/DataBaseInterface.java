package interfaces;

import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataBaseInterface extends Remote {

    String authenticatePlayer(String username, String password) throws RemoteException, SQLException;

    String registerPlayer(String username, String password, String email) throws RemoteException,
            UserExistsException, SQLException;

    boolean isValidUsername(String username) throws RemoteException, SQLException;

    String addToken(String username) throws RemoteException;

    String getTokenValid(String username) throws RemoteException;

    String generateStorngPasswordHash(String password) throws RemoteException;

    boolean validatePassword(String originalPassword, String storedPassword) throws RemoteException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<Game> getAllGames() throws RemoteException;

    ArrayList<Card> getAllCards()throws RemoteException;


 }
