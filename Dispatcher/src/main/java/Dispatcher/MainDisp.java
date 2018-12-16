package Dispatcher;

import global.domain.Constants;
import global.interfaces.DispatcherInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static global.domain.Constants.DISPATCH_PORT;

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
            registry.rebind("dispatchService", dispatcherImp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Dispatch gekoppeld op poort: " + DISPATCH_PORT);

        try {
            Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                            "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                            "\\DataBaseServer_jar\\DataBaseServer.jar " + dbport}
            );

            Thread.sleep(1500);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar " +
                            "C:\\Users\\woute\\Dropbox\\IdeaProjects\\OnsSpel\\out\\artifacts" +
                            "\\AppServer_jar\\AppServer.jar " + appport}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
