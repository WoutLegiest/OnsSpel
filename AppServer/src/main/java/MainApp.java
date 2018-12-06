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

    private static int appPort;

    public static void main(String[] args) throws RemoteException {

        appPort = Integer.parseInt(args[0]);

        startRegistry();
        registerDispatcher();

        try {
            Registry registry = LocateRegistry.getRegistry(IP, appPort);
            AppServerInterface appServerInterface = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);
            appServerInterface.sendMessage("Ik kom van hier" + appPort);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start up the registry of the AppServer en initionlasie and bound a first appServer to it
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
