package client.threads;

import client.controller.LobbyController;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread to update the tableviews of the lobby every 5 seconds
 */
public class ScoreBoardThread extends Thread{

    private final LobbyController lc;
    private AtomicBoolean running = new AtomicBoolean(false);

    public ScoreBoardThread(LobbyController lc) {
        this.lc = lc;
    }

    public void stopThread() {
        running.set(false);
    }

    public synchronized void run(){

        running.set(true);

        try {
            while(running.get()){

                wait(5000);

                //Gewone threads classen met JavaFx
                Platform.runLater(() -> {
                        //System.out.println("Uitgevoerd");
                        lc.refreshT1();
                        lc.refreshT2();
                    });

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
