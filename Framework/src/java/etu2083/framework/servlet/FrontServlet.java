/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package etu2083.framework.servlet;

import etu2083.framework.AnnotationGetter;
import etu2083.framework.ConverterExtension;
import etu2083.framework.FileUpload;
import etu2083.framework.Mapping;
import etu2083.framework.ModelView;
import etu2083.framework.servlet.annotations.ParamName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author aris
 */
@MultipartConfig()
public class FrontServlet extends HttpServlet {
    Map<String, Mapping> mappingUrls = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        AnnotationGetter.packages.add("controller");

        // Initializing all of the class routes
        mappingUrls = MappingInitializer.getAllControllerURLMethods();
    }
    
    public void getParametersFromView(HttpServletRequest request, HttpServletResponse response, Object objectUrlInstance) throws ParseException {
        // Getting the current parameters values
        Map<String, String[]> currentUrlParamaters = request.getParameterMap();
        
        for (Map.Entry<String, String[]> urlParams : currentUrlParamaters.entrySet()) {
            try {
                // Checking if the object has the current parameter
                Field currentObjectField = objectUrlInstance.getClass().getField(urlParams.getKey().trim());
                currentObjectField.setAccessible(true);

                ArrayList<String> paramValues = new ArrayList<>();
                for (String urlParamValue : urlParams.getValue()) {
                    // Getting the values of the parameters as an object
                    paramValues.add(urlParamValue.trim());
                }

                Class<?> fieldType = currentObjectField.getType();
                if (fieldType.isArray()) {
                    // Handle array field type
                    Class<?> componentType = fieldType.getComponentType();
                    Object arrayParam = Array.newInstance(componentType, paramValues.size());

                    for (int i = 0; i < paramValues.size(); i++) {
                        Object convertedValue = ConverterExtension.ConvertStringToType(componentType, paramValues.get(i));
                        Array.set(arrayParam, i, convertedValue);
                    }

                    currentObjectField.set(objectUrlInstance, arrayParam);
                } else {
                    if (fieldType == FileUpload.class) {
                        Part part = request.getPart(currentObjectField.getName());
                        
                        FileUpload f = new FileUpload();
                        f.setName(part.getSubmittedFileName());
                        f.setFileBytes(getBytesFromInputStream(part.getInputStream()));
                        
                        currentObjectField.set(objectUrlInstance, f);
                    } else {
                        // Handle non-array field type
                        String paramValue = paramValues.get(0);
                        Object convertedValue = ConverterExtension.ConvertStringToType(fieldType, paramValue);
                        currentObjectField.set(objectUrlInstance, convertedValue);
                    }
                    
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
            } catch (IOException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServletException ex) {
                Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }
    
    Object[] getParametersForMethodFromView(HttpServletRequest request, HttpServletResponse res, Method currentUrlMethod) throws ParseException {
        // Getting the current parameters values
        Map<String, String[]> currentUrlParamaters = request.getParameterMap();
        
        // Getting the parameters
        Parameter[] parameters = currentUrlMethod.getParameters();
        Object[] argsArray = new Object[parameters.length];
        
        int parameterIndex = 0;
        for (Parameter param : parameters) {
            ParamName paramName = param.getAnnotation(ParamName.class);
            Object currentParameter = null;

            if (paramName != null) {
                String paramNameString = paramName.name();
                String[] paramValues = currentUrlParamaters.get(paramNameString);

                if (paramValues != null && paramValues.length > 0) {
                    Class<?> paramType = param.getType();

                    if (paramType.isArray()) {
                        // Handle array parameter type
                        Class<?> componentType = paramType.getComponentType();
                        Object arrayParam = Array.newInstance(componentType, paramValues.length);

                        for (int i = 0; i < paramValues.length; i++) {
                            Object convertedValue = ConverterExtension.ConvertStringToType(componentType, paramValues[i]);
                            Array.set(arrayParam, i, convertedValue);
                        }

                        currentParameter = arrayParam;
                    } else {
                        // Handle non-array parameter type
                        String paramValue = paramValues[0];
                        currentParameter = ConverterExtension.ConvertStringToType(paramType, paramValue);
                    }
                }
            }

            // Setting the args array
            argsArray[parameterIndex] = currentParameter;
            parameterIndex++;
        }
        
        System.out.println(Arrays.toString(argsArray));
        
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
                    if (method.getName().equals(urlMapObject.getMethod())) {
                        currentUrlMappedMethod = method;
                        break;
                    }
                }

                if (currentUrlMappedMethod == null) return; // TODO: Changer en exception
                
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
                } else {
                    response.getWriter().println("An Error Occured");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } 
        } else if (currentURL.contains(".jsp")){
            response.getWriter().println("Access Denied");
            return;
        } else {
            response.getWriter().println("URL Not Found");
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
