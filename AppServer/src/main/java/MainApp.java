import interfaces.AppServerInterface;
import interfaces.DispatcherInterface;

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
