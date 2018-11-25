import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;
import interfaces.AppServerInterface;
import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static domain.Constants.*;

public class AppServerImpl extends UnicastRemoteObject implements AppServerInterface {

    private DataBaseInterface dataBase;

    public AppServerImpl() throws RemoteException {

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
    public ArrayList<Player> getAllPlayers() throws RemoteException {
        return dataBase.getAllPlayers();
    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {
        return dataBase.getAllGames();
    }

    @Override
    public ArrayList<Card> shuffleCards(int range) throws RemoteException {
        ArrayList<Card> allCards= dataBase.getAllCards();
        int numberOfCards=range*range/2;
        ArrayList<Card> gameCards=new ArrayList<>();
        for (int i=0;i<numberOfCards;i++){
            gameCards.add(allCards.get(i));
            gameCards.add(allCards.get(i));
        }
        Collections.shuffle(gameCards);

        return gameCards;
    }

}
