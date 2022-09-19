package com.ragbecca;

import com.ragbecca.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SwingApp extends JFrame {

    private JTextArea brandText;

    private JTextArea priceText;

    private JTextArea typeText;

    private JTextArea stockText;

    private JTextArea descriptionText;

    /**
     * List with all the phones to be accessed later
     */
    private List<Phone> phones;

    private Utils utils;

    private Container container;

    private SpringLayout springLayout;

    private ConnectMySQL connectMySQL;


    /**
     * Create the view for the application
     *
     * @param connectMySQL the initialization of the database connection. There should only be one initialization in the whole project!
     */
    public void createView(ConnectMySQL connectMySQL) {

        setDefaultLookAndFeelDecorated(true);

        springLayout = new SpringLayout();

        this.connectMySQL = connectMySQL;

        utils = new Utils();

        setLayout(springLayout);

        container = getContentPane();

        phones = getData();

        JTable jTable = addAllVisibleComponents();

        addHierarchyListener(e -> {
            if (e.getChanged().isEnabled()) {
                phones = getData();
                updatePhoneViewer(phones, jTable);
            }
        });

        setValuesJFrame();
    }

    private void setValuesJFrame() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int widthScreen = screenSize.width / 2;
        int heightScreen = 560;

        setPreferredSize(new Dimension(widthScreen, heightScreen));

        setTitle("Phoneshop");
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JTable addAllVisibleComponents() {
        JTable jTable = createPhoneViewer();
        createSearchBar(jTable);

        addButton();
        removeButton(jTable);
        exitButton();

        createLabels();

        return jTable;
    }

    /**
     * The creation of all individual labels that aren't special.
     */
    private void createLabels() {
        Container container = getContentPane();

        brandText = createTextAreaLabel();
        JLabel brandLabel = createLabel("Brand");
        priceText = createTextAreaLabel();
        JLabel priceLabel = createLabel("Price");
        typeText = createTextAreaLabel();
        JLabel typeLabel = createLabel("Type");
        stockText = createTextAreaLabel();
        JLabel stockLabel = createLabel("Stock");

        descriptionText = addDecription();

        utils.putConstraint(springLayout, brandLabel, 10, -1, 300, 260, container);
        utils.putConstraint(springLayout, brandText, 10, -1, 440, 310, container);

        utils.putConstraint(springLayout, priceLabel, 10, -1, 520, 480, container);
        utils.putConstraint(springLayout, priceText, 10, -1, 670, 530, container);

        utils.putConstraint(springLayout, typeLabel, 50, -1, 300, 260, container);
        utils.putConstraint(springLayout, typeText, 50, -1, 440, 310, container);

        utils.putConstraint(springLayout, stockLabel, 50, -1, 520, 480, container);
        utils.putConstraint(springLayout, stockText, 50, -1, 670, 530, container);
    }

    /**
     * The creation of the Searchbar in order to Search on the application
     *
     * @param jTable The table that needs to be updated when the search is happening
     */
    private void createSearchBar(JTable jTable) {

        JTextField textField = new JTextField("Search");
        addPlaceholderStyle(textField);

        textField.addKeyListener(searchBarKeyListener(textField, jTable));
        textField.addFocusListener(searchBarFocusListener(textField));

        add(textField);

        utils.putConstraint(springLayout, textField, 10, -1, 240, 10, container);
    }

    /**
     * Opens an external view which makes it possible to add another com.ragbecca.Phone to the system
     */
    private void addButton() {
        JButton addButton = new JButton("+");

        addButton.addActionListener(e -> {
            SwingAddApp swingAddApp = new SwingAddApp();
            swingAddApp.addNewPhone(this, connectMySQL);
        });

        add(addButton);

        utils.putConstraint(springLayout, addButton, 500, 520, 70, 10, container);

    }

    /**
     * Removes a value from the DB and screen
     *
     * @param jTable The Table which the value will be grabbed from to be deleted
     */
    private void removeButton(JTable jTable) {

        JButton removeButton = new JButton("-");

        removeButton.addActionListener(e -> {
            if (!jTable.isColumnSelected(0)) {
                return;
            }
            int viewRow = jTable.getSelectedRow();
            int modelRow = jTable.convertRowIndexToModel(viewRow);
            String value = (String) jTable.getModel().getValueAt(modelRow, 0);
            String information = value.substring(value.indexOf(" ") + 1);

            try {
                connectMySQL.connect().prepareStatement("DELETE FROM javabase.phone WHERE Type='" + information + "';").execute();

                System.out.println("Succesfully deleted");

                DefaultTableModel model = (DefaultTableModel) jTable.getModel();


                List<Phone> phonesGotten = getData();
                updatePhoneViewer(phonesGotten, jTable);
                model.fireTableDataChanged();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        utils.putConstraint(springLayout, removeButton, 500, 520, 140, 80, container);

        add(removeButton);
    }

    /**
     * Creates an exit button which closes the whole console
     */
    private void exitButton() {

        JButton exitButton = new JButton("Exit");

        exitButton.addActionListener(e -> dispose());

        utils.putConstraint(springLayout, exitButton, 500, 520, 670, 610, container);

        add(exitButton);
    }

    /**
     * Adds the description + description label to the view
     *
     * @return sends back the TextArea that has been created. For changes later on
     */
    private JTextArea addDecription() {
        JTextArea jTextArea = new JTextArea(" ");
        JLabel jLabel = new JLabel("Description");
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);

        utils.putConstraint(springLayout, jLabel, 80, -1, 670, 260, container);
        utils.putConstraint(springLayout, jTextArea, 100, 480, 670, 260, container);
        add(jTextArea);
        add(jLabel);

        return jTextArea;
    }


    /**
     * Creates a TextArea for viewing
     *
     * @return sends back the TextArea that has been created. For changes later on
     */
    private JTextArea createTextAreaLabel() {
        JTextArea jTextArea = new JTextArea(" ");
        jTextArea.setEditable(false);
        add(jTextArea);
        return jTextArea;
    }

    /**
     * Creates a label for viewing
     *
     * @param text The text visible on the label
     * @return sends back the Label that has been created. For changes later on
     */
    private JLabel createLabel(String text) {
        JLabel jLabel = new JLabel(text);
        add(jLabel);
        return jLabel;
    }


    /**
     * Creates a view of the PhoneList
     *
     * @return JTable that is filled and initialized. Will be usable for checks and updates
     */
    private JTable createPhoneViewer() {

        String[] column = {"Phone"};
        String[][] values = getRowValues();
        DefaultTableModel model = new DefaultTableModel(values, column);
        JTable jTable = new JTable(values, column) {
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };

        jTable.setModel(model);

        jTable.addMouseListener(phoneViewerMouseListener(jTable));

        giveExtraValueToJTable(jTable);

        JScrollPane scrollPane = new JScrollPane(jTable);

        utils.putConstraint(springLayout, scrollPane, 40, 480, 240, 10, container);

        add(scrollPane);
        return jTable;
    }

    /**
     * Adds a sorter on column 0 and removes the TableHeader
     *
     * @param jTable the JTable that needs to be edited
     */
    private void giveExtraValueToJTable(JTable jTable) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(jTable.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        int columnIndexToSort = 0;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.sort();
        jTable.setRowSorter(sorter);

        jTable.setTableHeader(new JTableHeader());
    }

    /**
     * Gets the values for the rows of the table
     *
     * @return returns the values gotten from the phones
     */
    private String[][] getRowValues() {
        List<String[]> data = new ArrayList<>();
        for (Phone phone : phones) {
            String[] phoneData = {phone.getBrand() + " " + phone.getType()};
            data.add(phoneData);
        }

        String[][] values = new String[data.size()][1];

        for (int i = 0; i < data.size(); i++) {
            values[i] = data.get(i);
        }
        return values;
    }

    /**
     * Shows a singular phone with all values in the TextArea's.
     *
     * @param phone Sends the phone with which needs to be shown.
     */
    private void viewPhone(Phone phone) {
        brandText.setText(phone.getBrand());
        priceText.setText("€ " + phone.getPrice().setScale(2, RoundingMode.HALF_DOWN));
        typeText.setText(phone.getType());
        stockText.setText(String.valueOf(phone.getStock()));
        descriptionText.setText(phone.getDescription());
    }


    /**
     * Grab the data from the MySQL database and returns it.
     *
     * @return a list of phones with all the data from the database.
     */
    private List<Phone> getData() {
        try {
            ResultSet resultSet = connectMySQL.connect().prepareStatement("SELECT P.phone_ID, P.`Type`," +
                    " P.Description, P.Price, P.Stock, B.company_name FROM javabase.phone P INNER JOIN javabase.brands B" +
                    " ON P.brand_ID = B.brand_id;").executeQuery();

            List<Phone> phones = new ArrayList<>();

            while (resultSet.next()) {
                Phone phone = new Phone(resultSet.getInt("phone_ID"), resultSet.getString("company_name"),
                        resultSet.getString("Type"), resultSet.getString("Description"),
                        resultSet.getBigDecimal("Price"), resultSet.getInt("Stock"));
                phones.add(phone);
            }
            resultSet.close();
            connectMySQL.disconnect();
            return phones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the placeholderStyle to a TextField. This TextField is commonly a Search.
     * Shows the specialised style of a TextField
     *
     * @param textField The TextField that needs to be shown with a special style
     */
    private void addPlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.ITALIC);
        textField.setFont(font);
        textField.setForeground(Color.gray);
    }

    /**
     * Removes the placeholderStyle from a TextField. This TextField is commonly a Search.
     * Shows the normal style of a TextField
     *
     * @param textField The TextField that needs to become normal
     */
    private void removePlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.PLAIN | Font.BOLD);
        textField.setFont(font);
        textField.setForeground(Color.black);
    }

    /**
     * Update the phone viewer on the first view. The phone-viewer is also known as the jTable.
     *
     * @param phones The list of phones that's saved, used to show which phones there are on the table.
     * @param jTable The table that shows the phones
     */
    private void updatePhoneViewer(List<Phone> phones, JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();

        model.fireTableDataChanged();
        while (model.getRowCount() > 0) {
            for (int row = 0; row < model.getRowCount(); row++) {
                model.removeRow(0);
            }
        }
        if (phones.size() == 0) {
            return;
        }
        for (Phone phone : phones) {
            String[] phoneData = {phone.getBrand() + " " + phone.getType()};
            model.addRow(phoneData);
        }
        model.fireTableDataChanged();
    }

    /**
     * Creates a MouseListener that listens to when a row is clicked on the JTable
     *
     * @param jTable The table (phone table) the listener needs to be attached to
     * @return Shows the JTable row their values at the correct Labels and TextFields
     */
    private MouseListener phoneViewerMouseListener(JTable jTable) {

        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = jTable.getSelectedRow();
                int modelRow = jTable.convertRowIndexToModel(viewRow);
                String value = (String) jTable.getRowSorter().getModel().getValueAt(modelRow, 0);
                String information = value.substring(value.indexOf(" ") + 1);
                boolean phoneThere = phones.stream().anyMatch(findPhone -> Objects.equals(findPhone.getType(), information));
                if (phoneThere) {
                    Phone phone = phones.stream().filter(findPhone -> Objects.equals(findPhone.getType(), information)).findFirst().get();
                    viewPhone(phone);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    /**
     * Creates a keylistener that listens to KeyTyped, updates the JTable with phones after 3 keys are typed
     *
     * @param textField The TextField the key is typed in
     * @param jTable    The JTable that needs to be updated
     * @return The KeyListener that needs to be added to the TextField
     */
    private KeyListener searchBarKeyListener(JTextField textField, JTable jTable) {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (textField.getText().isBlank()) {
                    List<Phone> searchPhones = phones;
                    updatePhoneViewer(searchPhones, jTable);
                    return;
                } else if (textField.getText().length() < 3) {
                    List<Phone> searchPhones = phones;
                    updatePhoneViewer(searchPhones, jTable);
                    return;
                }
                List<Phone> searchPhones = phones.stream().filter(phoneFind ->
                                phoneFind.getType().toLowerCase().contains(textField.getText().toLowerCase())
                                        || phoneFind.getBrand().toLowerCase().contains(textField.getText().toLowerCase())
                                        || phoneFind.getDescription().toLowerCase().contains(textField.getText().toLowerCase()))
                        .collect(Collectors.toList());
                updatePhoneViewer(searchPhones, jTable);
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
    }

    /**
     * Creates a FocusListener that edits the TextField with information when selected or deselected
     *
     * @param textField the TextField to be changed
     * @return the FocusListener to be added to the TextField
     */
    private FocusListener searchBarFocusListener(JTextField textField) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("Search")) {
                    textField.setText(null);
                    textField.requestFocus();
                    removePlaceholderStyle(textField);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().length() == 0) {
                    addPlaceholderStyle(textField);
                    textField.setText("Search");
                }
            }
        };
    }
}
