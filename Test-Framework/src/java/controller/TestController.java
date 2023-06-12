/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import etu2083.framework.servlet.annotations.Controller;
import etu2083.framework.servlet.annotations.AppRoute;
import etu2083.framework.ModelView;
import etu2083.framework.servlet.annotations.ParamName;
import etu2083.framework.servlet.annotations.Scope;

import java.util.Date;

/**
 *
 * @author aris
 */
@Controller
@Scope(type=Scope.ScopeType.SINGLETON)
public class TestController {
    public String myVar;
    public double huhu = 0;
    
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
    public ModelView huhu(@ParamName(name="iii") String iii,@ParamName(name="haha[]") String[] haha) {
        this.huhu++;
        
        ModelView v = new ModelView();
        v.addItem("huhu", this.huhu);
        v.addItem("nom", iii);
        v.addItem("date", date);
        v.addItem("haha", haha);

        v.setView("test");
        
        return v;
    }
}
