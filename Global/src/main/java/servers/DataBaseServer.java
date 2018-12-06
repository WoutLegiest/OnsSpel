package servers;

import interfaces.DataBaseInterface;

import java.io.Serializable;

/**
 * @author Wouter Legiest on 5/12/2018
 */
public class DataBaseServer implements Serializable {

    private static final long serialVersionUID = -4383476674523935340L;

    private final int port;
    private final String IP;
    private DataBaseInterface dataBaseImpl;
    private int nAppServers;

    public DataBaseServer(int port, String IP, DataBaseInterface dataBaseImpl) {
        this.port = port;
        this.IP = IP;
        this.dataBaseImpl = dataBaseImpl;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    public DataBaseInterface getDataBaseImpl() {
        return dataBaseImpl;
    }

    public void setDataBaseImpl(DataBaseInterface dataBaseImpl) {
        this.dataBaseImpl = dataBaseImpl;
    }

    public int getnAppServers() {
        return nAppServers;
    }

    public void setnAppServers(int nAppServers) {
        this.nAppServers = nAppServers;
    }
}
