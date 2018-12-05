package servers;

import interfaces.AppServerInterface;

import java.io.Serializable;

/**
 * @author Wouter Legiest on 5/12/2018
 */
public class AppServer  implements Serializable {

    private static final long serialVersionUID = -3325140243868020180L;

    private final int port;
    private final String IP;
    private AppServerInterface appServerImpl;

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
}
