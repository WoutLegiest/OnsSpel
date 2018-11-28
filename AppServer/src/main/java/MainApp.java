import interfaces.AppServerInterface;
import interfaces.DispatcherInterface;
import javafx.scene.image.Image;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static domain.Constants.*;

public class MainApp {

    public static void main(String[] args) throws RemoteException {

        startRegistry("appService1");
        registerDispatcher("appService1");

    }

    //Is het de bedoeling dan dat elke appserver\dbs gekoppeld is aan zijn eigen Service ?

    /**
     * Start up the registry of the AppServer en initionlasie and bound a first appServer to it
     * @param serviceName
     */
    public static void startRegistry(String serviceName){
        //TODO: service naam veranderen naar int, zo zijn de 6** appSerivce en dan 4** de databasen

        try{
            AppServerInterface appServerImp = new AppServerImpl();
            Registry registry = LocateRegistry.createRegistry(APPSERVER_PORT);
            registry.rebind(serviceName, appServerImp);
            System.out.println("AppServer gekoppeld op poort: " + APPSERVER_PORT);
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
    }

    /**
     * Generates a new AppServer and bind to the Appserver registry
     * @param serviceName name of the appServer
     */
    public static void startUpNewServer(String serviceName){

        try {
            AppServerInterface appServerImp = new AppServerImpl();
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            registry.rebind(serviceName, appServerImp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method who registers the AppServer at the dispatcher.
     * @param serviceName name of the appServer
     */
    public static void registerDispatcher(String serviceName){

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dispatcherImp.registerAppServer(serviceName);
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
