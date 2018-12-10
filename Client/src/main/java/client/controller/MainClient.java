package client.controller;

import client.callBack.ClientImpl;
import global.interfaces.AppServerInterface;
import global.interfaces.DispatcherInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import global.servers.AppServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static global.domain.Constants.*;

@SuppressWarnings("Duplicates")
public class MainClient extends Application {

    static AppServer appServer;

    static int index;
    static int myIndexNumberServerOne;
    static int getMyIndexNumberServerBackup;

    static ClientImpl clientImpl;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Dispatcher aanroepen en deze geeft string terug van de appserver
        //Deze kan dan gebruikt worden voor registratie enzo

        Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
        DispatcherInterface dispatch = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

        appServer = dispatch.getAppServer(index);

        ClientImpl callbackObj = new ClientImpl();

        try {
            Registry appRegistry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) appRegistry.lookup(APPSERVER_SERVICE);

            myIndexNumberServerOne=appServer.registerForCallback(callbackObj);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        clientImpl = callbackObj;
        SceneController.viewController.setViewToLogin();
    }

    public static void main(String[] args) {

        index = Integer.parseInt(args[0]);
        launch(args);
    }

    public static AppServer getAppServer() {
        return appServer;
    }

    public static void migrateToServer(AppServer appServer) {

        MainClient.appServer = appServer;

        try {
            Registry appRegistry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServerImpl = (AppServerInterface) appRegistry.lookup(APPSERVER_SERVICE);

            myIndexNumberServerOne=appServerImpl.registerForCallback(clientImpl);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }


    }
}
