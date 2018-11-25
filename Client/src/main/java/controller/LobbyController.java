package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import domain.Game;
import interfaces.DispatcherInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;

import static controller.SceneController.viewController;
import static domain.Constants.DISPATCH_PORT;
import static domain.Constants.IP;


public class LobbyController {

    //Tab 1
    @FXML private TableView<Player> ranking;

    @FXML private TableColumn<Player, String> usernameColum;
    @FXML private TableColumn<Player,Integer> totalPointColum;
    @FXML private TableColumn<Player,Timestamp> dateLastGameColum;
    @FXML private TableColumn<Player,Timestamp> dateJoinColum;

    //Tab 2
    @FXML private TableView<Game> currentGames;

    @FXML private TableColumn<Game, Integer> gameNr;
    @FXML private TableColumn<Game, String> gameOwner;
    @FXML private TableColumn<Game, String> players;
    @FXML private TableColumn<Game, Button> join;


    //Tab 3
    @FXML private Button buttonStartGame;
    @FXML private ChoiceBox<String> theme;
    @FXML private ChoiceBox<String> sizeGame;
    @FXML private ChoiceBox<String> numberOfPlayer;


    @FXML
    public void initialize() {

        //Tab 1
        usernameColum.setCellValueFactory(new PropertyValueFactory<Player, String>("username"));
        totalPointColum.setCellValueFactory(new PropertyValueFactory<Player, Integer>("totalScore"));
        //dateLastGameColum.setCellValueFactory(new PropertyValueFactory<Player, Timestamp>("username"));
        dateJoinColum.setCellValueFactory(new PropertyValueFactory<Player, Timestamp>("joinDate"));

        loadT1();

        //Tab2
        gameNr.setCellValueFactory(new PropertyValueFactory<Game,Integer>("idGame"));
        gameOwner.setCellValueFactory(new PropertyValueFactory<Game,String>("owner"));
        players.setCellValueFactory(new PropertyValueFactory<Game, String>("maxNumberOfPlayers"));
        join.setCellValueFactory(new PropertyValueFactory<Game,Button>("join"));

        //Tab 3

        theme.getItems().removeAll(theme.getItems());
        theme.getItems().addAll("default",
                "Emma Watson", "Harry Potter");
        theme.getSelectionModel().select("default");

        sizeGame.getItems().removeAll(sizeGame.getItems());
        sizeGame.getItems().addAll("2x3",
                "4x4", "6x6");
        sizeGame.getSelectionModel().select("4x4");

        numberOfPlayer.getItems().removeAll(numberOfPlayer.getItems());
        numberOfPlayer.getItems().addAll("2 Players",
                "3 Players", "4 Players");
        numberOfPlayer.getSelectionModel().select("2 Players");


    }

    private ArrayList<Player> loadPlayers(){
        ArrayList<Player>allPlayers=new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatch = (DispatcherInterface) registry.lookup("dispatchService");

            allPlayers = dispatch.getAllPlayers();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allPlayers;
    }

    private ArrayList<Game> loadGames(){
        ArrayList<Game> allGames = new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatch = (DispatcherInterface) registry.lookup("dispatchService");

            allGames = dispatch.getAllGames();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allGames;
    }

    private void loadT1(){
        ArrayList<Player>allPlayers = loadPlayers();

        //Heeft getters en setters nodig van player voor opbouw ervan !!!!!
        ranking.getItems().addAll(allPlayers);

        ranking.getSortOrder().add(totalPointColum);

    }

    public void loadT2(){
        ArrayList<Game>allGames = loadGames();

        currentGames.getItems().addAll(allGames);


    }

    public void refreshT1(ActionEvent actionEvent){
        loadT1();
    }

    public void refreshT2(ActionEvent actionEvent){
        loadT2();
    }

    public void createGame(ActionEvent actionEvent){
        int size=Integer.parseInt(String.valueOf(sizeGame.getValue().charAt(0)));
        int numberOfPlayers = Integer.parseInt(String.valueOf(numberOfPlayer.getValue().charAt(0)));

        Game game = new Game(0,numberOfPlayers,size);

        ArrayList<Card>gameCards=new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatch = (DispatcherInterface) registry.lookup("dispatchService");

            gameCards = dispatch.shuffleCards(size);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        ArrayList<Player>gamePlayers= new ArrayList<>();

        GameExtended gameExtended=new GameExtended(game,gameCards,gamePlayers);

        try {
            viewController.setViewToGame();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("(>)");
        String lastItem;

        public XCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(lastItem + " : " + event);
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item != null ? item : "<null>");
                setGraphic(hbox);
            }
        }
    }
}
