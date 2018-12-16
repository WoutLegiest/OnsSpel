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

    private int nextAppPort;
    private int nextDBPort;

    public DispatcherImpl(int appport, int dbport) throws RemoteException {
        dbServers = new ArrayList<>();
        appServers = new ArrayList<>();
        nextAppPort = appport;
        nextDBPort = dbport;
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

    /**
     * Get the appServer withs is locaded at that index
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public AppServer getAppServer(int index) throws RemoteException {
        return appServers.get(index);
    }

    /**
     * Get a AppServer from a appServerInterface
     * @param appServerInterface
     * @return
     * @throws RemoteException
     */
    @Override
    public AppServer getAppServer(AppServerInterface appServerInterface) throws RemoteException{

        for(AppServer as: appServers){

            if(as.getAppServerImpl().equals(appServerInterface))
                return as;

        }
        return null;
    }

    /**
     * Get the Database with the smallest amout of AppServer connected to
     * @return
     * @throws RemoteException
     */
    @Override
    public DataBaseServer getDataBaseServer() throws RemoteException {

        //Is sorted from smalletst number of AppServers to biggist
        dbServers.sort(Comparator.comparingInt(DataBaseServer::getnAppServers));

        return dbServers.get(0);
    }

    /**
     * Start up a databaseServer, using de cmd in windows
     * @throws RemoteException
     */
    @Override
    public void startDatabaseServer() throws RemoteException {
        try {
           Runtime.getRuntime().exec(
                   new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                           "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                           "\\DataBaseServer_jar\\DataBaseServer.jar " + nextDBPort}
           );
        } catch (IOException e) {
           e.printStackTrace();
        }
        increaseDBPort();
    }

    /**
     * Checks if a AppServer haze more than 20 games, if there are more,
     * starts up a new Server and gives back the appServer object. If not, return null
     * @param port
     * @return
     * @throws RemoteException
     */
    @Override
    public AppServer checkAppServer(int port) throws RemoteException{

        int nextport=0;

        for(AppServer as: appServers){

            if (as.getPort() == port){
                if (as.getnGames() >= 20){

                    nextport = nextAppPort;

                    startAppServer();
                }
            }
        }

        for(AppServer as: appServers){

            if (as.getPort() == nextport){
              return as;
            }
        }

        return null;

    }

    /**
     * Start up a AppServer using the cmd of Windows
     * @throws RemoteException
     */
    @Override
    public void startAppServer() throws RemoteException {
        try {
            Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                            "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                            "\\AppServer_jar\\AppServer.jar " + nextAppPort}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        increaseAppPort();
    }

    /**
     * Increase the number of Games, kept in the appServer object
     * @param port
     * @throws RemoteException
     */
    @Override
    public void addGame(int port) throws RemoteException {

        for(AppServer as: appServers){

            if (as.getPort() == port){

                as.addGame();
            }
        }
    }

    private void increaseAppPort() throws RemoteException{
        nextAppPort++;
    }

    private void increaseDBPort() throws RemoteException{
        nextDBPort++;
    }
}
