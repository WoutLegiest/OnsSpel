import domain.*;
import exceptions.UserExistsException;
import interfaces.AppServerInterface;
import interfaces.ClientInterface;
import interfaces.DispatcherInterface;
import servers.AppServer;
import servers.DataBaseServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static domain.Constants.*;

public class AppServerImpl extends UnicastRemoteObject implements AppServerInterface {

    private static final long serialVersionUID = 4015101812358698416L;

    private DataBaseServer dataBase;
    private ArrayList<GameExtended> games;
    private ArrayList<ClientInterface> clientList;
    private ArrayList<AppServerInterface> appServerList;

    public AppServerImpl() throws RemoteException {

        games=new ArrayList<>();
        clientList=new ArrayList<>();

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dataBase = dispatcherImp.getDataBaseServer();

        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    public DataBaseServer getDataBase() throws RemoteException {
        return dataBase;
    }

    @Override
    public void addOtherAppServer(AppServerInterface appInterface) throws RemoteException{
        appServerList.add(appInterface);
    }

    @Override
    public void setDataBase(DataBaseServer dataBase) throws RemoteException {
        this.dataBase = dataBase;
    }

    @Override
    public int registerForCallback(ClientInterface callbackClientObject) throws RemoteException {
        if (!(clientList.contains(callbackClientObject))) {
            clientList.add(callbackClientObject);

            System.out.println("Registered new client ");
            return clientList.indexOf(callbackClientObject);
        }
        return -1;
    }

    @Override
    public String authenticatePlayer(String username, String password) throws RemoteException, SQLException {
        return dataBase.getDataBaseImpl().authenticatePlayer(username, password);
    }

    @Override
    public String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException {
        return dataBase.getDataBaseImpl().registerPlayer(username, password,email);
    }

    @Override
    public Player getPlayer(String username) throws RemoteException {
        return dataBase.getDataBaseImpl().getPlayer(username);
    }

    @Override
    public ArrayList<Player> getAllPlayers() throws RemoteException {
        return dataBase.getDataBaseImpl().getAllPlayers();
    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {
        return dataBase.getDataBaseImpl().getAllGames();
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
        return dataBase.getDataBaseImpl().getCardsByTheme(theme);
    }

    @Override
    public int gameCreated( int owner, int maxNumberOfPlayer, int size) throws RemoteException {
        return dataBase.getDataBaseImpl().registerGame(owner, maxNumberOfPlayer, size);
    }

    @Override
    public void gameCreatedExtended(GameExtended gameExtended) throws RemoteException {
        games.add(gameExtended);
        dataBase.getDataBaseImpl().saveGameExtended(gameExtended);

    }

    @Override
    public void pushTurn(int gameId, Turn turn) throws RemoteException {
        for (GameExtended gameExtended: games){
            if(gameExtended.getGame().getIdGame()==gameId){
                gameExtended.addTurn(turn);
                performTurn(gameExtended,turn);
                gameExtended.nextPlayer();
                gameExtended.updateGamePlayer(turn);
                return;
            }
        }
    }

    @Override
    public void serverToClientMessage(String username, String message, int clientIndex, int gameId) throws RemoteException {
        for (Integer index :findGame(gameId).getClientIndexes()){
            if(index!=clientIndex) clientList.get(index).receiveMessage(username,message);
        }

    }

    private void performTurn(GameExtended gameExtended, Turn turn){
        for(int i=0;i<gameExtended.getPlayers().size();i++){
            if(gameExtended.getPlayers().get(i).getId()!=turn.getPlayer().getId()){
                try {
                    clientList.get(gameExtended.getClientIndexes().get(i)).performOtherPlayerTurn(turn);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private GameExtended findGame(int gameId ){
        for(GameExtended game:games){
            if (game.getGame().getIdGame()==gameId) return game;
        }
        return null;
    }



}
