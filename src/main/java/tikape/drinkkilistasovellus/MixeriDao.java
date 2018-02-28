/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.drinkkilistasovellus;

import java.sql.Connection;
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
public class MixeriDao implements Dao<Mixeri, Integer>{
    
    private Database database;
    
    public MixeriDao(Database database) {
        this.database = database;
    }

    @Override
    public Mixeri findOne(Integer key) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MixeriDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Mixeri WHERE id = ?");
        stmt.setInt(1, key);
        
        ResultSet rs = stmt.executeQuery();
        //Jos jotain vikaa, niin tässä mahdollisesti
        //------------------------------------------
        if (!rs.next()) {
            return null;
        }
        //------------------------------------------

        Mixeri mixeri = new Mixeri(rs.getInt("id"), rs.getString("nimi"));
        stmt.close();
        rs.close();

        return mixeri;
    }

    @Override
    public List<Mixeri> findAll() throws SQLException {
        List<Mixeri> mixerit = new ArrayList<>();
        Connection conn = null;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MixeriDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM Mixeri");

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            mixerit.add(new Mixeri(rs.getInt("id"), rs.getString("nimi")));
        }

        return mixerit;
    }

    @Override
    public Mixeri saveOrUpdate(Mixeri object) throws SQLException {
        if (object.getId() == null) {
            return save(object);
        } else {
            // muulloin päivitetään asiakas
            return update(object);
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MixeriDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM DrinkkiMixeri "
                + "WHERE mixeri_id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        stmt.close();
        
        
        stmt = conn.prepareStatement("DELETE FROM Mixeri WHERE id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    private Mixeri update(Mixeri mixeri) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MixeriDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("UPDATE Drinkki SET nimi = ?");
        stmt.setString(1, mixeri.getNimi());

        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return mixeri;
    }

    private Mixeri save(Mixeri mixeri) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MixeriDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Mixeri"
                + "(nimi)"
                + "VALUES (?)");
        stmt.setString(1, mixeri.getNimi());

        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM Mixeri WHERE nimi = ?");
        stmt.setString(1, mixeri.getNimi());
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Mixeri d = new Mixeri(rs.getInt("id"), rs.getString("nimi"));
        stmt.close();
        rs.close();
        conn.close();

        return d;
    }
    
}
