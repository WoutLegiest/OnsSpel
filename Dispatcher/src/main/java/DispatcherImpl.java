import domain.Card;
import domain.Game;
import domain.Player;
import exceptions.UserExistsException;
import interfaces.AppServerInterface;
import interfaces.DataBaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static domain.Constants.APPSERVER_PORT;
import static domain.Constants.DATABASE_PORT;

public class DispatcherImpl extends UnicastRemoteObject implements DispatcherInterface {

    private ArrayList<String> dataBaseServices;
    private ArrayList<String> appServerServices;

    public DispatcherImpl() throws RemoteException {
        dataBaseServices = new ArrayList<>();
        appServerServices = new ArrayList<>();
    }

    /**
     * Find the registry of the DB
     * Make a new DB and bind it to the Registry Service
     * @throws RemoteException
     */
    @Override
    public void registerDataBaseServer(String serviceName) throws RemoteException{

        dataBaseServices.add(serviceName);
        System.out.println("DB toegevoegd aan Dispatch");
    }

    @Override
    public void registerAppServer(String serviceName) throws RemoteException{

        appServerServices.add(serviceName);
        System.out.println("AppServer toegevoegd aan Dispatch");
    }

    public String getAppServerServiceName() throws RemoteException{
        return appServerServices.get(0);
    }

    public String getDataBaseServerServiceName() throws RemoteException{
        return dataBaseServices.get(0);
    }




}
