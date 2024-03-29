/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package etu2083.framework.servlet;

import com.google.gson.Gson;
import etu2083.framework.AnnotationGetter;
import etu2083.framework.ConverterExtension;
import etu2083.framework.HttpSessionExtension;
import etu2083.framework.Mapping;
import etu2083.framework.ModelView;
import etu2083.framework.servlet.annotations.Auth;
import etu2083.framework.servlet.annotations.ParamName;
import etu2083.framework.servlet.annotations.RestAPI;
import etu2083.framework.servlet.annotations.Session;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.convert.ConverterException;
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
    Map<Class<?>, Object> autoloads = new HashMap<>();
    
    boolean authSessionInitialized = false;
    
    @Override
    public void init() throws ServletException {
        AnnotationGetter.packages.add("controller");

        // Initializing all of the class routes
        mappingUrls = MappingInitializer.getAllControllerURLMethods();
        
        try {
            // Initializing autoloads
            autoloads = MappingInitializer.getAllAutoloadsClasses();
            
            System.out.println(autoloads);
            System.out.println(mappingUrls);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
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
    }
    
    void InitializeAuthSessions(HttpServletRequest request) {
        // Intilializing is connected
        String isConnectedSessionName = getServletContext().getInitParameter("isConnectedSessionName");
        request.getSession().setAttribute(isConnectedSessionName, false);
        
        // Initializing profile
        String connectedProfileSessionName = getServletContext().getInitParameter("ConnectedProfileSessionName");
        request.getSession().setAttribute(connectedProfileSessionName, null);
    }
    
    public void getParametersFromView(HttpServletRequest request, HttpServletResponse response, Object objectUrlInstance) throws ParseException {
        if (!authSessionInitialized) {
            InitializeAuthSessions(request);
            authSessionInitialized = true;
        }
        
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
                    // Handle non-array field type
                    String paramValue = paramValues.get(0);
                    Object convertedValue = ConverterExtension.ConvertStringToType(fieldType, paramValue);
                    currentObjectField.set(objectUrlInstance, convertedValue);
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
    
    public Object checkAutoloads(Class<?> classType) throws IllegalAccessException {
        if (autoloads.containsKey(classType)) {
            Object containedObject = autoloads.get(classType);
            resetObject(containedObject);
            
            return containedObject;
        }
        
        return null;
    }
    
    public static void resetObject(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); 

            if (field.getType().isPrimitive()) setPrimitiveValue(field, object);
            else field.set(object, null);
        }
    }
    
    private static void setPrimitiveValue(Field field, Object object) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == boolean.class) {
            field.setBoolean(object, false);
        } else if (type == byte.class) {
            field.setByte(object, (byte) 0);
        } else if (type == short.class) {
            field.setShort(object, (short) 0);
        } else if (type == int.class) {
            field.setInt(object, 0);
        } else if (type == long.class) {
            field.setLong(object, 0L);
        } else if (type == float.class) {
            field.setFloat(object, 0.0f);
        } else if (type == double.class) {
            field.setDouble(object, 0.0);
        } else if (type == char.class) {
            field.setChar(object, '\u0000');
        }
    }
    
    private boolean CheckAuthFunction(HttpServletRequest request, Method calledMethod)  {
        String isConnectedSessionName = getServletContext().getInitParameter("isConnectedSessionName");
        String connectedProfileSessionName = getServletContext().getInitParameter("ConnectedProfileSessionName");
        
        System.out.println("Session Auth Name:" + isConnectedSessionName + "| Profile: " + connectedProfileSessionName);
        // Checking if it has an auth annotation
        if (!calledMethod.isAnnotationPresent(Auth.class)) return true;
        System.out.println("Session Auth Name VAlue:" + request.getSession().getAttribute(isConnectedSessionName) + "| Profile Value: " + (String) request.getSession().getAttribute(connectedProfileSessionName));
        
        // Checking if the current client is connected
        if (request.getSession().getAttribute(isConnectedSessionName) == null) return false;
        if (((boolean) request.getSession().getAttribute(isConnectedSessionName)) == false) return false;
        
        Auth authInstance = calledMethod.getAnnotation(Auth.class);
        String profileSessionValue = (String) request.getSession().getAttribute(connectedProfileSessionName);
        
        
        if (!authInstance.profil().equals("")) if (!authInstance.profil().equals(profileSessionValue)) return false;
        
        return true;
    }
    
    private boolean CheckForSessionCall(HttpServletRequest request, Method calledMethod, Object instancedObject) {
        Field[] objectFields = instancedObject.getClass().getDeclaredFields();
        for (int i = 0; i < objectFields.length; i++) {
            String fieldName = objectFields[i].getName();
            
            // Recherche d'un attribut nomme field
            if (fieldName.equals("session")) {
                // Comparer au type qu'il faut
                
                if (objectFields[i].getType() != HashMap.class) return false;
                
                objectFields[i].setAccessible(true);
                try {
                    objectFields[i].set(instancedObject, HttpSessionExtension.AllSessions(request));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return true;
            }
        }
        
        return false;
    } 
    
    // Checking method if a rest API
    public String getJSONFrom(Method method, Object calledObject, Object[] methodArguments) {
        String jsonData = null;
        
        try {
            Object returnedValues = method.invoke(calledObject, methodArguments);
            
            Gson gson = new Gson();
            jsonData = gson.toJson(returnedValues);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return jsonData;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentURL = request.getRequestURI().replace(request.getContextPath(), "");   
//        response.getWriter().println(currentURL);
        
        String currentUrlwArgs = currentURL + "?" + request.getQueryString(); 
        
//        response.getWriter().println(mappingUrls.size());
        // Check si l'url existe dans l'hashmap
        if (mappingUrls.containsKey(currentURL)) {
            try {
                Mapping urlMapObject = mappingUrls.get(currentURL);
                
                // Obtenir la classe par son nom
                Class<?> urlObject = Class.forName(urlMapObject.getClassName());
                
                // Instatiating the url object
                Object urlObjectInstance = checkAutoloads(urlObject);
                if (urlObjectInstance == null) urlObjectInstance = urlObject.getConstructor().newInstance();
                
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
                
                // Check si le client est courament connecte
                if (!CheckAuthFunction(request, currentUrlMappedMethod)) {
                    response.getWriter().println("Access Denied: Not Authentified");
                    return;
                }
            
                // Checking if the session needs session
                if (currentUrlMappedMethod.isAnnotationPresent(Session.class)) {
                    boolean actionResults = CheckForSessionCall(request, currentUrlMappedMethod, urlObjectInstance);
                    
                    if (!actionResults) {
                        response.getWriter().println("Session call initialized incorrectly");
                        return;
                    }
                }
                
                // Checking if the rest API annotation is there
                if (currentUrlMappedMethod.isAnnotationPresent(RestAPI.class)) {
                    String jsonData = getJSONFrom(currentUrlMappedMethod, urlObjectInstance, argsArray);
                    response.getWriter().println(jsonData);
                    
                    // Stopping normal execution
                    return;
                }
                
                // Invoke the method in the class
                ModelView modelView = (ModelView) currentUrlMappedMethod.invoke(urlObjectInstance, argsArray);
               
                if (modelView != null) {
                    // Checking if json
                    if (modelView.isRestAPI()) {
                        String jsonData = modelView.getJsonData();
                        response.getWriter().print(jsonData);
                        
                        // Stop normal processing
                        return;
                    }
                    
                    // Setting attributes
                    if (modelView.hasData()) {
                        for (Map.Entry<String, Object> data : modelView.getData().entrySet()) {
                            request.setAttribute(data.getKey(), data.getValue());
                        }
                    }
                    
                    if (modelView.doesInvalidate()) {
                        request.getSession().invalidate();
                    } else {
                        // Setting sessions
                        if (modelView.hasSessions()) {
                            request.getSession().setMaxInactiveInterval(1800);

                            for (Map.Entry<String,Object> session : modelView.getSession().entrySet()) {
                                System.out.println("Session:" + session.getKey() + "| Value: " + session.getValue());
                                request.getSession().setAttribute(session.getKey(), session.getValue());

                                System.out.println("-------------Session Name VAlue:" + session.getKey() + "| Value: " + request.getSession().getAttribute((String) session.getKey()));
                            }
                        }
                    }
                    
                    // Destroying sessions
                    if (modelView.haveSessionsToRemove()) {
                        List<String> sessionToRemove = modelView.getSessionsToInvalidate();
                        
                        for (String sessionName : sessionToRemove) {
                            request.getSession().removeAttribute(sessionName);
                        }
                    }

                    // Dispatching the results  
                    if (modelView.getView().equals(null) || modelView.getView().equals("")) return; // TODO: Exception
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
