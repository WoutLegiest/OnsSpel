package DataBaseServer;

import global.interfaces.DataBaseInterface;
import global.interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static global.domain.Constants.*;

public class MainDBS {

    //Beginnen op 27182 enzo
    private static int dbPort;



    public static void main(String[] args) {

        dbPort = Integer.parseInt(args[0]);

        startRegistry();
        registerDispatcher();

    }

    /**
     * Starting a new Registry, on port of DATABASE_PORT
     * Creating a implementation of the DBInterface en bind it to the registry
     */
    public static void startRegistry(){
        try{
            DataBaseInterface databaseImp = new DataBaseImpl();
            databaseImp.settingUpDataBaseServer(dbPort);
            Registry registry = LocateRegistry.createRegistry(dbPort);
            registry.rebind(DATABASE_SERVICE, databaseImp);
            System.out.println("DataBaseServer gekoppeld op poort: " + dbPort);
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
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dispatcherImp.registerDataBaseServer(dbPort, IP);
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
