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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    
    private Object convertToParamType(Class<?> paramType, String paramValue) throws ParseException {
        if (paramType.equals(String.class)) {
            return paramValue;
        } else if (paramType.equals(Integer.class) || paramType.equals(int.class)) {
            return Integer.parseInt(paramValue);
        } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
            return Double.parseDouble(paramValue);
        } else if (paramType.equals(Date.class)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(paramValue);
        } else {
            throw new IllegalArgumentException("Unsupported parameter type: " + paramType.getName());
        }
    }
    
    public void getParametersFromView(HttpServletRequest request, HttpServletResponse response, Object objectUrlInstance) throws ParseException {
        // Getting the current parameters values
        Map<String, String[]> currentUrlParamaters = request.getParameterMap();
        
        for (Map.Entry<String, String[]> urlParams : currentUrlParamaters.entrySet()) {
            try {
                // Checking if the object has the current parameters
                Field currentObjectField = objectUrlInstance.getClass().getField(urlParams.getKey().trim());
                currentObjectField.setAccessible(true);
                objectUrlInstance.getClass().getField(urlParams.getKey().trim()).setAccessible(true);
                
                ArrayList<String> paramsValue = new ArrayList<>();
                for (String urlParamValue : urlParams.getValue()) {
                    // Getting the values of the parameters as an object
                    paramsValue.add((String) urlParamValue.trim());
                }
                
                // If the value was an array or was a unique value 
                if (paramsValue.size() > 1) {
                    currentObjectField.set(objectUrlInstance, paramsValue.get(0));
                } else {
                    currentObjectField.set(objectUrlInstance, convertToParamType(currentObjectField.getType(), paramsValue.get(0)));
                }
            } catch (NoSuchFieldException ex) {
                // Skip ahead
                continue;
            } catch (SecurityException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    Object[] getParametersForMethodFromView(HttpServletRequest request, HttpServletResponse res, Method currentUrlMethod) throws ParseException {
        // Getting the current parameters values
        Map<String, String[]> currentUrlParamaters = request.getParameterMap();
        
        // Getting the parameters
        Parameter[] parameters = currentUrlMethod.getParameters();
        Object[] argsArray = new Object[parameters.length];
        
        int parameterIndex = 0;
        for (Parameter param : parameters) {
            Object currentParamater = null;
            
            // Checking for all args
            for (Map.Entry<String, String[]> urlParams : currentUrlParamaters.entrySet()) {
                if (!param.getName().equals(urlParams.getKey())) {
                    // Si un parametres n'est pas present
                    break;
                }
                
                // Si le parametre est trouvee
                currentParamater = convertToParamType(param.getType(), urlParams.getValue()[0]);
            }
            
            // Setting the args array
            argsArray[parameterIndex] = currentParamater;
            parameterIndex++;
        }
        
        return argsArray;
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
                
                
                // Getting parameters for the object
                getParametersFromView(request, response, urlObjectInstance);
                
                
                Method currentUrlMappedMethod = null;
                
                Method[] methods = urlObjectInstance.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(urlMapObject.getMethod())) currentUrlMappedMethod = method;
                }

                if (currentUrlMappedMethod == null) return; // TODO: Changer en eception
                
                // Setting the parameters argumets
                Object[] argsArray = getParametersForMethodFromView(request, response, currentUrlMappedMethod);
                
                // Invoke the method in the class
                ModelView modelView = (ModelView) currentUrlMappedMethod.invoke(urlObjectInstance, argsArray);
               
                if (modelView != null) {
                    if (modelView.hasData()) {
                        for (Map.Entry<String, Object> data : modelView.getData().entrySet()) {
                            request.setAttribute(data.getKey(), data.getValue());
                        }
                    }

                    // Dispatching the results  
                    request.getRequestDispatcher(modelView.getView()).forward(request, response);
                } 
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
            } catch (ParseException ex) {
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
