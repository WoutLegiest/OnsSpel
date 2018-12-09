package interfaces;

import domain.Player;
import domain.Turn;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Wouter Legiest on 3/12/2018
 */
public interface ClientInterface extends Remote {

    void performOtherPlayerTurn(Turn turn) throws RemoteException;

    void yourTurn()throws RemoteException;

    void updateScoreTable()throws RemoteException;

    void receiveMessage(String username, String message) throws RemoteException;

    void addPlayer(Player player, int index) throws RemoteException;

    void startGame() throws RemoteException;

    void performImage(Turn turn) throws RemoteException;
}
