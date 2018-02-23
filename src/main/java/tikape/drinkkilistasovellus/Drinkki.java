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
class Drinkki {
    
    private String nimi;
    private Integer id;
    
    public Drinkki(Integer id, String nimi) {
        this.nimi = nimi;
        this.id = id;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNimi() {
        return this.nimi;
    }
    
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
}
