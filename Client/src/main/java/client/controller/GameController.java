package client.controller;

import global.domain.*;
import global.interfaces.AppServerInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static client.controller.MainClient.*;
import static client.controller.SceneController.viewController;
import static global.domain.Constants.*;

@SuppressWarnings("Duplicates")
public class GameController {

    //Game
    private GameExtended game;

    //player
    private Player player;
    private GamePlayer gamePlayer;
    private String token;

    //Game specifics
    private Card cover;
    private Image coverImage;
    private double cellHeight;
    private double cellWidth;

    //Grid variables
    private ArrayList<Button> buttons;
    private ArrayList<ImageView> images;

    //Turn
    private Turn turn;
    private Runnable taskFinishTurn;

    //Chat
    private StringBuilder stringBuilder;

    //Grid
    @FXML private GridPane gridGame;

    //label
    @FXML private Label turnLabel;

    //score table
    @FXML private TableView<GamePlayer> scoreTableGame;
        //columns
    @FXML private TableColumn<GamePlayer, String> usernameColum;
    @FXML private TableColumn<GamePlayer,Integer> localScoreColumn;
    @FXML private TableColumn<GamePlayer, Integer> turnColumn;

    //chat
    @FXML private TextField chatTextInput;
    @FXML private Label chatScreen;

    @FXML
    public void initialize() {

        //ScoreTable
        usernameColum.setCellValueFactory(new PropertyValueFactory<GamePlayer, String>("username"));
        localScoreColumn.setCellValueFactory(new PropertyValueFactory<GamePlayer, Integer>("localScore"));
        turnColumn.setCellValueFactory(new PropertyValueFactory<GamePlayer, Integer>("turnsPlayed"));
        stringBuilder=new StringBuilder();
        turnLabel.setText("Not ready yet");

        chatScreen.setWrapText(true);
    }

    /**
     * Set credentials of the Game controller
     * @param gameExtended Game that is currently being played
     * @param player Player who is playing right now
     * @param token Player toke
     * @param cover Image cover
     */
    void setCredentials(GameExtended gameExtended, Player player, String token, Card cover){

        //assign variables
        this.game=gameExtended;
        this.player=player;
        this.token=token;
        this.cover=cover;
        gamePlayer=new GamePlayer(player);
        clientImpl.setGameController(this);
        if(stringBuilder==null) stringBuilder=new StringBuilder();

        //initializes UI
        makeGrid();
        setLabelsStart();
        addToScoreTable();

        //set Turn variable
        turn=null;

        //initialize chat
        chatScreen.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        stringBuilder.append("You Joined the game! " + "\n");
        chatScreen.setText(stringBuilder.toString());
    }

    /**
     * Method used to make and design the Memory grid.
     */
    private void makeGrid() {

        double totalGridDimensionX=gridGame.getPrefWidth();
        double totalGridDimensionY=gridGame.getPrefHeight();
        cellHeight=totalGridDimensionY/game.getGame().getSize();
        cellWidth=totalGridDimensionX/game.getGame().getSize();

        for (int i=0;i<game.getGame().getSize();i++){
            gridGame.getColumnConstraints().add(new ColumnConstraints(cellWidth));
            gridGame.getRowConstraints().add(new RowConstraints(cellHeight));
        }

        //variables for the card images
        ArrayList<Card> cards = game.getCards();
        images= new ArrayList<>();

        //load cover image
        coverImage= new Image(transformToUrl(cover));

        int rowIndex =0, columnIndex=0;

        buttons =new ArrayList<>();
        int size=game.getGame().getSize();

        for(int i = 0;i<size*size;i++){

            images.add(makeImageView(new Image(transformToUrl(cards.get(i))),i));
            ImageView ivCover = makeImageView(coverImage,-1);

            if(i!=0 && i%game.getGame().getSize()==0)
                rowIndex++;

            columnIndex=i-(rowIndex*game.getGame().getSize());

            Button button = new Button();
            button.setId(String.valueOf(i));
            buttons.add(button);

            button.setPrefHeight(cellHeight-cellHeight/20);
            button.setPrefWidth(cellWidth-cellWidth/20);
            button.setGraphic(ivCover);

            button.setOnAction(e -> {
                performClick(button.getId());
                new Thread(taskFinishTurn).start();
            });

            gridGame.add(button, columnIndex, rowIndex);
        }
    }

    /**
     * Method that transforms a card into a correct adress.
     * @param card Input Cards
     * @return Address of corresponding image.
     */
    private String transformToUrl(Card card) {

        return card.getPath().substring(3);
    }

