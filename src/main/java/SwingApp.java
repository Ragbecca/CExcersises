import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SwingApp {

    static JTextArea brandText;
    static JTextArea priceText;
    static JTextArea typeText;
    static JTextArea stockText;
    static JTextArea descriptionText;

    static List<Phone> phones;

    public static void createView(ConnectMySQL connectMySQL) {

        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame jFrame = new JFrame();
        SpringLayout sprLayout = new SpringLayout();

        jFrame.setLayout(sprLayout);

        phones = getData(connectMySQL);
        JTable jTable = createPhoneViewer(jFrame, sprLayout);
        createSearchBar(jFrame, sprLayout, jTable);

        addButton(jFrame, sprLayout, connectMySQL);
        removeButton(jFrame, sprLayout, jTable, connectMySQL);
        exitButton(jFrame, sprLayout);

        jFrame.addHierarchyListener(e -> {
            if (e.getChanged().isEnabled()) {
                phones = getData(connectMySQL);
                updatePhoneViewer(phones, jTable);
            }
        });

        createLabels(jFrame, sprLayout);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int widthScreen = screenSize.width / 2;
        int heightScreen = 560;

        jFrame.setPreferredSize(new Dimension(widthScreen, heightScreen));

        jFrame.setTitle("Phoneshop");
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    public static void createLabels(JFrame jFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        brandText = createLabel(jFrame);
        JLabel brandLabel = createLabel(jFrame, "Brand");
        priceText = createLabel(jFrame);
        JLabel priceLabel = createLabel(jFrame, "Price");
        typeText = createLabel(jFrame);
        JLabel typeLabel = createLabel(jFrame, "Type");
        stockText = createLabel(jFrame);
        JLabel stockLabel = createLabel(jFrame, "Stock");

        descriptionText = addDecription(jFrame, springLayout);

        putConstraint(springLayout, brandLabel, 10, -1, 300, 260, container);
        putConstraint(springLayout, brandText, 10, -1, 440, 310, container);


        putConstraint(springLayout, priceLabel, 10, -1, 520, 480, container);
        putConstraint(springLayout, priceText, 10, -1, 670, 530, container);

        putConstraint(springLayout, typeLabel, 50, -1, 300, 260, container);
        putConstraint(springLayout, typeText, 50, -1, 440, 310, container);

        putConstraint(springLayout, stockLabel, 50, -1, 520, 480, container);
        putConstraint(springLayout, stockText, 50, -1, 670, 530, container);
    }

    public static void createSearchBar(JFrame jFrame, SpringLayout springLayout, JTable jTable) {
        Container container = jFrame.getContentPane();

        JTextField textField = new JTextField("Search");
        addPlaceholderStyle(textField);

        textField.addKeyListener(new KeyListener() {
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
        });

        textField.addFocusListener(new FocusListener() {
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
        });

        jFrame.add(textField);

        putConstraint(springLayout, textField, 10, -1, 240, 10, container);
    }

    public static void addButton(JFrame jFrame, SpringLayout springLayout, ConnectMySQL connectMySQL) {
        Container container = jFrame.getContentPane();

        JButton addButton = new JButton("+");

        addButton.addActionListener(e -> {
            SwingAddApp.addNewPhone(jFrame , connectMySQL);
        });

        jFrame.add(addButton);

        putConstraint(springLayout, addButton, 500, 520, 70, 10, container);

    }

    public static void removeButton(JFrame jFrame, SpringLayout springLayout, JTable jTable, ConnectMySQL connectMySQL) {

        Container container = jFrame.getContentPane();

        JButton removeButton = new JButton("-");

        removeButton.addActionListener(e -> {
            if (!jTable.isColumnSelected(0)) {
                return;
            }
            int i = jTable.getSelectedRow();
            String value = (String) jTable.getModel().getValueAt(i, 0);
            String information = value.substring(value.indexOf(" ") + 1);

            try {
                connectMySQL.connect().prepareStatement("DELETE FROM javabase.phone WHERE Type='" + information +"';").execute();

                getData(connectMySQL);
                System.out.println("Succesfully deleted");

                DefaultTableModel model = (DefaultTableModel) jTable.getModel();


                List<Phone> phonesGotten = getData(connectMySQL);
                updatePhoneViewer(phonesGotten, jTable);
                model.fireTableDataChanged();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        putConstraint(springLayout, removeButton, 500, 520, 140, 80, container);

        jFrame.add(removeButton);
    }

    public static void exitButton(JFrame jFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        JButton exitButton = new JButton("Exit");

        exitButton.addActionListener(e -> jFrame.dispose());

        putConstraint(springLayout, exitButton, 500, 520, 670, 610, container);

        jFrame.add(exitButton);
    }

    public static JTextArea addDecription(JFrame jFrame, SpringLayout springLayout) {
        JTextArea jTextArea = new JTextArea(" ");
        JLabel jLabel = new JLabel("Description");
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);

        Container container = jFrame.getContentPane();

        putConstraint(springLayout, jLabel, 80, -1, 670, 260, container);

        putConstraint(springLayout, jTextArea, 100, 480, 670, 260, container);

        jFrame.add(jTextArea);
        jFrame.add(jLabel);

        return jTextArea;
    }


    public static JTextArea createLabel(JFrame jFrame) {
        JTextArea jTextArea = new JTextArea(" ");
        jTextArea.setEditable(false);
        jFrame.add(jTextArea);
        return jTextArea;
    }

    public static JLabel createLabel(JFrame jFrame, String text) {
        JLabel jLabel = new JLabel(text);
        jFrame.add(jLabel);
        return jLabel;
    }


    public static JTable createPhoneViewer(JFrame jFrame, SpringLayout springLayout) {
        Container container = jFrame.getContentPane();

        List<String[]> data = new ArrayList<>();
        for (Phone phone : phones) {
            String[] phoneData = {phone.getBrand() + " " + phone.getType()};
            data.add(phoneData);
        }

        String[][] strings = new String[data.size()][1];

        for (int i = 0; i < data.size(); i++) {
            strings[i] = data.get(i);
        }

        String[] column = {"Phone"};

        DefaultTableModel model = new DefaultTableModel(strings, column);
        JTable jTable = new JTable(strings, column) {
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };

        jTable.setModel(model);

        jTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = jTable.getSelectedRow();
                String value = (String) jTable.getModel().getValueAt(i, 0);
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
        });


        TableRowSorter<TableModel> sorter = new TableRowSorter<>(jTable.getModel());
        jTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        int columnIndexToSort = 0;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.sort();
        jTable.setTableHeader(new JTableHeader());
        JScrollPane scrollPane = new JScrollPane(jTable);

        putConstraint(springLayout, scrollPane, 40, 480, 240, 10, container);

        jFrame.add(scrollPane);
        return jTable;
    }

    public static void viewPhone(Phone phone) {
        brandText.setText(phone.getBrand());
        priceText.setText("€ " + BigDecimal.valueOf(phone.getPrice()).setScale(2, RoundingMode.HALF_DOWN));
        typeText.setText(phone.getType());
        stockText.setText(String.valueOf(phone.getStock()));
        descriptionText.setText(phone.getDescription());
    }


    public static List<Phone> getData(ConnectMySQL connectMySQL) {
        try {
            ResultSet resultSet = connectMySQL.connect().prepareStatement("SELECT P.phone_ID, P.`Type`," +
                    " P.Description, P.Price, P.Stock, B.company_name FROM javabase.phone P INNER JOIN javabase.brands B" +
                    " ON P.brand_ID = B.brand_id;").executeQuery();

            List<Phone> phones = new ArrayList<>();

            while (resultSet.next()) {
                Phone phone = new Phone(resultSet.getInt("phone_ID"), resultSet.getString("company_name"),
                        resultSet.getString("Type"), resultSet.getString("Description"),
                        resultSet.getDouble("Price"), resultSet.getInt("Stock"));
                phones.add(phone);
            }
            resultSet.close();
            connectMySQL.disconnect();
            return phones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addPlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.ITALIC);
        textField.setFont(font);
        textField.setForeground(Color.gray);
    }

    public static void removePlaceholderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(Font.PLAIN | Font.BOLD);
        textField.setFont(font);
        textField.setForeground(Color.black);
    }

    public static void updatePhoneViewer(List<Phone> phones, JTable jTable) {
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

    public static void putConstraint(SpringLayout springLayout, Component component, int padNorth, int padSouth, int padEast, int padWest, Container container) {
        if (padNorth > 0) {
            springLayout.putConstraint(SpringLayout.NORTH, component, padNorth, SpringLayout.NORTH, container);
        }
        if (padSouth > 0) {
            springLayout.putConstraint(SpringLayout.SOUTH, component, padSouth, SpringLayout.NORTH, container);
        }
        if (padEast > 0) {
            springLayout.putConstraint(SpringLayout.EAST, component, padEast, SpringLayout.WEST, container);
        }
        if (padWest > 0) {
            springLayout.putConstraint(SpringLayout.WEST, component, padWest, SpringLayout.WEST, container);
        }
    }
}
