import interfaces.DataBaseInterface;

public class DataBaseDisp {

    private DataBaseInterface dataBase;

    public DataBaseDisp(DataBaseInterface dbImp) {
        this.dataBase = dbImp;
    }

    public DataBaseDisp() {
    }

    public DataBaseInterface getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBaseInterface dataBase) {
        this.dataBase = dataBase;
    }
}
