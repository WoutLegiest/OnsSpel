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

        startRegistry();
        registerDispatcher();

    }

    /**
     * Start up the registry of the AppServer en initionlasie and bound a first appServer to it
     */
    public static void startRegistry(){
        try{
            AppServerInterface appServerImp = new AppServerImpl();
            Registry registry = LocateRegistry.createRegistry(APPSERVER_PORT);
            registry.rebind(APPSERVER_SERVICE, appServerImp);
            System.out.println("AppServer gekoppeld op poort: " + APPSERVER_PORT);
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
            dispatcherImp.registerAppServer(APPSERVER_PORT, IP);
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }


}
