package controller;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static controller.SceneController.viewController;
import static domain.Constants.*;

public class MainClient extends Application {

    static String appServerServiceName;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Dispatcher aanroepen en deze geeft string terug van de appserver
        //Deze kan dan gebruikt worden voor registratie enzo

        Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
        DispatcherInterface dispatch = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

        appServerServiceName = dispatch.getAppServerServiceName();

        viewController.setViewToLogin();

        //TODO: Hier als dispachter oproepen om de doc van op de server naar de client te kopieren &&
        //Enkel de methoden in de interface zetten die door de registry moeten worden gebruikt


    }


    public static void main(String[] args) {
        launch(args);
    }

}
