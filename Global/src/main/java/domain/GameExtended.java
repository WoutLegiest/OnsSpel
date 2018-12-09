package domain;

import java.io.Serializable;
import java.util.ArrayList;

public class GameExtended implements Serializable {

    private static final long serialVersionUID = -7010078965463558136L;

    private final Game game;
    private final String theme;
    private final ArrayList<Card>cards;
    private final ArrayList<GamePlayer>players;
    private ArrayList<Integer>clientIndexes;
    private ArrayList<Boolean>correctCards;
    private GamePlayer currentPlayerTurn;
    private ArrayList<Turn> turns;

    public GameExtended(Game game) {
        this.game = game;
        this.theme = null;
        this.cards=null;
        this.players=null;
        this.currentPlayerTurn=null;
        clientIndexes=new ArrayList<>();
        correctCards= new ArrayList<>();
        turns=new ArrayList<>();
    }

    public GameExtended(Game game, String theme, ArrayList<Card> cards, ArrayList<GamePlayer> players, GamePlayer currentPlayerTurn) {
        this.game = game;
        this.theme = theme;
        this.cards = cards;
        this.players = players;
        this.currentPlayerTurn=currentPlayerTurn;
        clientIndexes=new ArrayList<>();
        correctCards= new ArrayList<>();
        for(Card card: cards){
            correctCards.add(false);
        }
        turns=new ArrayList<>();
    }

    public GameExtended(GameExtended gameExtended) {
        this.game = gameExtended.game;
        this.theme = gameExtended.theme;
        this.cards = gameExtended.cards;
        this.players = gameExtended.players;
        this.currentPlayerTurn = gameExtended.currentPlayerTurn;
        this.clientIndexes = gameExtended.clientIndexes;
        this.correctCards = gameExtended.correctCards;
        this.turns = gameExtended.turns;
    }

    /**
     * Method that adds a player to a game in the correct way.
     * @param player Player in need to be added.
     * @param indexClient Index of the corresponding client.
     */
    public void addPlayer(GamePlayer player, int indexClient) {
        players.add(player);
        game.increaseNumberOfPlayers();
        addClientIndex(indexClient);
    }

    public boolean addTurn(Turn turn){

        turns.add(turn);

        if(turn.checkTurn(cards)){
            correctCards.set(turn.getCard1(),true);
            correctCards.set(turn.getCard2(),true);
            return true;
        }
        return false;
    }

    public void addClientIndex(int clientIndex){
        clientIndexes.add(clientIndex);
    }

    public void nextPlayer(){

        int nextPlayerIndex=-1;

        for(int i=0;i<players.size();i++){

            if(currentPlayerTurn.getId()==players.get(i).getId())
                nextPlayerIndex=i;
        }

        if(nextPlayerIndex==players.size()-1)
            nextPlayerIndex=0;
        else
            nextPlayerIndex++;

        currentPlayerTurn=players.get(nextPlayerIndex);
    }

    public void updateGamePlayer(Turn turn) {

        for (GamePlayer player: players){

            if(player.getId()==turn.getPlayer().getId()){
                player.increaseTurns();

                if(turn.checkTurn(cards)){
                    player.increaseScore();
                }
            }
        }

        if (turn.isCorrect()){
            correctCards.set(turn.getCard1(),true);
            correctCards.set(turn.getCard2(),true);
        }
    }

    //-----getters and setters-----//

    public Game getGame() {
        return game;
    }

    public String getTheme() {
        return theme;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<GamePlayer> getPlayers() {
        return players;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public Player getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(GamePlayer currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public ArrayList<Integer> getClientIndexes() {
        return clientIndexes;
    }

    public void setClientIndexes(ArrayList<Integer> clientIndexes) {
        this.clientIndexes = clientIndexes;
    }

    public ArrayList<Boolean> getCorrectCards() {
        return correctCards;
    }

    public void setCorrectCards(ArrayList<Boolean> correctCards) {
        this.correctCards = correctCards;
    }


}