    /**
     * Make an image view and set correct parameters.
     * @param image Input image that needs to be wrapped in an imageView, type: image.
     * @param id Id that needs to be added to the ImageView, type: Integer.
     * @return ImageView.
     */
    private ImageView makeImageView(Image image, int id){
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(false);
        iv.setFitWidth(cellWidth-cellWidth/10);
        iv.setFitHeight(cellHeight-cellHeight/10);
        iv.setId(String.valueOf(id));
        return iv;
    }

    /**
     * Perform a click on a button.
     * @param idButton ID of the button being clicked, type: String.
     */
    private void performClick(String idButton){

        //save which button is clicked
        int idButtonInt=Integer.parseInt(idButton);

        //check if a player is allowed to perform a turn
        boolean myTurn=checkForTurn();

        //check if there are enough players.
        boolean enoughPlayers=checkForOtherPlayers();
        //check if card isn't allready out of the game.
        boolean clickPossible=checkIfCardIsPossible(idButtonInt);

        if(myTurn && clickPossible && enoughPlayers){

            //perform the turn
            if(turn==null){

                turn=new Turn(player,idButtonInt);
                //change view of card
                changeView(idButtonInt);
                taskFinishTurn =null;

            //Second turn
            }else if(turn.getCard2()==-1 && turn.getCard1()!=idButtonInt){

                disableAllButton();

                turn.setCard2(idButtonInt);

                changeView(turn.getCard2());

                if(turn.checkTurn(game.getCards())) {
                    changeTurnLabel(Color.PURPLE,"\t Correct");
                }

                //perform feedback to the user.

                //PELLE DURFT HIER AANKOMEN EN IK VIL U LEVEND!
                taskFinishTurn = () -> {

                    try {
                        Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
                        AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);
                        appServer.pushTurn(game.getGame().getIdGame(),turn);

                    } catch (RemoteException | NotBoundException e) {
                        e.printStackTrace();
                    }

                    if(!turn.isCorrect()) {

                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //wrong guess, flip the cards again.
                        changeView(turn.getCard1());
                        changeView(turn.getCard2());
                    }
                    else{
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    game.nextPlayer();
                    setLabels(false);

                    game.updateGamePlayer(turn);

                    System.out.println(game.checkComplete());

                    updateScoreTable();
                    turn=null;

                    checkView();

                    if(game.checkComplete()){
                        completeGame();
                    }
                };
            }
        }
    }

    private void completeGame(){

        //Push all score's to database

        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);
            appServer.endGame(game.getGame().getIdGame(), player.getId(), player.getUsername());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        //Alert
        Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("End of the Game");
                    alert.setHeaderText(null);
                    alert.setContentText("Congratulations, you win the game !");

