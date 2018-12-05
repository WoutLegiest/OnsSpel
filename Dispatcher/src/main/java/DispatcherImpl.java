import interfaces.AppServerInterface;
import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;
import servers.AppServer;
import servers.DataBaseServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static domain.Constants.*;

public class DispatcherImpl extends UnicastRemoteObject implements DispatcherInterface {

    private ArrayList<DataBaseServer> dbServers;
    private ArrayList<AppServer> appServers;

    public DispatcherImpl() throws RemoteException {
        dbServers = new ArrayList<>();
        appServers = new ArrayList<>();
    }

    /**
     * Find the registry of the DB
     * Make a new DB and bind it to the Registry Service
     * @throws RemoteException
     */
    @Override
    public void registerDataBaseServer(int port, String ip) throws RemoteException{

        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            DataBaseInterface dataBaseImpl = (DataBaseInterface) registry.lookup(DATABASE_SERVICE);
            dbServers.add(new DataBaseServer(port, ip, dataBaseImpl));

        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        System.out.println("DB toegevoegd aan Dispatch");
    }

    @Override
    public void registerAppServer(int port, String ip) throws RemoteException{

        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            AppServerInterface appServerInterface = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);
            appServers.add(new AppServer(port, ip, appServerInterface));

        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        System.out.println("AppServer toegevoegd aan Dispatch");
    }

    @Override
    public AppServer getAppServerServiceName() throws RemoteException {
        return appServers.get(0);
    }

    @Override
    public DataBaseServer getDataBaseServerServiceName() throws RemoteException {
        return dbServers.get(0);
    }


}
