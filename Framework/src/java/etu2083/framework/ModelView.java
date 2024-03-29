/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aris
 */
public class ModelView {
    private String view;
    private Map<String, Object> data;
    private Map<String, Object> session;
    
    // Json propreties
    private boolean isJson = false;
    
    // More session propreties
    private boolean invalidate = false;
    private List<String> sessionsToRemove;

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
    
    public <T> void addSession(String name, T value) {
        if (session == null) session = new HashMap<>();
        session.put(name, value);
    }
    
    public boolean hasSessions() {
        return session != null;
    }
    
    public Map<String, Object> getSession() {
        return this.session;
    }
    
    // Rest API propreties
    public boolean isRestAPI() {
        return this.isJson;
    }
    
    public boolean toggleIsJson() {
        this.isJson = !isJson;
        
        return this.isJson;
    }
    
    public void setIsJson(boolean value) {
        this.isJson = value;
    }
    
    public String getJsonData() {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
    
    // More session methods
    public boolean doesInvalidate() {
        return this.invalidate;
    }
    
    public void toggleInvalidate() {
        this.invalidate = !this.invalidate;
    }
    
    public void setInvalidate(boolean value) {
        this.invalidate = value;
    }
    
    public boolean haveSessionsToRemove() {
        if (this.sessionsToRemove == null) return false;
        
        if (this.sessionsToRemove.size() > 0) return true;
        
        return false;
    }
    
    public List<String> getSessionsToInvalidate() {
        return this.sessionsToRemove;
    }
    
    public void removeSession(String sessionName) {
        if (this.sessionsToRemove == null) this.sessionsToRemove = new ArrayList<>();
        this.sessionsToRemove.add(sessionName);
    }
}
