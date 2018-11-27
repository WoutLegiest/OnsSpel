package controller;

import domain.Card;
import domain.GameExtended;
import domain.Player;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ArrayList;

public class GameController {

    //Game
    private GameExtended game;
    private Player player;
    private String token;

    //Game Grid
    @FXML
    private GridPane gridGame;


    @FXML
    public void initialize() {


    }

    public void setCredentials(GameExtended gameExtended, Player player, String token){
        this.game=gameExtended;
        this.player=player;
        this.token=token;

        makeGrid();

    }

    private void makeGrid() {

        double totalGridDimensionX=gridGame.getPrefWidth();
        double totalGridDimensionY=gridGame.getPrefHeight();
        double cellHeight=totalGridDimensionY/game.getGame().getSize();
        double cellWidth=totalGridDimensionX/game.getGame().getSize();

        for (int i=0;i<game.getGame().getSize();i++){
            gridGame.getColumnConstraints().add(new ColumnConstraints(cellWidth));
            gridGame.getRowConstraints().add(new RowConstraints(cellHeight));
        }

        Image image = new Image("/images/upmLogo.png");

        int rowIndex =0, columnIndex=0;
        ArrayList<Card> cards=game.getCards();
        for(int i = 0;i<game.getGame().getSize()*2;i++){
            if(i!=0 && i%game.getGame().getSize()==0){
                rowIndex++;
            }
            columnIndex=i-(rowIndex*game.getGame().getSize());
            ImageView iv = new ImageView();
            iv.setImage(image);
            iv.setPreserveRatio(false);
            iv.setFitWidth(cellWidth);
            iv.setFitHeight(cellHeight);

            GridPane.setConstraints(iv, columnIndex, rowIndex);
        }
    }
}
