package com.ragbecca;

public class Main {

    public static void main(String[] args) {
        ConnectMySQL mysqlConnect = new ConnectMySQL();
        SwingApp swingApp = new SwingApp();
        swingApp.createView(mysqlConnect);
    }
}
