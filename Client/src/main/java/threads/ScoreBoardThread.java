package threads;

import controller.LobbyController;

public class ScoreBoardThread extends Thread{

    public ScoreBoardThread() {
    }

    public synchronized void run(){

        LobbyController lc = new LobbyController();

        try {
            while(true){

                wait(5000);
                lc.refreshT1();
                lc.refreshT2();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
