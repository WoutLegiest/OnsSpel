package client.callBack;

import client.controller.GameController;
import global.domain.Player;
import global.domain.Turn;
import global.interfaces.ClientInterface;

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
    public void performOtherPlayerTurn(Turn turn, boolean watch) throws RemoteException {
        gameController.performOtherTurn(turn, watch);
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
        gameController.updateChat(username, message);
    }

    @Override
    public void addPlayer(Player player, int index) throws RemoteException {
        gameController.addPlayer(player,index);

    }

    @Override
    public void startGame() throws RemoteException {
        gameController.setLabels(false);
    }

    @Override
    public void performImage(Turn turn) throws RemoteException {
        gameController.changeView(turn.getCard2());
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
