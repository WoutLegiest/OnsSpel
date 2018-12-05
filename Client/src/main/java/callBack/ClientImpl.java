package callBack;

import controller.GameController;
import domain.Player;
import domain.Turn;
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
    public void receiveMessage(String username, String message) throws RemoteException {
        gameController.updateChat(username,message);
    }

    @Override
    public void addPlayer(Player player, int index) throws RemoteException {
        gameController.addPlayer(player,index);

    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
