package interfaces;

import servers.AppServer;
import servers.DataBaseServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInterface extends Remote {

    void registerDataBaseServer(int port, String ip) throws RemoteException;
    void registerAppServer(int port, String ip) throws RemoteException;

    AppServer getAppServerServiceName() throws RemoteException;
    DataBaseServer getDataBaseServerServiceName() throws RemoteException;
}
