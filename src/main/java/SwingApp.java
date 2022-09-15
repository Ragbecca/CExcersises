import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SwingApp {

    public static void createView(ConnectMySQL connectMySQL) {
        JFrame jFrame = new JFrame();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        int screenWidth = size.width / 2;
        int screenHeight = size.height - 40;

        jFrame.setLayout(new GridBagLayout());

        createCloseButton(jFrame, screenWidth, screenHeight);

        List<Phone> phones = getData(connectMySQL);
        createPhoneViewer(jFrame, screenWidth, screenHeight, phones);

        jFrame.setSize(screenWidth, screenHeight);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    public static void createPhoneViewer(JFrame jFrame, int screenWidth, int screenHeight, List<Phone> phones) {
        List<String[]> data = new ArrayList<>();
        for (Phone phone : phones) {
            String[] phoneData = {String.valueOf(phone.getPhone_id()), phone.getBrand(), phone.getType(), String.valueOf(phone.getPrice())};
            data.add(phoneData);
        }

        String[][] strings = new String[data.size()][3];

        for (int i = 0; i<data.size(); i++) {
            
        }

        String[] column = {"ID", "MODEL", "TYPE", "PRICE"};
        final JTable jt = new JTable(arrayData, column);
        jFrame.add(jt);
    }

    public static void createCloseButton(JFrame jFrame, int screenWidth, int screenHeight) {
        GridBagConstraints gbc = new GridBagConstraints();
        JButton jButton = new JButton("Close");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        jButton.setBounds((screenWidth - 85), (screenHeight - 35), 80, 30);
        jButton.addActionListener(e -> {
            jFrame.dispose();
        });
        jFrame.add(jButton, gbc);
    }

    public static List<Phone> getData(ConnectMySQL connectMySQL) {
        try {
            ResultSet resultSet = connectMySQL.connect().prepareStatement("SELECT * FROM javabase.phone;").executeQuery();

            List<Phone> phones = new ArrayList<>();

            while (resultSet.next()) {
                Phone phone = new Phone(resultSet.getInt("phone_ID"),
                        resultSet.getString("Brand"),
                        resultSet.getString("Type"),
                        resultSet.getString("Description"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("Stock"));
                phones.add(phone);
            }
            resultSet.close();
            connectMySQL.disconnect();
            return phones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void searchPhone() {

    }
}
