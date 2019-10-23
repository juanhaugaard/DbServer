package org.tayrona.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DatabaseManager {
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String url = "jdbc:h2:mem:";
    private static final String user = "sa";
    private static final String password = "";
    private static final Map<String, Connection> defaultConnections = new ConcurrentHashMap<>();

    private DatabaseManager() {
    }

    private static String makeKey() {
        return makeKey(url, user, password);
    }

    private static String makeKey(final String url, final String user, final String password) {
        return String.format("%s-%s-%s", url, user, password);
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(url, user, password);
    }

    public static Connection getConnection(final String url, final String user, final String password) throws SQLException {
        Connection ret;
        final String key = makeKey(url, user, password);
        if (!defaultConnections.containsKey(key)) {
            ret = DriverManager.getConnection(url, user, password);
            defaultConnections.put(key, ret);
        } else {
            ret = defaultConnections.get(key);
            if (ret.isClosed()) {
                ret = DriverManager.getConnection(url, user, password);
                defaultConnections.put(key, ret);
            }
        }
        return ret;
    }

    public static Statement createStatement() throws SQLException {
        return createStatement(getConnection());
    }

    public static Statement createStatement(final String url, final String user, final String password) throws SQLException {
        return createStatement(getConnection(url, user, password));
    }

    public static Statement createStatement(final Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public static boolean execute(final String sql) throws SQLException {
        return execute(getConnection(), sql);
    }

    public static boolean execute(final String url, final String user, final String password, final String sql) throws SQLException {
        return execute(getConnection(url, user, password), sql);
    }

    public static boolean execute(final Connection connection, final String sql) throws SQLException {
        return createStatement(connection).execute(sql);
    }

    public static ResultSet executeQuery(final String sql) throws SQLException {
        return executeQuery(getConnection(), sql);
    }

    public static ResultSet executeQuery(final String url, final String user, final String password, final String sql) throws SQLException {
        return executeQuery(getConnection(url, user, password), sql);
    }

    public static ResultSet executeQuery(final Connection connection, final String sql) throws SQLException {
        return createStatement(connection).executeQuery(sql);
    }

    public static PreparedStatement prepareStatement(final String sql) throws SQLException {
        return prepareStatement(getConnection(), sql);
    }

    public static PreparedStatement prepareStatement(final Connection connection, final String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
    public static PreparedStatement prepareStatement(final String url, final String user, final String password, final String sql) throws SQLException {
        return prepareStatement(getConnection(url, user, password), sql);
    }
}
