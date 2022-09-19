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

public class SwingAddApp extends JFrame {

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

        brands = getDataBrands();

        setTitle("Add Phone");
        setLayout(sprLayout);

        jFrameCreationWithFields(otherJFrame);
    }

    /**
     * Calls all fields to be created and updates the JFrame
     *
     * @param otherJFrame the main frame
     */
    private void jFrameCreationWithFields(JFrame otherJFrame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int widthScreen = screenSize.width / 4;
        int heightScreen = 560;
        setPreferredSize(new Dimension(widthScreen, heightScreen));

        container = getContentPane();

        addTexts();

        addButton();

        backButton(otherJFrame);

        addBrandComboBox();

        addDescription();

        addErrorTextField();

        addWindowListener(jFrameWindowListener(otherJFrame));

        setResizable(false);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Creates the ErrorTextField and hides it for later use
     */
    private void addErrorTextField() {
        errorText = new JTextArea("");

        errorText.setEditable(false);
        errorText.setLineWrap(true);
        errorText.setWrapStyleWord(true);
        errorText.setVisible(false);

        utils.putConstraint(sprLayout, errorText, 370, 400, 360, 10, container);
        add(errorText);
    }

    private void addButton() {

        JButton addButton = new JButton("Add to Database");

        addButton.addActionListener(actionListenerAddButton());

        add(addButton);

        utils.putConstraint(sprLayout, addButton, 320, 350, 270, 120, container);

    }

    /**
     * Creates an ActionListener which adds the phone to the DB when the add button is called
     *
     * @return the ActionListener to be used to initialize it
     */
    private ActionListener actionListenerAddButton() {
        return e -> {
            errorText.setVisible(false);
            BigDecimal updatedGottenPrice;
            if (!isDescriptionTextFilledIn() || !isPriceTextFilledInAndCorrect() || !isTypeFilledIn() || !isStockFilledInAndCorrect()) {
                return;
            }
            updatedGottenPrice = new BigDecimal(priceText.getText()).setScale(2, RoundingMode.HALF_DOWN);
            String gottenBrand = (String) brandComboBox.getSelectedItem();
            for (Brand brand : brands) {
                if (Objects.equals(brand.getCompany_name(), gottenBrand)) {
                    try {
                        connectMySQL.connect().prepareStatement("INSERT INTO javabase.phone (brand_ID, Type, Description, Price, Stock) VALUES ('" +
                                brand.getBrand_id() + "', '" + typeText.getText() + "', '" + descriptionText.getText() + "', '" + updatedGottenPrice + "', '" + Integer.parseInt(stockText.getText()) + "')").execute();
                        dispose();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };
    }

    private boolean isDescriptionTextFilledIn() {
        try {
            descriptionText.getText();
            return true;
        } catch (Exception exception) {
            errorMessageEdit(false, "description");
            return false;
        }
    }

    private boolean isPriceTextFilledInAndCorrect() {
        try {
            String gottenPriceText = priceText.getText();
            double gottenPrice = Double.parseDouble(gottenPriceText);
            new BigDecimal(gottenPrice).setScale(2, RoundingMode.HALF_DOWN);
            return true;
        } catch (NumberFormatException exception) {
            errorMessageEdit(true, "price");
            return false;
        } catch (Exception exception) {
            errorMessageEdit(false, "price");
            return false;
        }
    }

    private boolean isTypeFilledIn() {
        try {
            typeText.getText();
            return true;
        } catch (Exception exception) {
            errorMessageEdit(false, "model");
            return false;
        }
    }

    private boolean isStockFilledInAndCorrect() {
        try {
            String gottenStockText = stockText.getText();
            Integer.parseInt(gottenStockText);
            return true;
        } catch (NumberFormatException exception) {
            errorMessageEdit(true, "stock");
            return false;
        } catch (Exception exception) {
            errorMessageEdit(false, "stock");
            return false;
        }
    }

    private void errorMessageEdit(boolean isSpecialType, String typeOfError) {
        if (isSpecialType) {
            errorText.setText("Your " + typeOfError + " wasn't a correct number.");
            errorText.setVisible(true);
            return;
        }
        errorText.setText("You didn't fill in the " + typeOfError + ".");
        errorText.setVisible(true);
    }

    /**
     * Creates a back to main screen button
     *
     * @param otherJFrame The main frame that needs to become visible
     */
    private void backButton(JFrame otherJFrame) {

        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> {
            dispose();
            otherJFrame.setVisible(true);
        });

        add(backButton);

        utils.putConstraint(sprLayout, backButton, 500, 530, 380, 300, container);
    }

    /**
     * Add each individual text and label
     */
    private void addTexts() {
        typeText = new JTextArea();
        priceText = new JTextArea();
        stockText = new JTextArea();

        addTextAndLabel(typeText, (short) 10, (short) 30, (short) 50, (short) 10, (short) 160, "Model");
        addTextAndLabel(priceText, (short) 50, (short) 70, (short) 50, (short) 10, (short) 160, "Price");
        addTextAndLabel(stockText, (short) 50, (short) 70, (short) 220, (short) 180, (short) 330, "Stock");
    }

    /**
     * @param textArea The different TextArea that needs to be placed
     * @param padN     Padding on the north side, relative to the north side
     * @param padS     Padding on the south side, relative to the north side (height)
     * @param padE     Padding on the east side, relative to the west side (width)
     * @param padW     Padding on the west side, relative to the west side
     * @param text     The text that needs to be added to the Label
     */
    private void addTextAndLabel(JTextArea textArea, short padN, short padS, short padE, short padW, short pad2W, String text) {

        JLabel jLabel = new JLabel(text);

        utils.putConstraint(sprLayout, jLabel, padN, padS, padE, padW, container);
        utils.putConstraint(sprLayout, textArea, padN, padS, pad2W, padE + 10, container);

        add(textArea);
        add(jLabel);
    }

    /**
     * Adds the description module to the screen
     */
    private void addDescription() {
        descriptionText = new JTextArea();

        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);

        JLabel jLabel = new JLabel("Description");

        utils.putConstraint(sprLayout, jLabel, 90, 110, 360, 10, container);
        utils.putConstraint(sprLayout, descriptionText, 120, 300, 360, 10, container);

        add(descriptionText);
        add(jLabel);
    }

    /**
     * Adds all brands to a ComboBox (Dropdown) to be displayed for easier selection
     */
    private void addBrandComboBox() {

        String[] brandsString = new String[brands.size()];
        for (int i = 0; i < brands.size(); i++) {
            brandsString[i] = brands.get(i).getCompany_name();
        }
        System.out.println();
        brandComboBox = new JComboBox(Arrays.stream(brandsString).sorted().toArray());
        JLabel jLabel = new JLabel("Brand");

        utils.putConstraint(sprLayout, jLabel, 10, 30, 220, 180, container);
        utils.putConstraint(sprLayout, brandComboBox, 10, 30, 330, 230, container);

        add(brandComboBox);
        add(jLabel);
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
