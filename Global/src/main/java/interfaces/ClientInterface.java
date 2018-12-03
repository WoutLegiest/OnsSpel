package interfaces;

import domain.Turn;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Wouter Legiest on 3/12/2018
 */
public interface ClientInterface extends Remote {

    void performOtherPlayerTurn(Turn rurn) throws RemoteException;

    void updateScoreTable()throws RemoteException;

    void receiveMessage() throws RemoteException;
}
