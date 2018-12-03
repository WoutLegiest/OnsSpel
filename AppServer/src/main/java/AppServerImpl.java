import domain.*;
import exceptions.UserExistsException;
import interfaces.AppServerInterface;
import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static domain.Constants.*;

public class AppServerImpl extends UnicastRemoteObject implements AppServerInterface {

    private DataBaseInterface dataBase;
    private ArrayList<GameExtended> games;

    public AppServerImpl() throws RemoteException {

        games=new ArrayList<>();

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            String dbServiceName = dispatcherImp.getDataBaseServerServiceName();

            registry = LocateRegistry.getRegistry(IP, DATABASE_PORT);
            dataBase = (DataBaseInterface) registry.lookup(dbServiceName);

        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public String authenticatePlayer(String username, String password) throws RemoteException, SQLException {
        return dataBase.authenticatePlayer(username, password);
    }

    @Override
    public String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException {
        return dataBase.registerPlayer(username, password,email);
    }

    @Override
    public Player getPlayer(String username, String token) throws SQLException, RemoteException {
        return dataBase.getPlayer(username,token);
    }

    @Override
    public ArrayList<Player> getAllPlayers() throws RemoteException {
        return dataBase.getAllPlayers();
    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {
        return dataBase.getAllGames();
    }

    @Override
    public ArrayList<Card> shuffleCards(int range, String theme) throws RemoteException {
        ArrayList<Card> cardsByTheme= cardsByTheme(theme);
        //System.out.println("CardByTheme is er " + cardsByTheme.size());
        int numberOfCards=range*range/2;
        ArrayList<Card> gameCards=new ArrayList<>();
        Random rand = new Random();

        for (int i=0;i<numberOfCards;i++){
            int index= rand.nextInt(Math.abs(cardsByTheme.size()));
            Card tempCard=cardsByTheme.remove(index);
            //System.out.println(tempCard.getIdcard());
            gameCards.add(tempCard);
            gameCards.add(tempCard);
            //System.out.println(i);
        }
        Collections.shuffle(gameCards);

        //System.out.println("Voor het teruggeven" + gameCards.size());
        return gameCards;
    }

    @Override
    public ArrayList<Card> cardsByTheme(String theme)throws RemoteException{
        return dataBase.getCardsByTheme(theme);
    }

    @Override
    public int gameCreated( int owner, int maxNumberOfPlayer, int size) throws RemoteException {
        return dataBase.registerGame(owner, maxNumberOfPlayer, size);
    }

    @Override
    public void gameCreatedExtended(GameExtended gameExtended) throws RemoteException {
        games.add(gameExtended);
        dataBase.saveGame(gameExtended);

    }

    @Override
    public void pushTurn(int gameId, Turn turn) throws RemoteException {
        for (GameExtended gameExtended: games){
            if(gameExtended.getGame().getIdGame()==gameId){
                gameExtended.addTurn(turn);
                performTurn(gameExtended);
                return;
            }
        }
    }

    private void performTurn(GameExtended gameExtended){
        //todo: gameExtended moet een array bij houden met de clients, dewe clients moeten update turn kunnen uitvoeren.
    }



}
