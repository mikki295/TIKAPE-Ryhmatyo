/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.drinkkilistasovellus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mikaelde
 */
public class DrinkkiDao implements Dao<Drinkki, Integer> {

    private Database database;

    public DrinkkiDao(Database database) {
        this.database = database;
    }
    
    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return DriverManager.getConnection(dbUrl);
        }

        return DriverManager.getConnection("jdbc:sqlite:drinkkitietokanta.db");
    }

    @Override
    public Drinkki findOne(Integer key) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DrinkkiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Drinkki WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        //Jos jotain vikaa, niin tässä mahdollisesti
        //------------------------------------------
        if (!rs.next()) {
            return null;
        }
        //------------------------------------------

        Drinkki drinkki = new Drinkki(rs.getInt("id"), rs.getString("nimi"));
        stmt.close();
        rs.close();
        
        conn.close();

        return drinkki;
    }

    @Override
    public List<Drinkki> findAll() throws SQLException {
        List<Drinkki> drinkit = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DrinkkiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM Drinkki");

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            drinkit.add(new Drinkki(rs.getInt("id"), rs.getString("nimi")));
        }

        return drinkit;

    }

    @Override
    public Drinkki saveOrUpdate(Drinkki object) throws SQLException {
        if (object.getId() == null) {
            return save(object);
        } else {
            // muulloin päivitetään asiakas
            return update(object);
        }
    }

    
    /**
     *Poistaa drinkin seka Drinkki taulusta että DrinkkiMixeri taulusta kaikki kyseiseen drinkkiin liittyvät rivi
     * @param key
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DrinkkiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Drinkki WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt.close();
        
        stmt = conn.prepareStatement("DELETE FROM DrinkkiMixeri WHERE drinkki_id = ?");
        stmt.setInt(1,key);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private Drinkki save(Drinkki drinkki) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DrinkkiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Drinkki"
                + "(nimi)"
                + "VALUES (?)");
        stmt.setString(1, drinkki.getNimi());

        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM Drinkki WHERE nimi = ?");
        stmt.setString(1, drinkki.getNimi());
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Drinkki d = new Drinkki(rs.getInt("id"), rs.getString("nimi"));
        stmt.close();
        rs.close();
        conn.close();

        return d;
    }

    private Drinkki update(Drinkki drinkki) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Drinkki SET nimi = ?");
        stmt.setString(1, drinkki.getNimi());

        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return drinkki;
    }

    /**
     * Palauttaa DrinkkiOhje olion jolla on nimi = mixeri, maara = mixerin
     * määrä, ja jarjnro = monentenako mixeri lisätään.
     *
     * @param nro
     * @return
     * @throws SQLException
     */
    public List<DrinkkiOhje> findMixers(int id) throws SQLException {
        List<DrinkkiOhje> mixerit = new ArrayList<>();
        Connection conn = database.getConnection();
 

        PreparedStatement stmt = conn.prepareStatement("SELECT Drinkki.nimi AS drinkki,"
                + " Mixeri.nimi AS mixeri,"
                + " DrinkkiMixeri.jarjestysnro AS jarjestysnro,"
                + " DrinkkiMixeri.maara AS maara, DrinkkiMixeri.ohje as ohje"
                + " FROM Drinkki, Mixeri, DrinkkiMixeri"
                + " WHERE Drinkki.id = ?"
                + " AND DrinkkiMixeri.drinkki_id = Drinkki.id"
                + " AND Mixeri.id = DrinkkiMixeri.mixeri_id");
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            mixerit.add(new DrinkkiOhje(rs.getString("drinkki"),
                    rs.getString("mixeri"), rs.getString("maara"), 
                    rs.getInt("jarjestysnro"),rs.getString("ohje")));
        }

        stmt.close();
        rs.close();
        conn.close();

        return mixerit;

    }
    
    public void deleteAll() throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Drinkki;");
        stmt.executeUpdate();
        stmt.close();
        stmt = conn.prepareStatement("DELETE FROM DrinkkiMixeri;");
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
    
    public void addMixeriToDrink(String drinkkiNimi, String mixeriNimi, int jarjestysnro, String maara, String ohje) throws SQLException{
        Connection conn = database.getConnection();
        
        //Hakee drinkin id:m
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Drinkki WHERE nimi = ?");
        stmt.setString(1, drinkkiNimi);
        ResultSet rs = stmt.executeQuery();
        int drinkki_id = rs.getInt("id");
        stmt.close();
        rs.close();
        
        
        //Hakee mixerin id:n
        stmt = conn.prepareStatement("SELECT id FROM Mixeri WHERE nimi = ?");
        stmt.setString(1, mixeriNimi);
        rs = stmt.executeQuery();
        int mixeri_id = rs.getInt("id");
        stmt.close();
        rs.close();
        
        
        
        //Suorittaa halutun kyselyn
        stmt = conn.prepareStatement("INSERT INTO DrinkkiMixeri (drinkki_id, mixeri_id,jarjestysnro,maara,ohje)"
                + " VALUES (?,?,?,?,?)");
        stmt.setInt(1, drinkki_id);
        stmt.setInt(2,mixeri_id);
        stmt.setInt(3,jarjestysnro);
        stmt.setString(4, maara);
        stmt.setString(5, ohje);
        
        
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
    
    
    

}
