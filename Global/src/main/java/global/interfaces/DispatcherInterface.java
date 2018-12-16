package global.interfaces;

import global.servers.AppServer;
import global.servers.DataBaseServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInterface extends Remote {

    void registerDataBaseServer(int port, String ip) throws RemoteException;

    void registerAppServer(int port, String ip) throws RemoteException;

    AppServer getAppServer(int index) throws RemoteException;

    AppServer getAppServer(AppServerInterface appServerInterface) throws RemoteException;

    DataBaseServer getDataBaseServer() throws RemoteException;

    void startDatabaseServer(int port) throws RemoteException;

    void startAppServer(int port) throws RemoteException;

    void increaseAppPort() throws RemoteException;

    void increaseDBPort() throws RemoteException;
}
