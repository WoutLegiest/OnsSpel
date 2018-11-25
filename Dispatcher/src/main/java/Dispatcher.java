import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;
import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static domain.Constants.DATABASE_PORT;

public class Dispatcher extends UnicastRemoteObject implements DispatcherInterface {

    private ArrayList<DataBaseDisp> dataBaseDisps;

    public Dispatcher() throws RemoteException {
        dataBaseDisps = new ArrayList<>();
    }

    /**
     * Find the registry of the DB
     * Make a new DB and bind it to the Registry Service
     * @throws RemoteException
     */
    @Override
    public void registerDataBaseServer() throws RemoteException{
        try{

            Registry registry = LocateRegistry.getRegistry(DATABASE_PORT);
            DataBaseInterface databaseImp = (DataBaseInterface) registry.lookup("dataBaseService");

            //Nieuwe Database server maken op bais van afgehaalde Imp van de database
            DataBaseDisp newServer = new DataBaseDisp(databaseImp);
            dataBaseDisps.add(newServer);
            System.out.println("DB toegevoegd aan Dispatch");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String authenticatePlayer(String username, String password) throws RemoteException, SQLException{
        return dataBaseDisps.get(0).getDataBase().authenticatePlayer(username, password);
    }

    @Override
    public String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException {
        return dataBaseDisps.get(0).getDataBase().registerPlayer(username, password,email);
    }

    @Override
    public ArrayList<Player> getAllPlayers() throws RemoteException {
        return dataBaseDisps.get(0).getDataBase().getAllPlayers();
    }

    @Override
    public ArrayList<Game> getAllGames() throws RemoteException {
        return dataBaseDisps.get(0).getDataBase().getAllGames();
    }

    @Override
    public ArrayList<Card> shuffleCards(int range) throws RemoteException {
        ArrayList<Card> allCards=dataBaseDisps.get(0).getDataBase().getAllCards();
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
