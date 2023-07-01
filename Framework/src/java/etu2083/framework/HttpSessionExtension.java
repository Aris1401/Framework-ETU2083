/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework;

import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aris
 */
public class HttpSessionExtension {
    public static HashMap<String, Object> AllSessions(HttpServletRequest request) {
        Enumeration<String> sessionNames = request.getSession().getAttributeNames();
        
        // Returned value
        HashMap<String, Object> sessions = new HashMap<>();
        while (sessionNames.hasMoreElements()) {
            String sessionName = sessionNames.nextElement();
            
            sessions.put(sessionName, request.getSession(false).getAttribute(sessionName));
        }
        
        return sessions;
    }
}
