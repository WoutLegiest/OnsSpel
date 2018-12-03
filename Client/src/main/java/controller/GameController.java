package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import domain.Turn;
import interfaces.AppServerInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static controller.MainClient.appServerServiceName;
import static controller.MainClient.clientImpl;
import static domain.Constants.APPSERVER_PORT;
import static domain.Constants.IP;

public class GameController {

    //Game
    private GameExtended game;
    private Player player;
    private Player playerTurn;
    private String token;
    private Card cover;
    private double cellHeight;
    private double cellWidth;
    private ArrayList<Button> buttons;
    private ArrayList<ImageView> images;
    private ArrayList<Card> cards;
    private Image coverImage;

    private Turn turn;

    //Game Grid
    @FXML
    private GridPane gridGame;

    @FXML
    private Label turnLabel;

    @FXML
    public void initialize() {


    }

    public void setCredentials(GameExtended gameExtended, Player player, String token, Card cover){
        this.game=gameExtended;
        this.player=player;
        this.token=token;
        this.cover=cover;
        clientImpl.setGameController(this);
        makeGrid();
        setLabels();
        turn=null;

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
                if(turn.isCorrect()){
                    title= "Correct move!";
                    contentText= "You have gained a point, congrats mate";
                }else {
                    title="False move";
                    contentText="Try again next time!";
                    //wrong guess, flip the cards again.
                    changeView(turn.getCard1());
                    changeView(turn.getCard2());
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

                game.nextPlayer();
                setLabels();


            }
        }
    }

    private boolean checkIfCardIsPossible(int idButton) {
        return !game.getCorrectCards().get(idButton);
    }

    private void changeView(int idButton) {
        Button button= buttons.get(idButton);
        ImageView newIV=choseNewIV(button, idButton);
        button.setGraphic(newIV);
    }

    private boolean checkForTurn(){
        if(player.getId()==game.getCurrentPlayerTurn().getId())return true;
        else return false;
    }

    private ImageView choseNewIV(Button button, int id){
        if(button.getGraphic().getId().equals("-1")) return images.get(id);
        else return makeImageView(coverImage,-1);
    }

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

    /**
     * Perform a turn by another player.
     * @param turn turn to process.
     */
    public void performOtherTurn(Turn turn){
        game.addTurn(turn);
        game.setCurrentPlayerTurn(turn.getPlayer());
        setLabelDuringMove();

        changeView(turn.getCard1());
        changeView(turn.getCard2());

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

    }

    public void yourTurn(){

    }


    public void updateScoreTable() {
    }

    public void updateChat(String message) {
    }
}
