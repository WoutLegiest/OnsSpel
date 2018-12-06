import domain.Constants;
import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static domain.Constants.DISPATCH_PORT;

public class MainDisp {

    /**
     * Makes a registery and bind a dispatcher interface to it.
     * @param args
     */
    public static void main(String[] args)  {

        new Constants();

        try {
            DispatcherInterface dispatcherImp = new DispatcherImpl();
            Registry registry = LocateRegistry.createRegistry(DISPATCH_PORT);
            registry.rebind("dispatchService", dispatcherImp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Dispatch gekoppeld op poort: " + DISPATCH_PORT);
    }
}
