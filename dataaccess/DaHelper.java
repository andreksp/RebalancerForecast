package com.poliusp.monografia.dataaccess;
import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;

public class DaHelper {

    Connection conn = null;
    public DaHelper() throws SQLException, ClassNotFoundException {
        conn = CreateConnection();
    }

    private Connection CreateConnection() throws ClassNotFoundException, SQLException {

        Connection conn = DriverManager.getConnection("jdbc:sqlserver://WIN-T0C6086KPD4\\SQLEXPRESS;DatabaseName=Ibovespa;integratedSecurity=true");
        return conn;
    }

    public ResultSet ExecututeReader(String query) throws ClassNotFoundException, SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }
    public boolean ExecututeQuery(String query) throws ClassNotFoundException, SQLException {
        Statement statement = conn.createStatement();
        return statement.execute(query);
    }

    public void Close() throws SQLException {
        conn.close();
    }
}
