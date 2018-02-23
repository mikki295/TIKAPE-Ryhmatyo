/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.drinkkilistasovellus;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

/**
 * zz
 *
 * @author mikaelde
 */
public class Main {

    public static void main(String[] args) throws Exception {
        
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        //Luo yhteyden tietokantaan
        File tiedosto = new File("db", "drinkkitietokanta.db");
        Database db = new Database("jdbc:sqlite:" + tiedosto.getAbsolutePath());
        MixeriDao mdao = new MixeriDao(db);
        DrinkkiDao ddao = new DrinkkiDao(db);

        //Etusivu
        Spark.get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", ddao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        

        //Drinkin ohje
        Spark.get("/drinkki/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("mixerit", ddao.findMixers(Integer.parseInt(req.params(":id"))));
            return new ModelAndView(map, "ohje");
        }, new ThymeleafTemplateEngine());
        
        
        //Nayta mixerit
        Spark.get("/ainekset", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("mixerit", mdao.findAll());
            
            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());
        
        
        //Tallenna mixeri
        Spark.post("/savemixer", (req, res) -> {
            mdao.saveOrUpdate(new Mixeri(null, req.queryParams("aine")));
            res.redirect("/ainekset");
            return "";
        });

        //Posta mixeri
        Spark.post("/deletemixer/:id", (req, res) -> {
            mdao.delete(Integer.parseInt(req.params("id")));
            res.redirect("/ainekset");
            return "";
        });
        
        //Poista drinkki
        Spark.post("deletedrink/:id", (req, res) -> {
            ddao.delete(Integer.parseInt(req.params("id")));
            res.redirect("/luodrinkki");
            return "";
        });
        
        //Luo drinkki
        Spark.get("/luodrinkki", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("drinkit", ddao.findAll());
            map.put("mixerit", mdao.findAll());
            //map.put("mixerit", mixerit);
            
            
            return new ModelAndView(map, "luodrinkki");
        }, new ThymeleafTemplateEngine());
        
        //Tallenna drinkki
        Spark.post("/savedrink", (req, res) -> {
            ddao.saveOrUpdate(new Drinkki(null, req.queryParams("drinkki")));
            res.redirect("/luodrinkki");
            return "";
        });
        
        //Lisää drinkkiin mixeri tms
        Spark.post("/drinkinluonti", (req, res) -> {
            ddao.addMixeriToDrink(req.queryParams("Drinkki"), req.queryParams("Mixeri"), 0, req.queryParams("maara"), req.queryParams("ohje"));
            res.redirect("/luodrinkki");
            return "";
        });
        
    }

}
