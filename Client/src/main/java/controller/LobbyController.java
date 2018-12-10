package controller;

import domain.*;
import interfaces.AppServerInterface;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import servers.AppServer;
import threads.ScoreBoardThread;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static controller.MainClient.*;
import static controller.MainClient.appServer;
import static controller.SceneController.viewController;
import static domain.Constants.*;

@SuppressWarnings("Duplicates")
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

        ranking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadT1();

        //Tab2
        gameNr.setCellValueFactory(new PropertyValueFactory<Game,Integer>("idGame"));
        gameOwner.setCellValueFactory(new PropertyValueFactory<Game,String>("ownerUsername"));
        players.setCellValueFactory(new PropertyValueFactory<Game, String>("maxNumberOfPlayers"));
        freeSpots.setCellValueFactory(new PropertyValueFactory<Game, String>("curNumberOfPlayers"));

        currentGames.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadT2();

        //Tab 3

        theme.getItems().removeAll(theme.getItems());
        theme.getItems().addAll("default", "Emma Watson", "Mario Bros");
        theme.getSelectionModel().select("default");

        sizeGame.getItems().removeAll(sizeGame.getItems());
        sizeGame.getItems().addAll("2x2", "4x4", "6x6");
        sizeGame.getSelectionModel().select("4x4");

        numberOfPlayer.getItems().removeAll(numberOfPlayer.getItems());
        numberOfPlayer.getItems().addAll("2 Players", "3 Players", "4 Players");
        numberOfPlayer.getSelectionModel().select("2 Players");

        thread = new ScoreBoardThread(this);
        thread.start();
    }

    /**
     * Set the credentials that are cached at the controller
     * @param username
     * @param token
     */
    public void setCredentials(String username, String token){

        this.token=token;

        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            player = appServer.getPlayer(username);
        } catch (RemoteException | NotBoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Communicates with the appServer to get all the players, to fill the scoreboard
     * @return a list of all the players
     */
    private ArrayList<Player> loadPlayers(){
        ArrayList<Player>allPlayers=new ArrayList<>();

        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            allPlayers = appServer.getAllPlayers();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allPlayers;
    }

    /**
     * Communicates with the appServer to get all the games, to fill the scoreboard
     * @return a list of all the games
     */
    private ArrayList<Game> loadGames(){
        ArrayList<Game> allGames = new ArrayList<>();

        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            allGames = appServer.getAllGamesFromDB();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return allGames;
    }

    /**
     * Fill the tableview with the data
     */
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

    /**
     * Fill the tableview with the data
     */
    private void loadT2(){

        if(currentGames!=null){
            for ( int i = 0; i<currentGames.getItems().size(); i++) {
                currentGames.getItems().clear();
            }
        }

        ArrayList<Game>allGames = loadGames();

        currentGames.getItems().addAll(allGames);

        join.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        join.setCellFactory(personBooleanTableColumn -> new ButtonCellJoin(currentGames, this, player.getUsername()));

        watch.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        watch.setCellFactory(personBooleanTableColumn -> new ButtonCellWatch(currentGames, this));

    }

    /**
     * Public method that calls the private method to load the players tableview
     */
    public void refreshT1(){
        loadT1();
    }

    /**
     * Public method that calls the private method to load the game tableview
     */
    public void refreshT2(){
        loadT2();
    }

    /**
     * Create a game, push it to the DB, creates a gameExtended a push it to the AppServer.
     * Set the GUI to the gameview
     * @param actionEvent
     */
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
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServerImpl = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            int GameID = appServerImpl.gameCreated(player.getId(), numberOfPlayers, size);

            Game game = new Game(GameID, player.getId(), numberOfPlayers,0,size);

            ArrayList<Card>gameCards=new ArrayList<>();
            ArrayList<GamePlayer>gamePlayers= new ArrayList<>();
            Card cover=null;

            gameCards = appServerImpl.shuffleCards(size, themeString);
            cover= appServerImpl.getCardsByTheme(themeString.concat("_cover")).get(0);

            GamePlayer gamePlayer= new GamePlayer(player);
            GameExtended gameExtended=new GameExtended(game,themeString,gameCards,gamePlayers,gamePlayer);
            gameExtended.addPlayer(gamePlayer, myIndexNumberServerOne);

            appServerImpl.gameCreatedExtended(gameExtended);

            viewController.setViewToGame(gameExtended, player, token, cover);

        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to add a player to a game, adds the player to the other gameExtended object, on the other clients and server.
     * retrive a gameExtended and use it to build the GUI.
     * Couple this method to the buttons in the tableview in the lobby
     * @param gameID The game you want to join
     */
    void joinGame(int gameID){
        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServerImpl = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            AppServer gameAppServer = appServerImpl.findServer(gameID);

            //Save the appServer on the client side, if it is different
            if(!appServer.equals(gameAppServer)){
                migrateToServer(gameAppServer);
            }

            GameExtended gameExtended = gameAppServer.getAppServerImpl().findGameExtended(gameID);

            //---------------Add current Player----------------//

            appServerImpl.addPlayer(player, myIndexNumberServerOne, gameExtended.getGame().getIdGame());

            GamePlayer gamePlayer = new GamePlayer(player);
            gameExtended.addPlayer(gamePlayer,myIndexNumberServerOne);

            Card cover = appServer.getAppServerImpl().getCardsByTheme(gameExtended.getTheme().concat("_cover")).get(0);


            viewController.setViewToGame(gameExtended, player, token, cover);

        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to add a watchter a game. Method is couple to the button in the tableview of the games
     * @param gameID The id of the game you want to watch
     */
    void watchGame(int gameID){

        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServerImpl = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

            AppServer gameAppServer = appServerImpl.findServer(gameID);

            //Save the appServer on the client side, if it is different
            if(!appServer.equals(gameAppServer)){
                migrateToServer(gameAppServer);
            }

            //Watcher toevoegen

            appServerImpl.registerWatcher(gameID, myIndexNumberServerOne);

            GameExtended gameExtended = gameAppServer.getAppServerImpl().findGameExtended(gameID);

            Card cover = appServer.getAppServerImpl().getCardsByTheme(gameExtended.getTheme().concat("_cover")).get(0);

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

/**
 * Custom created class to embed a button inside a TableCell
 */
class ButtonCellJoin extends TableCell<Game, Boolean> {

    private final Button cellButton = new Button("Join");
    private LobbyController lc;
    private String usr;

    ButtonCellJoin(final TableView<Game> tblView, LobbyController lobbyC, String user){

        this.lc = lobbyC;
        usr= user;

        cellButton.setOnAction(t ->
            lc.joinGame(getTableView().getItems().get(getIndex()).getIdGame()));
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if(!empty){

            //Check of game vol zit
            if(getTableView().getItems().get(getIndex()).getCurNumberOfPlayers()==getTableView().getItems().get(getIndex()).getMaxNumberOfPlayers()){
                cellButton.setDisable(true);
                cellButton.setText("Full");
            }

            if(usr.equals(getTableView().getItems().get(getIndex()).getOwnerUsername())){
                cellButton.setDisable(true);
                cellButton.setText("Own game");
            }
            setGraphic(cellButton);
        }
    }
}

class ButtonCellWatch extends TableCell<Game, Boolean> {

    private final Button cellButton = new Button("Watch");
    private LobbyController lc;

    ButtonCellWatch(final TableView tblView, LobbyController lobbyC){

        this.lc = lobbyC;

        cellButton.setOnAction(t ->
                lc.watchGame(getTableView().getItems().get(getIndex()).getIdGame()));

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
