package org.tayrona.misc;

import org.h2.tools.SimpleResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Polar2Cartesian {
    /**
     * Convert polar coordinates to cartesian coordinates. The function may be
     * called twice, once to retrieve the result columns (with null parameters),
     * and the second time to return the data.
     *
     * @param r the distance from the point 0/0
     * @param alpha the angle
     * @return a result set with two columns: x and y
     */
    public static ResultSet polar2Cartesian(Double r, Double alpha) {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("X", Types.DOUBLE, 0, 0);
        rs.addColumn("Y", Types.DOUBLE, 0, 0);
        if (r != null && alpha != null) {
            double x = r * Math.cos(alpha);
            double y = r * Math.sin(alpha);
            rs.addRow(x, y);
        }
        return rs;
    }

    /**
     * Convert polar coordinates to cartesian coordinates. The function may be
     * called twice, once to retrieve the result columns (with null parameters),
     * and the second time to return the data.
     *
     * @param r the distance from the point 0/0
     * @param alpha the angle
     * @return an array two values: x and y
     */
    public static Object[] polar2CartesianArray(Double r, Double alpha) {
        double x = r * Math.cos(alpha);
        double y = r * Math.sin(alpha);
        return new Object[]{x, y};
    }

    /**
     * Convert a set of polar coordinates to cartesian coordinates. The function
     * may be called twice, once to retrieve the result columns (with null
     * parameters), and the second time to return the data.
     *
     * @param conn the connection
     * @param query the query
     * @return a result set with the coordinates
     */
    public static ResultSet polar2CartesianSet(Connection conn, String query) throws SQLException {
        SimpleResultSet result = new SimpleResultSet();
        result.addColumn("R", Types.DOUBLE, 0, 0);
        result.addColumn("A", Types.DOUBLE, 0, 0);
        result.addColumn("X", Types.DOUBLE, 0, 0);
        result.addColumn("Y", Types.DOUBLE, 0, 0);
        if (query != null) {
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                double r = rs.getDouble("R");
                double alpha = rs.getDouble("A");
                double x = r * Math.cos(alpha);
                double y = r * Math.sin(alpha);
                result.addRow(r, alpha, x, y);
            }
        }
        return result;
    }
}