                    alert.showAndWait();
                });

        try {
            viewController.setViewToLobby(player.getUsername(), player.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------- checks when button is clicked --------//

    private boolean checkIfCardIsPossible(int idButton) {
        return !game.getCorrectCards().get(idButton);
    }

    private boolean checkForTurn(){
        return player.getId() == game.getCurrentPlayerTurn().getId();
    }

    private boolean checkForOtherPlayers() {
        return game.getGame().getCurNumberOfPlayers() == game.getGame().getMaxNumberOfPlayers();
    }

    private void disableAllButton(){

        for(Node button: gridGame.getChildren()){
            button.setDisable(true);
        }
    }

    private void enableAllButton(){

        for(Node button: gridGame.getChildren()){
            button.setDisable(false);
        }
    }

    //-------- flip card methods --------//

    public synchronized void changeView(int idButton) {
        Button button= buttons.get(idButton);
        ImageView newIV= swapIV(button, idButton);

        Platform.runLater(() ->button.setGraphic(newIV));
    }

    private synchronized ImageView swapIV(Button button, int id){

        if(button.getGraphic().getId().equals("-1"))
            return images.get(id);
        else
            return makeImageView(coverImage,-1);
    }

    private ImageView choseRightIV(int buttonID){

        if(game.getCorrectCards().get(buttonID))
            return images.get(buttonID);
        else
            return makeImageView(coverImage,-1);

    }

    //-------- change label methods --------//

    /**
     * Method to set the label on the bottom of the screen, indicating which player is currently playing.
     * @param watch Is true if the client is a watcher
     */
    public void setLabels(boolean watch){

        StringBuilder stringBuilder=new StringBuilder();
        Color color;

        if(checkForTurn()){
            stringBuilder.append(player.getUsername());
            stringBuilder.append("\t : Your Turn!");
            color=Color.GREEN;

            if(!watch)
                enableAllButton();

        }else{
            stringBuilder.append(game.getCurrentPlayerTurn().getUsername());
            stringBuilder.append("\t Not Your Turn!");
            color=Color.RED;

            if(!watch)
                disableAllButton();
        }

        changeTurnLabel(color,stringBuilder.toString());
    }

    /**
     * Method to set the label at the start of a game.
     */
    private void setLabelsStart(){

        Color color=Color.BLUE;

        String stringBuilder = "Waiting for other player: " + "\t" +
                "Players: " + game.getGame().getCurNumberOfPlayers() +
                "/" + game.getGame().getMaxNumberOfPlayers();

        changeTurnLabel(color, stringBuilder);
    }

    private void changeTurnLabel(Color color, String text) {
        Background background=new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
        Platform.runLater(() -> {
            turnLabel.setBackground(background);
            turnLabel.setText(text);
            turnLabel.setTextFill(Color.WHITE);
        });
    }

    private void setLabelDuringMove(){
        changeTurnLabel(Color.BLUE,"Performing turn of player: " + game.getCurrentPlayerTurn().getUsername());
    }

    void checkBeginGame(){
        if(game.getGame().getMaxNumberOfPlayers() == game.getGame().getCurNumberOfPlayers()) {
            try {
                Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
                AppServerInterface appServerImpl = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);

                appServerImpl.beginGame(game.getGame().getIdGame());
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //-------- callback methods --------//

    /**
     * Perform a turn by another player.
     * @param turn turn to process.
     */
    public void performOtherTurn(Turn turn, boolean watch){

        checkView();

        game.addTurn(turn);
        game.setCurrentPlayerTurn(new GamePlayer(turn.getPlayer()));
        setLabelDuringMove();

        changeView(turn.getCard1());
        changeView(turn.getCard2());

        game.updateGamePlayer(turn);
        updateScoreTable();

        //if move done is not correct, flip cards again.
        if(!turn.checkTurn(game.getCards())){

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            changeView(turn.getCard1());
            changeView(turn.getCard2());
        }

        game.nextPlayer();
        setLabels(watch);

        checkView();

    }

    /**Indicater for a user when it is his turn.
     *
     */
    public void yourTurn(){
        game.setCurrentPlayerTurn(gamePlayer);
    }

    /**
     * Add a newly joined player to the score table.
     */
    private void addToScoreTable() {
        Platform.runLater(() ->{
            scoreTableGame.getItems().clear();
            scoreTableGame.getItems().addAll(game.getPlayers());
            scoreTableGame.getSortOrder().add(localScoreColumn);
        });
    }

    /**
     * Update the score table after possible changes
     */
    public void updateScoreTable(){
        Platform.runLater(() -> {
            scoreTableGame.refresh();
            scoreTableGame.getSortOrder().add(localScoreColumn);
        });
    }

    /**
     * Update the chat view when a user did sent a message.
     * @param username Username of the player who did sent a message, type String.
     * @param message Incoming message, type String.
     */
    public void updateChat(String username, String message) {
        stringBuilder.append(player.getUsername()).append(": ").append(message).append("\n");
        Platform.runLater(() -> chatScreen.setText(stringBuilder.toString()));
    }

    /**
     * Add a player to the game in all correct ways.
     * @param player Player in need to be added.
     * @param index Index of the client callback object corresponding to the player.
     */
    public void addPlayer(Player player, int index){

        game.addPlayer(new GamePlayer(player),index);
        addToScoreTable();
        stringBuilder.append(player.getUsername()).append(" joined the game. \n");
        setLabelsStart();
        Platform.runLater(() ->chatScreen.setText(stringBuilder.toString()));
        }

    /**
     * Set view of the buttons correct. Check every button again
     */
    private void checkView(){

        for (int i=0;i<buttons.size();i++){

            Button button = buttons.get(i);
            ImageView tempImageView = choseRightIV(i);
            Platform.runLater(() -> button.setGraphic(tempImageView));
        }
    }

    public void sendChat(){
        String message=chatTextInput.getText();
        chatTextInput.clear();
        stringBuilder.append("me: ").append(message).append("\n");

        // send chat message to app server
        try {
            Registry registry = LocateRegistry.getRegistry(appServer.getIP(), appServer.getPort());
            AppServerInterface appServer = (AppServerInterface) registry.lookup(APPSERVER_SERVICE);
            appServer.serverToClientMessage(gamePlayer.getUsername(),message, myIndexNumberServerOne, game.getGame().getIdGame());

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        Platform.runLater(() ->chatScreen.setText(stringBuilder.toString()));
        if(!message.equals("")&&!message.equals(" ")){


        }

    }

    public void alertEndGam(String winner) {

        //Alert
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("End of the Game");
            alert.setHeaderText(null);
            alert.setContentText(winner + "haze won the game !");

            alert.showAndWait();
        });
        try {
            viewController.setViewToLobby(player.getUsername(), player.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
