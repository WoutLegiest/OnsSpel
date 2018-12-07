package interfaces;

import domain.*;
import exceptions.UserExistsException;
import servers.AppServer;
import servers.DataBaseServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

//Moeten allemaal throws RemoteException; hebben !!
public interface AppServerInterface extends Remote {

    void addOtherAppServer(AppServerInterface appInterface) throws RemoteException;

    void setDataBase(DataBaseServer dataBase) throws RemoteException;

    int registerForCallback(ClientInterface callbackClientObject) throws java.rmi.RemoteException;

    String authenticatePlayer(String username, String token) throws SQLException, RemoteException;

    String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException;

    Player getPlayer(String username)throws SQLException, RemoteException;

    ArrayList<Player> getAllPlayers() throws RemoteException;

    ArrayList<Game> getAllGames()throws RemoteException;

    ArrayList<Card> shuffleCards(int range, String theme)throws RemoteException;

    ArrayList<Card> cardsByTheme(String theme)throws RemoteException;

    int gameCreated( int owner, int maxNumberOfPlayer, int size) throws RemoteException;

    void gameCreatedExtended(GameExtended gameExtended) throws RemoteException;

    void pushTurn(int gameId, Turn turn)throws RemoteException;

    void addPlayer(Player gp, int index, int gameID) throws RemoteException;

    void serverToClientMessage(String username, String message, int clientIndex, int gameId)throws RemoteException;

    AppServer findServer(int gameID)throws RemoteException;

    AppServerInterface findGame(int gameID) throws RemoteException;

    GameExtended findGameExtended(int gameId) throws RemoteException;
}
