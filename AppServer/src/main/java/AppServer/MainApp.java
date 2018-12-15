package AppServer;

import global.interfaces.AppServerInterface;
import global.interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static global.domain.Constants.*;

public class MainApp {

    private static int appPort;

    //beginnen van af 6626
    public static void main(String[] args) throws RemoteException {

        appPort = Integer.parseInt(args[0]);

        startRegistry();
        registerDispatcher();
    }

    /**
     * Start up the registry of the AppServer en initialise and bound a first appServer to it
     */
    public static void startRegistry(){
        try{
            AppServerInterface appServerImp = new AppServerImpl();
            Registry registry = LocateRegistry.createRegistry(appPort);

            registry.rebind(APPSERVER_SERVICE, appServerImp);
            System.out.println("AppServer gekoppeld op poort: " + appPort);
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
    }

    /**
     * Method who registers the AppServer at the dispatcher.
     */
    public static void registerDispatcher(){

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dispatcherImp.registerAppServer(appPort, IP);
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
