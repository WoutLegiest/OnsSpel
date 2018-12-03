package controller;

import callBack.ClientImpl;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import interfaces.AppServerInterface;
import interfaces.ClientInterface;
import interfaces.DispatcherInterface;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static controller.SceneController.viewController;
import static domain.Constants.*;

public class MainClient extends Application {

    static String appServerServiceName;

    static int myIndexNumberServerOne;

    static ClientImpl clientImpl;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Dispatcher aanroepen en deze geeft string terug van de appserver
        //Deze kan dan gebruikt worden voor registratie enzo

        Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
        DispatcherInterface dispatch = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

        appServerServiceName = dispatch.getAppServerServiceName();

        ClientInterface callbackObj = new ClientImpl();
        try {
            Registry appRegistry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) appRegistry.lookup(appServerServiceName);
            myIndexNumberServerOne=appServer.registerForCallback(callbackObj);


        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        clientImpl= (ClientImpl) callbackObj;
        viewController.setViewToLogin();



        //TODO: Hier als dispachter oproepen om de doc van op de server naar de client te kopieren &&
        //Enkel de methoden in de interface zetten die door de registry moeten worden gebruikt


    }


    public static void main(String[] args) {
        launch(args);
    }
}
