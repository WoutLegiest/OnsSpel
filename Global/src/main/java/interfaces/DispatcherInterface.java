package interfaces;

import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DispatcherInterface extends Remote {

    void registerDataBaseServer(String serviceName) throws RemoteException;
    void registerAppServer(String serviceName) throws RemoteException;

    String getAppServerServiceName() throws RemoteException;
    String getDataBaseServerServiceName() throws RemoteException;

}
