package controller;

import callBack.ClientImpl;
import interfaces.AppServerInterface;
import interfaces.ClientInterface;
import interfaces.DispatcherInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import servers.AppServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static controller.SceneController.viewController;
import static domain.Constants.*;

public class MainClient extends Application {

    static AppServer appServer;

    static int myIndexNumberServerOne;
    static int getMyIndexNumberServerBackup;

    static ClientImpl clientImpl;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Dispatcher aanroepen en deze geeft string terug van de appserver
        //Deze kan dan gebruikt worden voor registratie enzo

        Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
        DispatcherInterface dispatch = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

        appServer = dispatch.getAppServer();

        ClientInterface callbackObj = new ClientImpl();

        try {
            Registry appRegistry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) appRegistry.lookup(APPSERVER_SERVICE);

            myIndexNumberServerOne=appServer.registerForCallback(callbackObj);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        clientImpl= (ClientImpl) callbackObj;
        viewController.setViewToLogin();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
