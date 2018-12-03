package callBack;

import domain.Turn;
import interfaces.AppServerInterface;
import interfaces.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Wouter Legiest on 3/12/2018
 */
public class ClientImpl extends UnicastRemoteObject implements ClientInterface {

    public ClientImpl()throws RemoteException{

    }

    @Override
    public void performOtherPlayerTurn(Turn turn) throws RemoteException {

    }

    @Override
    public void updateScoreTable() throws RemoteException {

    }

    @Override
    public void receiveMessage() throws RemoteException {

    }
}
