import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        ConnectMySQL mysqlConnect = new ConnectMySQL();
        SwingApp.createView(mysqlConnect);
    }
}
