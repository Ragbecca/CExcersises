package com.ragbecca;

import com.ragbecca.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JComboBox brandComboBox;
    private JTextArea typeText;
    private JTextArea descriptionText;
    private JTextArea priceText;
    private JTextArea stockText;

    private JTextArea errorText;

    private List<Brand> brands;

    private Utils utils;

    private SpringLayout sprLayout;

    private Container container;

    private ConnectMySQL connectMySQL;

    /**
     * Initial method, calls all methods to create the AddPhoneViewer
     *
     * @param otherJFrame  the main JFrame
     * @param connectMySQL the initialized MySQL Connection
     */
    public void addNewPhone(JFrame otherJFrame, ConnectMySQL connectMySQL) {
        utils = new Utils();
        sprLayout = new SpringLayout();
        this.connectMySQL = connectMySQL;
        JFrame jFrame = new JFrame("Add Phone");

        brands = getDataBrands();

        jFrame.setLayout(sprLayout);

        jFrameCreationWithFields(jFrame, otherJFrame);
    }

    /**
     * Calls all fields to be created and updates the JFrame
     *
     * @param jFrame      frame that is AddPhoneViewer frame
     * @param otherJFrame the main frame
     */
    private void jFrameCreationWithFields(JFrame jFrame, JFrame otherJFrame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int widthScreen = screenSize.width / 4;
        int heightScreen = 560;
        jFrame.setPreferredSize(new Dimension(widthScreen, heightScreen));

        container = jFrame.getContentPane();

        addTexts(jFrame);

        addButton(jFrame);

        backButton(jFrame, otherJFrame);

        addBrandComboBox(jFrame);

        addDescription(jFrame);

        addErrorTextField(jFrame);

        jFrame.addWindowListener(jFrameWindowListener(otherJFrame));

        jFrame.setResizable(false);

        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
    }

    /**
     * Creates the ErrorTextField and hides it for later use
     *
     * @param jFrame the frame which it needs to be added to
     */
    private void addErrorTextField(JFrame jFrame) {
        errorText = new JTextArea("");

        errorText.setEditable(false);
        errorText.setLineWrap(true);
        errorText.setWrapStyleWord(true);
        errorText.setVisible(false);

        utils.putConstraint(sprLayout, errorText, 370, 400, 360, 10, container);
        jFrame.add(errorText);
    }

    private void addButton(JFrame jFrame) {

        JButton addButton = new JButton("Add to Database");

        addButton.addActionListener(actionListenerAddButton(jFrame));

        jFrame.add(addButton);

        utils.putConstraint(sprLayout, addButton, 320, 350, 270, 120, container);

    }

    /**
     * Creates an ActionListener which adds the phone to the DB when the add button is called
     *
     * @param jFrame the frame that needs to be disposed off when done
     * @return the ActionListener to be used to initialize it
     */
    private ActionListener actionListenerAddButton(JFrame jFrame) {
        return e -> {
            errorText.setVisible(false);
            String gottenDescriptionText;
            String gottenPriceText;
            String gottenTypeText;
            String gottenStockText;
            double gottenPrice;
            int gottenStock;
            BigDecimal updatedGottenPrice;
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
                updatedGottenPrice = new BigDecimal(gottenPrice).setScale(2, RoundingMode.HALF_DOWN);
            } catch (NumberFormatException error) {
                errorText.setText("Your price wasn't a correct number. Maybe it has to do with you using a , instead of a .");
                errorText.setVisible(true);
                return;
            }
            String gottenBrand = (String) brandComboBox.getSelectedItem();
            Brand realBrand;
            for (Brand brand : brands) {
                if (Objects.equals(brand.getCompany_name(), gottenBrand)) {
                    realBrand = brand;
                    try {
                        connectMySQL.connect().prepareStatement("INSERT INTO javabase.phone (brand_ID, Type, Description, Price, Stock) VALUES ('" +
                                +realBrand.getBrand_id() + "', '" + gottenTypeText + "', '" + gottenDescriptionText + "', '" + updatedGottenPrice + "', '" + gottenStock + "')").execute();
                        jFrame.dispose();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };
    }

    /**
     * Creates a back to main screen button
     *
     * @param jFrame      The frame that needs to be disposed off (AddPhoneViewer)
     * @param otherJFrame The main frame that needs to become visible
     */
    private void backButton(JFrame jFrame, JFrame otherJFrame) {

        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> {
            jFrame.dispose();
            otherJFrame.setVisible(true);
        });

        jFrame.add(backButton);

        utils.putConstraint(sprLayout, backButton, 500, 530, 380, 300, container);
    }

    /**
     * Add each individual text and label
     *
     * @param jFrame The Frame it needs to be added to
     */
    private void addTexts(JFrame jFrame) {
        typeText = new JTextArea();
        priceText = new JTextArea();
        stockText = new JTextArea();

        addTextAndLabel(jFrame, typeText, (short) 10, (short) 30, (short) 50, (short) 10, (short) 160, "Model");
        addTextAndLabel(jFrame, priceText, (short) 50, (short) 70, (short) 50, (short) 10, (short) 160, "Price");
        addTextAndLabel(jFrame, stockText, (short) 50, (short) 70, (short) 220, (short) 180, (short) 330, "Stock");
    }

    /**
     * @param jFrame   The Frame that everything needs to be added to
     * @param textArea The different TextArea that needs to be placed
     * @param padN     Padding on the north side, relative to the north side
     * @param padS     Padding on the south side, relative to the north side (height)
     * @param padE     Padding on the east side, relative to the west side (width)
     * @param padW     Padding on the west side, relative to the west side
     * @param text     The text that needs to be added to the Label
     */
    private void addTextAndLabel(JFrame jFrame, JTextArea textArea, short padN, short padS, short padE, short padW, short pad2W, String text) {
        Container container = jFrame.getContentPane();

        JLabel jLabel = new JLabel(text);

        utils.putConstraint(sprLayout, jLabel, padN, padS, padE, padW, container);
        utils.putConstraint(sprLayout, textArea, padN, padS, pad2W, padE + 10, container);

        jFrame.add(textArea);
        jFrame.add(jLabel);
    }

    /**
     * Adds the description module to the screen
     *
     * @param jFrame The Frame that the description module needs to be added to
     */
    private void addDescription(JFrame jFrame) {
        descriptionText = new JTextArea();

        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);

        JLabel jLabel = new JLabel("Description");

        utils.putConstraint(sprLayout, jLabel, 90, 110, 360, 10, container);
        utils.putConstraint(sprLayout, descriptionText, 120, 300, 360, 10, container);

        jFrame.add(descriptionText);
        jFrame.add(jLabel);
    }

    /**
     * Adds all brands to a ComboBox (Dropdown) to be displayed for easier selection
     *
     * @param jFrame The Frame that the comboBox needs to be added to
     */
    private void addBrandComboBox(JFrame jFrame) {

        String[] brandsString = new String[brands.size()];
        for (int i = 0; i < brands.size(); i++) {
            brandsString[i] = brands.get(i).getCompany_name();
        }
        System.out.println();
        brandComboBox = new JComboBox(Arrays.stream(brandsString).sorted().toArray());
        JLabel jLabel = new JLabel("Brand");

        utils.putConstraint(sprLayout, jLabel, 10, 30, 220, 180, container);
        utils.putConstraint(sprLayout, brandComboBox, 10, 30, 330, 230, container);

        jFrame.add(brandComboBox);
        jFrame.add(jLabel);
    }

    /**
     * Get all brands from the MySQL server
     *
     * @return a list with all brands
     */
    private List<Brand> getDataBrands() {
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

    /**
     * Creates a WindowListener which opens or closes the main window when something happens to this window
     *
     * @param otherJFrame the main window
     * @return the WindowListener that needs to be added to the window
     */
    private WindowListener jFrameWindowListener(JFrame otherJFrame) {
        return new WindowListener() {
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
        };
    }
}
