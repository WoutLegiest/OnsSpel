package callBack;

import controller.GameController;
import domain.GameExtended;
import domain.Turn;
import interfaces.AppServerInterface;
import interfaces.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Wouter Legiest on 3/12/2018
 */
public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    //logic implemented for one game at a time
    GameController gameController;

    public ClientImpl()throws RemoteException{
        gameController=null;
    }

    @Override
    public void performOtherPlayerTurn(Turn turn) throws RemoteException {
        gameController.performOtherTurn(turn);
    }

    @Override
    public void yourTurn() throws RemoteException {
        gameController.yourTurn();
    }

    @Override
    public void updateScoreTable() throws RemoteException {
        gameController.updateScoreTable();

    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        gameController.updateChat(message);
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
