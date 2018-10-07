package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VehicleTest {

    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db;create=true";
    static final String USER = "app";
    static final String PASSWORD = "app";
    private static Connection conn;

    @BeforeClass
    public static void initJdbc() {
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Verbindung zur Datenbank nicht möglich:\n"
                    + e.getMessage() + "\n");
            System.exit(1);
        }
    }

    @AfterClass
    public static void teardownJdbc() {
        try {
            if (conn != null || !conn.isClosed()) {
                conn.close();
                System.out.println("Goodbye!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //@Ignore
    @Test
    public void ddl() {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE vehicle (" +
                    " id INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                    " brand VARCHAR(255) NOT NULL," +
                    " type VARCHAR(255) NOT NULL)";
/*
            String sql = "CREATE TABLE vehicle (" +
                    " id INT CONSTRAINT vehicle_pk PRIMARY KEY" +
                    " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    " brand VARCHAR(255) NOT NULL," +
                    " type VARCHAR(255) NOT NULL)";
*/

            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void dml() {

        //Daten einfügen
        int countInserts = 0;
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO vehicle (id, brand, type) VALUES (1, 'Opel','Commodore')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (2, 'Opel','Kapitän')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (3, 'Opel','Kadett')";
            countInserts += stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        assertThat(countInserts, is(3));

        //Daten abfragen
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, brand, type FROM vehicle");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kadett"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}

