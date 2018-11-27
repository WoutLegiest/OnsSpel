package threads;

import controller.LobbyController;
import javafx.application.Platform;

public class ScoreBoardThread extends Thread{

    private final LobbyController lc;
    private boolean refresh = false;

    public ScoreBoardThread(LobbyController lc) {
        this.lc = lc;
    }

    public void stopThread(){
        refresh=false;
    }

    public synchronized void run(){

        refresh = true;

        try {
            while(refresh){

                wait(5000);

                //
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        System.out.println("Uitgevoerd");
                        lc.refreshT1();
                        //lc.refreshT2();
                    }
                });



            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
