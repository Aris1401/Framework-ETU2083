/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aris
 */
public class ModelView {
    private String view;
    private Map<String, Object> data;

    public String getView() {
        return view + ".jsp";
    }

    public void setView(String view) {
        this.view = view;
    }
    
    public <T> void addItem(String key, T value) {
        if (data == null) data = new HashMap<>();
        
        data.put(key, value);
    }
    
    public boolean  hasData() {
        return data != null;
    }
    
    public Map<String, Object> getData() {
        return this.data;
    }
}
