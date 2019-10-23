package org.tayrona.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FunctionMultiReturn {
    private String className = Polar2Cartesian.class.getName();
    String sql1 = String.format("CREATE ALIAS P2C FOR \"%s.polar2Cartesian\"", className);
    String sql2 = String.format("CREATE TABLE TEST(ID IDENTITY, R DOUBLE, A DOUBLE)");
    String sql3 = String.format("CREATE ALIAS P2C_SET FOR \"%s.polar2CartesianSet\" ", className);
    String sql4 = String.format("CREATE ALIAS P2C_A FOR \"%s.polar2CartesianArray\" ", className);
    String sql5 = String.format("SELECT R, A, ARRAY_GET(E, 1), ARRAY_GET(E, 2) FROM (SELECT R, A, P2C_A(R, A) E FROM TEST)");

    public void test() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseManager.execute(sql1);
            PreparedStatement prep = DatabaseManager.prepareStatement("SELECT X, Y FROM P2C(?, ?)");
            prep.setDouble(1, 5.0);
            prep.setDouble(2, 0.5);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                double x = rs.getDouble(1);
                double y = rs.getDouble(2);
                System.out.println("result: (x=" + x + ", y=" + y + ")");
            }

            DatabaseManager.execute(sql2);
            DatabaseManager.execute("INSERT INTO TEST(R, A) VALUES(5.0, 0.5), (10.0, 0.6)");
            DatabaseManager.execute(sql3);
            rs = DatabaseManager.executeQuery("SELECT * FROM P2C_SET('SELECT * FROM TEST')");
            while (rs.next()) {
                double r = rs.getDouble("R");
                double a = rs.getDouble("A");
                double x = rs.getDouble("X");
                double y = rs.getDouble("Y");
                System.out.println("(r=" + r + " a=" + a + ") : (x=" + x + ", y=" + y + ")");
            }

            DatabaseManager.execute(sql4);
            rs = DatabaseManager.executeQuery(conn, "SELECT R, A, P2C_A(R, A) FROM TEST");
            while (rs.next()) {
                double r = rs.getDouble(1);
                double a = rs.getDouble(2);
                Object o = rs.getObject(3);
                Object[] xy = (Object[]) o;
                double x = (Double) xy[0];
                double y = (Double) xy[1];
                System.out.println("(r=" + r + " a=" + a + ") : (x=" + x + ", y=" + y + ")");
            }

            rs = DatabaseManager.executeQuery(sql5);
            while (rs.next()) {
                double r = rs.getDouble(1);
                double a = rs.getDouble(2);
                double x = rs.getDouble(3);
                double y = rs.getDouble(4);
                System.out.println("(r=" + r + " a=" + a + ") : (x=" + x + ", y=" + y + ")");
            }
        }
    }
}
