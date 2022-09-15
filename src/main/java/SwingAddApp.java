import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SwingAddApp {

    static JComboBox brandComboBox;
    static JTextArea typeText;
    static JTextArea descriptionText;
    static JTextArea priceText;
    static JTextArea stockText;

    static JTextArea errorText;

    static List<Brand> brands;

    public static void addNewPhone(JFrame otherJFrame, ConnectMySQL connectMySQL) {
        JFrame jFrame = new JFrame("Add Phone");

        SpringLayout sprLayout = new SpringLayout();

        jFrame.setLayout(sprLayout);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int widthScreen = screenSize.width / 4;
        int heightScreen = 560;

        brands = getDataBrands(connectMySQL);

        jFrame.setPreferredSize(new Dimension(widthScreen, heightScreen));

        addTexts(jFrame, sprLayout);

        addButton(jFrame, sprLayout, connectMySQL);

        backButton(jFrame, otherJFrame, sprLayout);

        addBrandComboBox(jFrame, sprLayout);

        addDescription(jFrame, sprLayout);

        errorText = new JTextArea("");

        errorText.setEditable(false);
        errorText.setLineWrap(true);
        errorText.setWrapStyleWord(true);
        errorText.setVisible(false);

        jFrame.add(errorText);

        Container container = jFrame.getContentPane();

        SwingApp.putConstraint(sprLayout, errorText, 370, 400, 360, 10, container);

        jFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                otherJFrame.setVisible(false);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                otherJFrame.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                otherJFrame.setVisible(true);
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        jFrame.setResizable(false);

        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
    }

    public static void addButton(JFrame jFrame, SpringLayout springLayout, ConnectMySQL connectMySQL) {
        Container container = jFrame.getContentPane();

        JButton addButton = new JButton("Add to Database");

        addButton.addActionListener(e -> {
            errorText.setVisible(false);
            String gottenDescriptionText;
            String gottenPriceText;
            String gottenTypeText;
            String gottenStockText;
            double gottenPrice;
            int gottenStock;
            try {
                gottenDescriptionText = descriptionText.getText();
            } catch (Exception exception) {
                errorText.setText("You didn't fill in the description.");
                errorText.setVisible(true);
                return;
            }
            try {
                gottenPriceText = priceText.getText();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                errorText.setText("You didn't fill in the price.");
                errorText.setVisible(true);
                return;
            }
            try {
                gottenTypeText = typeText.getText();
            } catch (Exception exception) {
                errorText.setText("You didn't fill in the type.");
                errorText.setVisible(true);
                return;
            }
            try {
                gottenStockText = stockText.getText();
            } catch (Exception exception) {
                errorText.setText("You didn't fill in the stock.");
                errorText.setVisible(true);
                return;
            }
            try {
                gottenStock = Integer.parseInt(gottenStockText);
            } catch (NumberFormatException exception) {
                errorText.setText("Your stock wasn't a correct number.");
                errorText.setVisible(true);
                return;
            }
            try {
                gottenPrice = Double.parseDouble(gottenPriceText);
                gottenPrice = new BigDecimal(gottenPrice*1.21).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            } catch (NumberFormatException error) {
                errorText.setText("Your price wasn't a correct number. Maybe it has to do with you using a , instead of a .");
                errorText.setVisible(true);
                return;
            }
            String gottenBrand = (String) brandComboBox.getSelectedItem();
            System.out.println("Brand: " + gottenBrand);
            Brand realBrand;
            for (Brand brand : brands) {
                if (Objects.equals(brand.getCompany_name(), gottenBrand)) {
                    realBrand = brand;
                    try {
                        connectMySQL.connect().prepareStatement("INSERT INTO javabase.phone (brand_ID, Type, Description, Price, Stock) VALUES ('" +
                                + realBrand.getBrand_id() + "', '" + gottenTypeText + "', '" + gottenDescriptionText  + "', '" + gottenPrice  + "', '" + gottenStock + "')").execute();
                        jFrame.dispose();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        jFrame.add(addButton);

        SwingApp.putConstraint(springLayout, addButton, 320, 350, 270, 120, container);

    }

    public static void backButton(JFrame jFrame, JFrame otherJFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> {
            jFrame.dispose();
            otherJFrame.setVisible(true);
        });

        jFrame.add(backButton);

        SwingApp.putConstraint(springLayout, backButton, 500, 530, 380, 300, container);
    }

    public static void addTexts(JFrame jFrame, SpringLayout springLayout) {
        typeText = new JTextArea();
        priceText = new JTextArea();
        stockText = new JTextArea();

        addTextAndLabel(jFrame, typeText, (short) 10, (short) 30, (short) 50, (short) 10, (short) 160, "Model", springLayout);
        addTextAndLabel(jFrame, priceText, (short) 50, (short) 70, (short) 50, (short) 10, (short) 160, "Price", springLayout);
        addTextAndLabel(jFrame, stockText, (short) 50, (short) 70, (short) 220, (short) 180, (short) 330, "Stock", springLayout);
    }

    public static void addTextAndLabel(JFrame jFrame, JTextArea textArea, short padN, short padS, short padE, short padW, short pad2W, String text, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        JLabel jLabel = new JLabel(text);

        SwingApp.putConstraint(springLayout, jLabel, padN, padS, padE, padW, container);
        SwingApp.putConstraint(springLayout, textArea, padN, padS, pad2W, padE + 10, container);

        jFrame.add(textArea);
        jFrame.add(jLabel);
    }

    public static void addDescription(JFrame jFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        descriptionText = new JTextArea();

        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);

        JLabel jLabel = new JLabel("Description");

        SwingApp.putConstraint(springLayout, jLabel, 90, 110, 360, 10, container);
        SwingApp.putConstraint(springLayout, descriptionText, 120, 300, 360, 10, container);

        jFrame.add(descriptionText);
        jFrame.add(jLabel);
    }

    public static void addBrandComboBox(JFrame jFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        String[] brandsString = new String[brands.size()];
        for (int i = 0; i < brands.size(); i++) {
            brandsString[i] = brands.get(i).getCompany_name();
        }
        System.out.println();
        brandComboBox = new JComboBox(Arrays.stream(brandsString).sorted().toArray());
        JLabel jLabel = new JLabel("Brand");

        SwingApp.putConstraint(springLayout, jLabel, 10, 30, 220, 180, container);
        SwingApp.putConstraint(springLayout, brandComboBox, 10, 30, 330, 230, container);

        jFrame.add(brandComboBox);
        jFrame.add(jLabel);
    }

    public static List<Brand> getDataBrands(ConnectMySQL connectMySQL) {
        try {
            ResultSet resultSet = connectMySQL.connect().prepareStatement("SELECT * FROM javabase.brands").executeQuery();

            List<Brand> brands = new ArrayList<>();

            while (resultSet.next()) {
                Brand brand = new Brand(resultSet.getInt("brand_id"), resultSet.getString("company_name"));
                brands.add(brand);
            }
            resultSet.close();
            connectMySQL.disconnect();
            return brands;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
