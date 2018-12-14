package AppServer;

import global.exceptions.UserExistsException;
import global.domain.*;
import global.interfaces.AppServerInterface;
import global.interfaces.ClientInterface;
import global.interfaces.DispatcherInterface;
import global.servers.AppServer;
import global.servers.DataBaseServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static global.domain.Constants.*;

@SuppressWarnings("Duplicates")
public class AppServerImpl extends UnicastRemoteObject implements AppServerInterface {

    //None SessionUID !

    private DataBaseServer dataBase;
    private ArrayList<GameExtended> games;
    private ArrayList<ClientInterface> clientList;
    private ArrayList<AppServerInterface> appServerList;

    public AppServerImpl() throws RemoteException {

        games=new ArrayList<>();
        clientList=new ArrayList<>();
        appServerList = new ArrayList<>();

        try{
            Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);
            dataBase = dispatcherImp.getDataBaseServer();

        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds the other AppServer interfaces to this AppServer. So we can send messages to these other server
     * @param appInterface the to add interface
     * @throws RemoteException
     */
    @Override
    public void addOtherAppServer(AppServerInterface appInterface) throws RemoteException{
        appServerList.add(appInterface);
    }

    /**
     * Couple a database to this interface
     * @param dataBase The database objet
     * @throws RemoteException
     */
    @Override
    public void setDataBase(DataBaseServer dataBase) throws RemoteException {
        this.dataBase = dataBase;
    }

    /**
     * Add a clientInterface
     * @param callbackClientObject The to add callbackInterface
     * @return returns the index of the object inside the clienList attribute
     * @throws RemoteException
     */
    @Override
    public int registerForCallback(ClientInterface callbackClientObject) throws RemoteException {
        if (!(clientList.contains(callbackClientObject))) {
            clientList.add(callbackClientObject);

            System.out.println("Registered new client ");
            return clientList.indexOf(callbackClientObject);
        }
        return -1;
    }

    /**
     * Add a watcher
     * @param gameID Game the watcher wanted to watch
     * @param clientIndex The index of the clientInterface inside clientList
     * @throws RemoteException
     */
    @Override
    public void registerWatcher(int gameID, int clientIndex) throws RemoteException{

        for (GameExtended gameExtended: games) {
            if (gameExtended.getGame().getIdGame() == gameID) {
                gameExtended.addWatcher(clientIndex);
            }
        }
    }

    /**
     * Call the database to authenticate a player
     * @param username The username
     * @param password the password, non encryted
     * @return Passtrough of the string came from the database
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public String authenticatePlayer(String username, String password) throws RemoteException, SQLException {
        return dataBase.getDataBaseImpl().authenticatePlayer(username, password);
    }

    /**
     * Register the user at the database
     * @param username
     * @param password
     * @param email
     * @return
     * @throws RemoteException
     * @throws UserExistsException
     * @throws SQLException
     */
    @Override
    public String registerPlayer(String username, String password, String email) throws RemoteException, UserExistsException, SQLException {
        return dataBase.getDataBaseImpl().registerPlayer(username, password,email);
    }

    @Override
    public Player getPlayer(String username) throws RemoteException {
        return dataBase.getDataBaseImpl().getPlayer(username);
    }

    @Override
    public ArrayList<Player> getAllPlayers() throws RemoteException {
        return dataBase.getDataBaseImpl().getAllPlayers();
    }

    @Override
    public ArrayList<Game> getAllGamesFromDB() throws RemoteException {
        return dataBase.getDataBaseImpl().getAllGames();
    }

    @Override
    public ArrayList<Game> getAllGamesFromAppServer() throws RemoteException {

        ArrayList<Game> temp = new ArrayList<>();

        for (GameExtended ge: games){

            temp.add(ge.getGame());
        }

        return temp;
    }

    /**
     * Shuffles all the cards
     * @param range
     * @param theme
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<Card> shuffleCards(int range, String theme) throws RemoteException {

        ArrayList<Card> cardsByTheme= getCardsByTheme(theme);
        int numberOfCards=range*range/2;
        ArrayList<Card> gameCards=new ArrayList<>();
        Random rand = new Random();

        for (int i=0;i<numberOfCards;i++){
            int index= rand.nextInt(Math.abs(cardsByTheme.size()));
            Card tempCard=cardsByTheme.remove(index);
            gameCards.add(tempCard);
            gameCards.add(tempCard);
        }
        Collections.shuffle(gameCards);

        return gameCards;
    }

    @Override
    public ArrayList<Card> getCardsByTheme(String theme)throws RemoteException{
        return dataBase.getDataBaseImpl().getCardsByTheme(theme);
    }

    /**
     * Push a game to the database
     * @param owner the id of the owner
     * @param maxNumberOfPlayer
     * @param size the size of the game
     * @return The GameId that is created in the database
     * @throws RemoteException
     */
    @Override
    public int gameCreated( int owner, int maxNumberOfPlayer, int size) throws RemoteException {
        return dataBase.getDataBaseImpl().registerGame(owner, maxNumberOfPlayer, size);
    }

    /**
     * Add gameExtended object that will be stored on the AppServerImpl. The ownername will the asked to the database
     * and will be added to the game object
     * @param gameExtended The to add game extends object
     * @throws RemoteException
     */
    @Override
    public void gameCreatedExtended(GameExtended gameExtended) throws RemoteException {
        String username = dataBase.getDataBaseImpl().getUsername(gameExtended.getGame().getOwner());
        gameExtended.getGame().setOwnerUsername(username);
        games.add(gameExtended);
        dataBase.getDataBaseImpl().saveGameExtended(gameExtended);

    }

    /**
     * Method that handels a turn by pushing the turn to other clients and updates his own version of the game
     * @param gameId The to update game
     * @param turn The to add turn
     * @throws RemoteException
     */
    @Override
    public void pushTurn(int gameId, Turn turn) throws RemoteException {

        for (GameExtended gameExtended: games){
            if(gameExtended.getGame().getIdGame()==gameId){

                //Turn wordt toegevoegd aan het game object op de appServer
                gameExtended.addTurn(turn);

                //Turn doorvoeren naar alle andere meevolgende games
                performTurn(gameExtended,turn);

                //Int van de volgende speler klaarzetten
                gameExtended.nextPlayer();

                //Scoreboard updaten door gameplayer up te daten
                gameExtended.updateGamePlayer(turn);

                //Exit loop
                return;
            }
        }
    }

    /**
     * Add a player to al the other (waiting) games
     * @param gp The player object
     * @param ownIndex The client index
     * @param gameID The id of the game
     * @throws RemoteException
     */
    @Override
    public void addPlayer(Player gp, int ownIndex, int gameID) throws RemoteException{

        //Doorpushen naar de DB
        dataBase.getDataBaseImpl().addPlayer(gameID, ownIndex);

        //Lokaal toevoegen
        GamePlayer temp = new GamePlayer(gp);

        for(GameExtended ge: games){
            if(ge.getGame().getIdGame() == gameID){
                ge.addPlayer(temp, ownIndex);
            }
        }

        //Toevoegen aan al de andere servers
        for (Integer index :findGameExtended(gameID).getClientIndexes()){
            if(index!=ownIndex){
                clientList.get(index).addPlayer(gp,ownIndex);
            }
        }
    }

    @Override
    public void serverToClientMessage(String username, String message, int clientIndex, int gameId) throws RemoteException {

        //Dit steund op de lokale kopieren van de gameExtended !

        for (Integer index :findGameExtended(gameId).getClientIndexes()){
            if(index!=clientIndex)
                clientList.get(index).receiveMessage(username,message);
        }

    }

    /**
     * Retrive a AppServer object where a game i located
     * @param gameID the id of the game
     * @return A appserver object with al the data about the appserver
     * @throws RemoteException
     */
    @Override
    public AppServer findServer(int gameID) throws RemoteException {

        //Moet een AppServer gaan teruggeven, door eerst de server te gaan zoeken waar de game zich bevindt.

        AppServer temp = null;


        for(AppServerInterface asi: appServerList){
            AppServerInterface receive = asi.findGame(gameID);

            if(receive != null) {
                try {
                    Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
                    DispatcherInterface dispatcherImpl = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

                    temp = dispatcherImpl.getAppServer(receive);

                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }

        //Als de appServer niet te vinden is, dan is hij zichzelf
        if(temp == null){
            try {
                Registry registry = LocateRegistry.getRegistry(IP, DISPATCH_PORT);
                DispatcherInterface dispatcherImpl = (DispatcherInterface) registry.lookup(DISPATCH_SERVICE);

                temp = dispatcherImpl.getAppServer(this);

            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }

        return temp;
    }

    /**
     * Find the interface where the game is placed
     * @param gameID
     * @return the interface where the game is
     * @throws RemoteException
     */
    @Override
    public AppServerInterface findGame(int gameID) throws RemoteException{

        //System.out.println("Starten van findGame");

        for(GameExtended gameExtended: games){
            if(gameExtended.getGame().getIdGame()==gameID){
                System.out.print("Find interface");
                return this;
            }
        }
        return null;
    }

    /**
     * Send the turn to al the other client. Doing in parell using threads
     * @param gameExtended
     * @param turn
     */
    private void performTurn(GameExtended gameExtended, Turn turn){

        for(int i=0;i<gameExtended.getPlayers().size();i++){

            //Naar iedereen doorsturen behalve jezelf
            if(gameExtended.getPlayers().get(i).getId()!=turn.getPlayer().getId()){

                int finalI = i;
                Runnable r = () -> {
                    try {
                        clientList.get(gameExtended.getClientIndexes().get(finalI)).performOtherPlayerTurn(turn, false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                };

                new Thread(r).start();
            }
        }

        for(int i=0;i<gameExtended.getWatchIndexes().size();i++){

            int finalI = i;
            Runnable r = () -> {
                try {
                    clientList.get(gameExtended.getWatchIndexes().get(finalI)).performOtherPlayerTurn(turn, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            };

            new Thread(r).start();
        }
    }

    /**
     * Find the gameExtended object on the interface
     * @param gameId
     * @return returns
     * @throws RemoteException
     */
    @Override
    public GameExtended findGameExtended(int gameId) throws  RemoteException{

        for(GameExtended game:games){
            if (game.getGame().getIdGame()==gameId)
                return game;
        }
        return null;
    }

    /**
     * Starts the game that needs to be started
     * @param idGame
     * @throws RemoteException
     */
    @Override
    public void beginGame(int idGame) throws RemoteException {

        for(GameExtended ge: games){
            if(ge.getGame().getIdGame() == idGame){
                for (Integer i: ge.getClientIndexes())
                    clientList.get(i).startGame();

                return;
            }
        }

    }

    @Override
    public void endGame(int idGame) throws RemoteException{

        for(GameExtended ge: games){
            if(ge.getGame().getIdGame() == idGame){

                for(GamePlayer gp: ge.getPlayers()){
                    dataBase.getDataBaseImpl().updatePlayerScore(gp.getLocalScore(), gp.getId());
                }

                return;
            }
        }

    }


}
