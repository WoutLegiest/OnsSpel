package threads;

import controller.LobbyController;
import javafx.application.Platform;

public class ScoreBoardThread extends Thread{

    private final LobbyController lc;

    public ScoreBoardThread(LobbyController lc) {
        this.lc = lc;
    }

    public synchronized void run(){
        super.run();

        Platform.setImplicitExit(false);

        try {
            while(true){

                wait(5000);

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        lc.refreshT1();
                    }
                });

                //lc.refreshT2();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
