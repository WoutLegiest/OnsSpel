package controller;

import domain.*;
import interfaces.AppServerInterface;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static controller.MainClient.*;
import static domain.Constants.APPSERVER_PORT;
import static domain.Constants.IP;

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

    private ArrayList<Button> buttons;
    private ArrayList<ImageView> images;
    private ArrayList<Card> cards;

    //Turn
    private Turn turn;

    //Chat
    private StringBuilder stringBuilder;

    //Game Grid
    @FXML private GridPane gridGame;

    @FXML private Label turnLabel;

    //score table
    @FXML private TableView<GamePlayer> scoreTableGame;

    @FXML private TableColumn<GamePlayer, String> usernameColum;
    @FXML private TableColumn<GamePlayer,Integer> localScoreColumn;
    @FXML private TableColumn<GamePlayer, Integer> turnColumn;

    //chat
    @FXML private Button chatSendButton;
    @FXML private TextField chatTextInput;
    @FXML private TextArea chatScreen;




    @FXML
    public void initialize() {

        //ScoreTable
        usernameColum.setCellValueFactory(new PropertyValueFactory<GamePlayer, String>("username"));
        localScoreColumn.setCellValueFactory(new PropertyValueFactory<GamePlayer, Integer>("localScore"));
        turnColumn.setCellValueFactory(new PropertyValueFactory<GamePlayer, Integer>("turnsPlayed"));
        stringBuilder=new StringBuilder();

    }

    public void setCredentials(GameExtended gameExtended, Player player, String token, Card cover){
        this.game=gameExtended;
        this.player=player;
        this.token=token;
        this.cover=cover;
        gamePlayer=new GamePlayer(player);
        clientImpl.setGameController(this);
        makeGrid();
        setLabels();
        turn=null;

        if(stringBuilder==null) stringBuilder=new StringBuilder();

    }

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
        cards=game.getCards();
        images= new ArrayList<>();

        //load cover image
        coverImage= new Image(transformToUrl(cover));

        int rowIndex =0, columnIndex=0;

        buttons =new ArrayList<>();
        int size=game.getGame().getSize();
        for(int i = 0;i<size*size;i++){

            images.add(makeImageView(new Image(transformToUrl(cards.get(i))),i));
            ImageView ivCover = makeImageView(coverImage,-1);

            if(i!=0 && i%game.getGame().getSize()==0){
                rowIndex++;
            }
            columnIndex=i-(rowIndex*game.getGame().getSize());
            Button button = new Button();
            button.setId(String.valueOf(i));
            buttons.add(button);
            button.setPrefHeight(cellHeight-cellHeight/20);
            button.setPrefWidth(cellWidth-cellWidth/20);
            button.setGraphic(ivCover);

            button.setOnAction(e -> performClick(button.getId()));
            gridGame.add(button, columnIndex, rowIndex);
        }
    }

    private String transformToUrl(Card card) {

        return card.getPath().substring(3);
    }

    private ImageView makeImageView(Image image, int id){
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(false);
        iv.setFitWidth(cellWidth-cellWidth/10);
        iv.setFitHeight(cellHeight-cellHeight/10);
        iv.setId(String.valueOf(id));
        return iv;
    }

    private void performClick(String idButton){
        //check if a player is allowed to perform a turn.
        boolean myTurn=checkForTurn();
        //save which button is clicked
        int idButtonInt=Integer.parseInt(idButton);
        //check if card isn't allready out of the game.
        boolean clickPossible=checkIfCardIsPossible(idButtonInt);

        if(myTurn && clickPossible){

            //perform the turn
            if(turn==null){

                turn=new Turn(player,idButtonInt);
                //change view of card
                changeView(idButtonInt);

            }else if(turn.getCard2()==-1){

                turn.setCard2(idButtonInt);
                changeView(idButtonInt);
                String title=null;
                String contentText=null;

                //perform feedback to the user.
                boolean correct=turn.isCorrect();
                if(correct){
                    title= "Correct move!";
                    contentText= "You have gained a point, congrats mate";
                }else {
                    title="False move";
                    contentText="Try again next time!";

                }

                // send turn to app server
                Registry registry = null;
                try {
                    registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
                    AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);
                    appServer.pushTurn(game.getGame().getIdGame(),turn);

                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }


                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle( title);
                alert.setHeaderText(null);
                alert.setContentText(contentText);
                alert.showAndWait();

                if(!correct) {
                    //wrong guess, flip the cards again.
                    changeView(turn.getCard1());
                    changeView(turn.getCard2());
                }

                game.nextPlayer();
                setLabels();

                game.updateGamePlayer(turn);
                updateScoreTable();
                turn=null;


            }
            checkView();
        }
    }

    //-------- checks when button is clicked --------//

    private boolean checkIfCardIsPossible(int idButton) {
        return !game.getCorrectCards().get(idButton);
    }

    private boolean checkForTurn(){
        if(player.getId()==game.getCurrentPlayerTurn().getId())return true;
        else return false;
    }

    //-------- flip card methods --------//

    private void changeView(int idButton) {
        Button button= buttons.get(idButton);
        ImageView newIV= swapIV(button, idButton);
        button.setGraphic(newIV);
    }

    private ImageView swapIV(Button button, int id){
        if(button.getGraphic().getId().equals("-1")) return images.get(id);
        else return makeImageView(coverImage,-1);
    }

    private ImageView choseRightIV(int buttonID){
        ImageView IV=null;
        if(game.getCorrectCards().get(buttonID))return images.get(buttonID);
        else return makeImageView(coverImage,-1);

    }

    //-------- change label methods --------//

    /**
     * Method to set the label on the bottom of the screen, indicating which player is currently playing.
     */
    private void setLabels(){
        StringBuilder stringBuilder=new StringBuilder();
        Color color;
        if(checkForTurn()){
            stringBuilder.append(player.getUsername());
            stringBuilder.append(": Your Turn!");
            color=Color.GREEN;

        }else{
            stringBuilder.append(game.getCurrentPlayerTurn().getUsername());
            stringBuilder.append("Not Your Turn!");
            color=Color.RED;
        }

        changeTurnLabel(color,stringBuilder.toString());

    }

    private void changeTurnLabel(Color color, String text) {
        Background background=new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
        turnLabel.setBackground(background);
        turnLabel.setText(text);
        turnLabel.setTextFill(Color.WHITE);
    }

    private void setLabelDuringMove(){
        changeTurnLabel(Color.BLUE,"Performing turn of player: " + game.getCurrentPlayerTurn().getUsername());
    }

    //-------- callback methods --------//

    /**
     * Perform a turn by another player.
     * @param turn turn to process.
     */
    public void performOtherTurn(Turn turn){
        checkView();

        game.addTurn(turn);
        game.setCurrentPlayerTurn(new GamePlayer(turn.getPlayer()));
        setLabelDuringMove();

        changeView(turn.getCard1());
        changeView(turn.getCard2());

        game.updateGamePlayer(turn);
        updateScoreTable();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if move done is not correct, flip cards again.
        if(!turn.checkTurn(game.getCards())){
            changeView(turn.getCard1());
            changeView(turn.getCard2());
        }

        game.nextPlayer();
        setLabels();

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
    public void addToScoreTable() {

        scoreTableGame.getItems().addAll(game.getPlayers());
        scoreTableGame.getSortOrder().add(localScoreColumn);

    }

    /**
     * Update the score table after possible changes
     */
    public void updateScoreTable(){
        scoreTableGame.refresh();
        scoreTableGame.getSortOrder().add(localScoreColumn);
    }

    /**
     * Update the chat view when a user did sent a message.
     * @param username Username of the player who did sent a message, type String.
     * @param message Incoming message, type String.
     */
    public void updateChat(String username, String message) {
        stringBuilder.append(player.getUsername()+ ": " + message + "\n");
        chatScreen.setText(stringBuilder.toString());

    }

    public void addPlayer(Player player, int index){
        game.addPlayer(new GamePlayer(player),index);
        addToScoreTable();
        stringBuilder.append(player.getUsername() + " joined the game. \n");
        chatScreen.setText(stringBuilder.toString());

    }

    private void checkView(){
        for (int i=0;i<buttons.size();i++){
            Button button= buttons.get(i);
            ImageView tempImageView= choseRightIV(i);
            button.setGraphic(tempImageView);
        }
    }

    public void sendChat(){
        String message=chatTextInput.getText();
        if(!message.equals("")&&!message.equals(" ")){
            stringBuilder.append("me: " + message + "\n");

            // send chat message to app server
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry(IP, APPSERVER_PORT);
                AppServerInterface appServer = (AppServerInterface) registry.lookup(appServerServiceName);
                appServer.serverToClientMessage(gamePlayer.getUsername(),message, myIndexNumberServerOne, game.getGame().getIdGame());

            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }

        }

    }
}
