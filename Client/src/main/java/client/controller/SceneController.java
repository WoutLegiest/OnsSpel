package client.controller;

import global.domain.Card;
import global.domain.GameExtended;
import global.domain.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import static global.domain.Constants.ICON_PATH;

/**
 * Final class that controlles the scene of the GUI
 */
public final class SceneController {

    public static final SceneController viewController = new SceneController();
    private final Stage GUI = new Stage();

    public SceneController() {}

    public void setViewToLogin() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene newScene = new Scene(root, 320, 320);
        GUI.setScene(newScene);
        GUI.setTitle("Log in");
        GUI.setResizable(false);
        GUI.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

        GUI.show();

    }

    public void setViewToRegister() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
        Scene newScene = new Scene(root, 321, 320);
        GUI.setScene(newScene);
        GUI.setTitle("Register");

        GUI.show();

    }

    public void setViewToLobby(String username,String sessieToken) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/fxml/lobby.fxml"));
        Parent root = fxmlLoader.load();
        LobbyController controller =fxmlLoader.getController();
        controller.setCredentials(username,sessieToken);
        Scene newScene = new Scene(root, 650, 600);

        Platform.runLater(() -> {
            GUI.setScene(newScene);
            GUI.setTitle("Lobby");

            GUI.show();
        });
    }

    public void setViewToGame(GameExtended gameExtended, Player player, String token, Card cover) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/fxml/game.fxml"));
        Parent root = fxmlLoader.load();
        GameController controller =fxmlLoader.getController();
        controller.setCredentials(gameExtended,player,token,cover);
        GUI.setTitle("Memory Game");
        Scene newScene = new Scene(root, 900, 600);
        GUI.setScene(newScene);

        controller.checkBeginGame();

        GUI.show();
    }
}
