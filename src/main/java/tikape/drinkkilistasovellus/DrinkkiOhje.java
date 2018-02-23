/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.drinkkilistasovellus;

/**
 *
 * @author mikaelde
 */
public class DrinkkiOhje {
    
    private String mixeri;
    private String maara;
    private int jarjestysnro;
    private String drinkki;
    private String ohje;
    
    public DrinkkiOhje(String drinkki, String mixeri, String maara, int jarjestysnro, String ohje) {
        this.drinkki = drinkki;
        this.mixeri = mixeri;
        this.maara = maara;
        this.jarjestysnro = jarjestysnro;
        this.ohje = ohje;
    }
    
    public String getMixeri() {
        return this.mixeri;
    }
    
    public String getDrinkki() {
        return this.drinkki;
    }
    
    public String getMaara() {
        return this.maara;
    }
    
    public int getJarjestysnro() {
        return this.jarjestysnro;
    }
    
    public String getOhje() {
        return this.ohje;
    }
    
    
    
}
