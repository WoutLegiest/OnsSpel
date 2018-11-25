import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static domain.Constants.*;

public class MainDBS {


    public static void main(String[] args) {
        startRegistry("dataBaseService1");
        registerDispatcher("dataBaseService1");
        //TODO: Meerdere Databases opstarten door methode meerdere keren uit te voeren
    }

    /**
     * Starting a new Registry, on port of DATABASE_PORT
     * Creating a implementation of the DBInterface en bind it to the registry
     */
    public static void startRegistry(String serviceName){
        try{
            DataBaseInterface databaseImp = new DataBaseImpl();
            Registry registry = LocateRegistry.createRegistry(DATABASE_PORT);
            registry.rebind(serviceName, databaseImp);
            System.out.println("DataBaseServer gekoppeld op poort: " + DATABASE_PORT);

        }
        catch(RemoteException re){
            re.printStackTrace();
        }
    }

    /**
     * Bind the just created DB to the Dispatcher
     */
    public static void registerDispatcher(String serviceName){

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dispatcherImp.registerDataBaseServer(serviceName);
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
