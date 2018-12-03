package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

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
        makeGrid();
        setLabels();

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
        boolean clickPossible=checkForTurn();
        if(clickPossible){
            int idButtonInt=Integer.parseInt(idButton);
            Button button= buttons.get(idButtonInt);
            ImageView newIV=choseNewIV(button, idButtonInt);

            button.setGraphic(newIV);
        }
    }

    private boolean checkForTurn(){
        //TODO: logic met database connecteren om te checken wie zijn beurt het is.
        return true;
    }

    private ImageView choseNewIV(Button button, int id){
        if(button.getGraphic().getId().equals("-1")) return images.get(id);
        else return makeImageView(coverImage,-1);
    }

    private void setLabels(){
        StringBuilder stringBuilder=new StringBuilder();
        Color color;
        //TODO: verander van enkel in het begin werkende naar check op turn.
        if(player.getId()==game.getPlayers().get(0).getId()){
            stringBuilder.append(player.getUsername());
            stringBuilder.append(": Your Turn!");
            color=Color.GREEN;

        }else{
            stringBuilder.append("Not Your Turn!");
            color=Color.RED;
        }
        Background background=new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
        turnLabel.setBackground(background);
        turnLabel.setText(stringBuilder.toString());
        turnLabel.setTextFill(Color.WHITE);

    }

    //TODO: add score functionaliteit



}
