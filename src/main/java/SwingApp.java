import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SwingApp {

    public static void createView() {
        JFrame jFrame = new JFrame();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        int screenWidth = size.width / 2;
        int screenHeight = size.height - 40;

        jFrame.setLayout(new GridBagLayout());

        createCloseButton(jFrame, screenWidth, screenHeight);

        jFrame.setSize(screenWidth, screenHeight);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

        getData();
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

    public static void getData(ConnectMySQL connectMySQL) {
        try {
            Statement statement = connectMySQL.connect().prepareStatement("SELECT * FROM javabase.phone;");
            ResultSet resultSet = statement.getResultSet();

            List<Phone> phones = new ArrayList<Phone>();

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
