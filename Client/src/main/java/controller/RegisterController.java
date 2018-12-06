package controller;

import exceptions.UserExistsException;
import interfaces.AppServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

import static controller.MainClient.appServer;
import static controller.SceneController.viewController;
import static domain.Constants.*;


public class RegisterController {

    @FXML private Button buttonBackToLogin;
    @FXML private Button buttonRegister;
    @FXML private TextField textUsername;
    @FXML private TextField textEmail;
    @FXML private PasswordField textPassword;

    public RegisterController() {
    }

    public void register(ActionEvent actionEvent)  {

        String username=textUsername.getText();
        String password=textPassword.getText();
        String email=textEmail.getText();

        String token = null;

        try{
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            token = appServer.registerPlayer(username,password,email);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registy completed");
            alert.setHeaderText(null);
            alert.setContentText("Sessie token: " + token + "\n Have a happy play");

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

            alert.showAndWait();

            viewController.setViewToLobby(username, token);


        } catch (UserExistsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Name taken");
            alert.setHeaderText(null);
            alert.setContentText("Name is already taken \n Choose another name");

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

            alert.showAndWait();
        }
        catch (NotBoundException | SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    public void toLogin(ActionEvent ae){
        try {
            viewController.setViewToLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
