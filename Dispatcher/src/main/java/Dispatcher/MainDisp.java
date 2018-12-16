package Dispatcher;

import global.domain.Constants;
import global.interfaces.DispatcherInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static global.domain.Constants.*;

@SuppressWarnings("Duplicates")
public class MainDisp {

    /**
     * Makes a registery and bind a dispatcher interface to it.
     * @param args
     */
    public static void main(String[] args) {

        int dbport = 27812;
        int appport = 6626;

        try {
            DispatcherInterface dispatcherImp = new DispatcherImpl(appport,dbport);
            Registry registry = LocateRegistry.createRegistry(DISPATCH_PORT);
            registry.rebind(DISPATCH_SERVICE, dispatcherImp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Dispatch gekoppeld op poort: " + DISPATCH_PORT);


        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

            dispatcherImp.startDatabaseServer();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dispatcherImp.startAppServer();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dispatcherImp.startDatabaseServer();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dispatcherImp.startAppServer();

        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }
}
