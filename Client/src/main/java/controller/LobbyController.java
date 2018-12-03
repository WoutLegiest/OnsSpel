package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import domain.Game;
import interfaces.AppServerInterface;
import interfaces.DispatcherInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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

            player = appServer.getPlayer(username,token);
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

        for ( int i = 0; i<currentGames.getItems().size(); i++) {
            currentGames.getItems().clear();
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


        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        //Aanmaken van game en plaatsen op de database

        Game game = new Game(player.getId(),numberOfPlayers,size);

        ArrayList<Card>gameCards=new ArrayList<>();
        Card cover=null;

        try {
            Registry registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
            AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);

            gameCards = appServer.shuffleCards(size, themeString);
            cover= appServer.cardsByTheme(themeString.concat("_cover")).get(0);
            ArrayList<Player>gamePlayers= new ArrayList<>();

            GameExtended gameExtended=new GameExtended(game,gameCards,gamePlayers,player);
            gameExtended.addPlayer(player);

            appServer.gameCreatedExtended(gameExtended);

            viewController.setViewToGame(gameExtended, player, token, cover);

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
