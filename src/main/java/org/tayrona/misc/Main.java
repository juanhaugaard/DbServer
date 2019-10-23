package org.tayrona.misc;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        FunctionMultiReturn app = new FunctionMultiReturn();
        try {
            app.test();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
