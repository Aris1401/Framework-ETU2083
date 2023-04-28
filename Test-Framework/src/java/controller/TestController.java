/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import etu2083.framework.servlet.annotations.Controller;
import etu2083.framework.servlet.annotations.AppRoute;
import etu2083.framework.ModelView;
import java.util.Date;

/**
 *
 * @author aris
 */
@Controller
public class TestController {
    public String myVar;
    public double huhu;
    
    public String nom;
    public Date date;
    
    @AppRoute(url="/test")
    public ModelView test() {
        ModelView v = new ModelView();
        v.addItem("huhu", "Ito ny data ato izao");
        v.setView("test");
        
        return v;
    }
    
    @AppRoute(url="/huhu") 
    public ModelView huhu() {
        ModelView v = new ModelView();
        v.addItem("huhu", this.huhu);
        v.addItem("nom", nom);
        v.addItem("date", date);
        v.setView("test");
        
        return v;
    }
}
