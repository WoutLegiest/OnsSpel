package Dispatcher;

import global.interfaces.AppServerInterface;
import global.interfaces.DataBaseInterface;
import global.interfaces.DispatcherInterface;
import global.servers.AppServer;
import global.servers.DataBaseServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;

import static global.domain.Constants.APPSERVER_SERVICE;
import static global.domain.Constants.DATABASE_SERVICE;

public class DispatcherImpl extends UnicastRemoteObject implements DispatcherInterface {

    private ArrayList<DataBaseServer> dbServers;
    private ArrayList<AppServer> appServers;

    private int currentAppPort;
    private int currentDBPort;

    public DispatcherImpl(int appport, int dbport) throws RemoteException {
        dbServers = new ArrayList<>();
        appServers = new ArrayList<>();
        currentAppPort = appport;
        currentDBPort = dbport;
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

            for(DataBaseServer dataBaseServer: dbServers){
                dataBaseServer.notifyOfNewDatabase(dataBaseImpl);
                dataBaseImpl.addDataBaseServer(dataBaseServer.getDataBaseImpl());
            }

            if(dbServers.size()>0){
                dataBaseImpl.updateDataBase();
            }

            dbServers.add(new DataBaseServer(port, ip, dataBaseImpl));

        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("DB toegevoegd aan Dispatch");

    }

    /**
     * Register a AppServer to the dispatcher.
     * Find a databaseServer and add it to the Appserver
     * @param port
     * @param ip
     * @throws RemoteException
     */
    @Override
    public void registerAppServer(int port, String ip) throws RemoteException{

        AppServer appServer;

        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            AppServerInterface appServerInterface = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            appServer = new AppServer(port, ip, appServerInterface);
            appServer.getAppServerImpl().setDataBase(getDataBaseServer());

            appServers.add(appServer);

            //Bind all the appServers to each other

            for(AppServer as : appServers){

                if(as != appServer){
                    as.getAppServerImpl().addOtherAppServer(appServer.getAppServerImpl());
                    appServer.getAppServerImpl().addOtherAppServer(as.getAppServerImpl());
                }

            }

        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        System.out.println("AppServer toegevoegd aan Dispatch");
    }

    @Override
    public AppServer getAppServer(int index) throws RemoteException {
        return appServers.get(index);
    }

    @Override
    public AppServer getAppServer(AppServerInterface appServerInterface) throws RemoteException{

        for(AppServer as: appServers){

            if(as.getAppServerImpl().equals(appServerInterface))
                return as;

        }
        return null;
    }

    @Override
    public DataBaseServer getDataBaseServer() throws RemoteException {

        //Is sorted from smalletst number of AppServers to biggist
        dbServers.sort(Comparator.comparingInt(DataBaseServer::getnAppServers));

        return dbServers.get(0);
    }

    @Override
    public void startDatabaseServer(int port) throws RemoteException {
        try {
           Runtime.getRuntime().exec(
                   new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                           "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                           "\\DataBaseServer_jar\\DataBaseServer.jar " + port}
           );
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    @Override
    public void startAppServer(int port) throws RemoteException {
        try {
            Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                            "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                            "\\AppServer_jar\\AppServer.jar" + port}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void increaseAppPort() throws RemoteException{
        currentAppPort++;
    }

    @Override
    public void increaseDBPort() throws RemoteException{
        currentDBPort++;
    }
}
