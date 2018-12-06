package servers;

import interfaces.AppServerInterface;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Wouter Legiest on 5/12/2018
 */
public class AppServer  implements Serializable {

    private static final long serialVersionUID = -3325140243868020180L;

    private final int port;
    private final String IP;
    private AppServerInterface appServerImpl;
    private int nGames;

    public AppServer(int port, String IP, AppServerInterface appServerImpl) {
        this.port = port;
        this.IP = IP;
        this.appServerImpl = appServerImpl;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    public AppServerInterface getAppServerImpl() {
        return appServerImpl;
    }

    public void setAppServerImpl(AppServerInterface appServerImpl) {
        this.appServerImpl = appServerImpl;
    }

    public int getnGames() {
        return nGames;
    }

    public void setnGames(int nGames) {
        this.nGames = nGames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServer)) return false;
        AppServer appServer = (AppServer) o;
        return getPort() == appServer.getPort() &&
                getnGames() == appServer.getnGames() &&
                Objects.equals(getIP(), appServer.getIP()) &&
                Objects.equals(getAppServerImpl(), appServer.getAppServerImpl());
    }
}

