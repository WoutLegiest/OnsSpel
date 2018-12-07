package controller;

import interfaces.AppServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static controller.MainClient.appServer;
import static controller.SceneController.viewController;
import static domain.Constants.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")
public class LoginController {


    @FXML private Button buttonLogin;
    @FXML private Button buttonRegister;
    @FXML private TextField textUsername;
    @FXML private PasswordField textPassword;

    @FXML
    public void initialize(){
        buttonLogin.setDefaultButton(true);
    }

    public void login(ActionEvent actionEvent){

        try{
            String username=textUsername.getText();
            String password=textPassword.getText();

            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);


            String token = appServer.authenticatePlayer(username,password);

            if(token != null){
                /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Succeeded");
                alert.setHeaderText(null);
                alert.setContentText("Sessie token: " + token + "\n Have a happy play");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

                alert.showAndWait();*/

                viewController.setViewToLobby(username, token);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Faild");
                alert.setHeaderText(null);
                alert.setContentText("Username and/or password wrong \n \n If you don't hava an account " +
                        "\n Please click on 'Register'");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(SceneController.class.getResourceAsStream(ICON_PATH)));

                alert.showAndWait();
            }

        } catch (NotBoundException | SQLException | IOException re) {
            re.printStackTrace();
        }
    }

    public void toRegister(ActionEvent actionEvent) {
        try {
            viewController.setViewToRegister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
