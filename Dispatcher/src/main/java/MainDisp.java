import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static domain.Constants.DISPATCH_PORT;

public class MainDisp {

    public static void main(String[] args) throws RemoteException {

        DispatcherInterface dispatcherImp = new Dispatcher();
        Registry registry = LocateRegistry.createRegistry(DISPATCH_PORT);
        registry.rebind("dispatchService", dispatcherImp);
        System.out.println("Dispatch gekoppeld op poort: " + DISPATCH_PORT);
    }
}
