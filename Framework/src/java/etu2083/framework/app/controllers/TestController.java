/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework.app.controllers;

import etu2083.framework.ModelView;
import etu2083.framework.servlet.annotations.AppRoute;
import etu2083.framework.servlet.annotations.Controller;

@Controller
public class TestController{
    @AppRoute(url="/test")
    public ModelView index() {
        ModelView md = new ModelView();
        md.setView("huhu");
        
        return md;
    }
}
