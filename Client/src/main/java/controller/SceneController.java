package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static domain.Constants.ICON_PATH;

public final class SceneController {

    public static final SceneController viewController = new SceneController();

    private static Scene openScene;

    public SceneController() {}

    public void setViewToLogin() throws IOException {

        if(!(openScene ==null))
            openScene.getWindow().hide();

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene newScene = new Scene(root, 320, 320);
        stage.setScene(newScene);
        stage.setTitle("Log in");
        stage.setResizable(false);
        stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

        stage.show();

        openScene=newScene;

    }

    public void setViewToRegister() throws IOException {

        //Toedoen huidige scene
        openScene.getWindow().hide();

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/register.fxml"));
        Scene newScene = new Scene(root, 321, 320);
        stage.setScene(newScene);
        stage.setTitle("Register");
        stage.setResizable(false);
        stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

        stage.show();

        openScene=newScene;

    }

    public void setViewToLobby(String username) throws IOException {

        //Toedoen huidige scene
        openScene.getWindow().hide();

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/lobby.fxml"));
        Scene newScene = new Scene(root, 600, 600);
        stage.setScene(newScene);
        stage.setTitle("Lobby");
        stage.setResizable(false);
        stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

        stage.show();

        openScene=newScene;

    }

    public void setViewToGame() throws IOException {

        //Toedoen huidige scene
        openScene.getWindow().hide();

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("../fxml/game.fxml"));
        stage.setTitle("Memory Game");
        Scene newScene = new Scene(root, 450, 450);
        stage.setScene(newScene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

        stage.show();

        openScene=newScene;
    }


}
