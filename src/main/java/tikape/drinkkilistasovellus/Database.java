/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.drinkkilistasovellus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mikaelde
 */
public class Database {
    
    private String databaseAddress;
    
    public Database(String databaseAdress) {
        this.databaseAddress = databaseAdress;
    }
    
    public static Connection getConnection() throws Exception {
    String dbUrl = System.getenv("JDBC_DATABASE_URL");
    if (dbUrl != null && dbUrl.length() > 0) {
        return DriverManager.getConnection(dbUrl);
    }

    return DriverManager.getConnection("jdbc:sqlite:drinkkitietokanta.db");
}
    
}
