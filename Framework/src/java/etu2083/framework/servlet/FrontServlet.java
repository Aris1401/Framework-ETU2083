/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package etu2083.framework.servlet;

import etu2083.framework.AnnotationGetter;
import etu2083.framework.Mapping;
import etu2083.framework.ModelView;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aris
 */
public class FrontServlet extends HttpServlet {
    Map<String, Mapping> mappingUrls = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        AnnotationGetter.packages.add("controller");

        // Initializing all of the class routes
        mappingUrls = MappingInitializer.getAllControllerURLMethods();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String currentURL = request.getRequestURI().replace(request.getContextPath(), "");   
        response.getWriter().println(currentURL);
        
        String currentUrlwArgs = currentURL + "?" + request.getQueryString(); 
        
        response.getWriter().println(mappingUrls.size());
        // Check si l'url existe dans l'hashmap
        if (mappingUrls.containsKey(currentURL)) {
            try {
                Mapping urlMapObject = mappingUrls.get(currentURL);
                
                // Obtenir la classe par son nom
                Class<?> urlObject = Class.forName(urlMapObject.getClassName());
                
                // Instatiating the url object
                Object urlObjectInstance = urlObject.getConstructor().newInstance();
                
                // Invoke the method in the class
                ModelView modelView = (ModelView) urlObjectInstance.getClass().getMethod(urlMapObject.getMethod()).invoke(urlObjectInstance);
                
                if (modelView.hasData()) {
                    for (Map.Entry<String, Object> data : modelView.getData().entrySet()) {
                        request.setAttribute(data.getKey(), data.getValue());
                    }
                }
                
                // Dispatching the results  
                request.getRequestDispatcher(modelView.getView()).forward(request, response);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (currentURL.contains(".jsp")){
            response.getWriter().println("Access Denied");
            return;
        } else {
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // </editor-fold>

}
