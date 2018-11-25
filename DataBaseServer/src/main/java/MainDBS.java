import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static domain.Constants.*;

public class MainDBS {


    public static void main(String[] args) {
        startRegistry();
        registerDispatcher();
    }

    /**
     * Starting a new Registry, on port of DATABASE_PORT
     * Creating a implementation of the DBInterface en bind it to the registry
     */
    public static void startRegistry(){
        try{
            DataBaseInterface databaseImp = new DataBaseConnector();
            Registry registry = LocateRegistry.createRegistry(DATABASE_PORT);
            registry.rebind("dataBaseService", databaseImp);
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
    }

    /**
     * Bind the just created DB to the Dispatcher
     */
    public static void registerDispatcher(){


        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup("dispatchService");
            dispatcherImp.registerDataBaseServer();
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
