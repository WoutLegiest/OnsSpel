package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import domain.Game;
import interfaces.AppServerInterface;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import threads.ScoreBoardThread;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static controller.MainClient.appServerServiceName;
import static controller.SceneController.viewController;
import static controller.MainClient.myIndexNumberServerOne;
import static domain.Constants.*;


public class LobbyController {

    //Player credentials
    private Player player;
    private String token;

    private ScoreBoardThread thread;

    @FXML private Button buttonLogout;

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
    @FXML private TableColumn<Game, String> freeSpots;
    @FXML private TableColumn<Game, Boolean> join;
    @FXML private TableColumn<Game, Boolean> watch;

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
        dateLastGameColum.setCellValueFactory(new PropertyValueFactory<Player, Timestamp>("lastGameDate"));
        dateJoinColum.setCellValueFactory(new PropertyValueFactory<Player, Timestamp>("joinDate"));

        loadT1();

        //Tab2
        gameNr.setCellValueFactory(new PropertyValueFactory<Game,Integer>("idGame"));
        gameOwner.setCellValueFactory(new PropertyValueFactory<Game,String>("ownerUsername"));
        players.setCellValueFactory(new PropertyValueFactory<Game, String>("maxNumberOfPlayers"));
        freeSpots.setCellValueFactory(new PropertyValueFactory<Game, String>("curNumberOfPlayers"));
        //join.setCellValueFactory(new PropertyValueFactory<Game,Button>("join"));

        loadT2();

        //Tab 3

        theme.getItems().removeAll(theme.getItems());
        theme.getItems().addAll("default", "Emma Watson", "Mario Bros");
        theme.getSelectionModel().select("default");

        sizeGame.getItems().removeAll(sizeGame.getItems());
        sizeGame.getItems().addAll("2x3", "4x4", "6x6");
        sizeGame.getSelectionModel().select("4x4");

        numberOfPlayer.getItems().removeAll(numberOfPlayer.getItems());
        numberOfPlayer.getItems().addAll("2 Players", "3 Players", "4 Players");
        numberOfPlayer.getSelectionModel().select("2 Players");

        thread = new ScoreBoardThread(this);
        thread.start();
    }

    public void setCredentials(String username, String token){

        this.token=token;

        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);

            player = appServer.getPlayer(username);
        } catch (RemoteException | NotBoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Player> loadPlayers(){
        ArrayList<Player>allPlayers=new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);

            allPlayers = appServer.getAllPlayers();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allPlayers;
    }

    private ArrayList<Game> loadGames(){
        ArrayList<Game> allGames = new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);

            allGames = appServer.getAllGames();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allGames;
    }

    private void loadT1(){

        if(ranking != null){
            for ( int i = 0; i<ranking.getItems().size(); i++) {
                ranking.getItems().clear();
            }
        }

        ArrayList<Player>allPlayers = loadPlayers();

        //Heeft getters en setters nodig van player voor opbouw ervan !!!!!
        ranking.getItems().addAll(allPlayers);
        ranking.getSortOrder().add(totalPointColum);

    }

    private void loadT2(){

        join.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Game, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Game, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        join.setCellFactory(new Callback<TableColumn<Game, Boolean>, TableCell<Game, Boolean>>() {
            @Override
            public TableCell<Game, Boolean> call(TableColumn<Game, Boolean> personBooleanTableColumn) {
                return new ButtonCellJoin(currentGames);
            }
        });

        watch.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Game, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Game, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        watch.setCellFactory(new Callback<TableColumn<Game, Boolean>, TableCell<Game, Boolean>>() {
            @Override
            public TableCell<Game, Boolean> call(TableColumn<Game, Boolean> personBooleanTableColumn) {
                return new ButtonCellWatch(currentGames);
            }
        });

        if(currentGames!=null){
            for ( int i = 0; i<currentGames.getItems().size(); i++) {
                currentGames.getItems().clear();
            }
        }

        ArrayList<Game>allGames = loadGames();

        currentGames.getItems().addAll(allGames);
    }

    public void refreshT1(){
        loadT1();
    }

    public void refreshT2(){
        loadT2();
    }

    public void createGame(ActionEvent actionEvent){

        int size=Integer.parseInt(String.valueOf(sizeGame.getValue().charAt(0)));
        int numberOfPlayers = Integer.parseInt(String.valueOf(numberOfPlayer.getValue().charAt(0)));
        String themeString=theme.getValue();

        if(themeString.equals("default"))
            themeString="bh";
        else if(themeString.equals("Emma Watson"))
            themeString="EMMA";
        else
            themeString="mario";

        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);

            int GameID = appServer.gameCreated(player.getId(), numberOfPlayers, size);

            Game game = new Game(GameID, player.getId(), numberOfPlayers,1,size);

            ArrayList<Card>gameCards=new ArrayList<>();
            ArrayList<Player>gamePlayers= new ArrayList<>();
            Card cover=null;

            gameCards = appServer.shuffleCards(size, themeString);
            cover= appServer.cardsByTheme(themeString.concat("_cover")).get(0);


            GameExtended gameExtended=new GameExtended(game,gameCards,gamePlayers,player);
            gameExtended.addPlayer(player, myIndexNumberServerOne);

            appServer.gameCreatedExtended(gameExtended);

            viewController.setViewToGame(gameExtended, player, token, cover);

        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }


        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);



        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent actionEvent){

        player = null;
        token = null;

        thread.stopThread();

        try {
            viewController.setViewToLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ButtonCellJoin extends TableCell<Game, Boolean> {

    final Button cellButton = new Button("Remove");

    ButtonCellJoin(final TableView tblView){

        cellButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                System.out.println("Sassy");
            }
        });
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if(!empty){
            setGraphic(cellButton);
        }
    }
}

class ButtonCellWatch extends TableCell<Game, Boolean> {

    final Button cellButton = new Button("Remove");

    ButtonCellWatch(final TableView tblView){

        cellButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                System.out.println("Sassy^2");
            }
        });
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if(!empty){
            setGraphic(cellButton);
        }
    }
}
