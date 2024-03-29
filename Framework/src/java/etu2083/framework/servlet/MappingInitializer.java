 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework.servlet;

import etu2083.framework.Mapping;
import etu2083.framework.servlet.annotations.Controller;
import etu2083.framework.AnnotationGetter;
import etu2083.framework.servlet.annotations.AppRoute;
import etu2083.framework.servlet.annotations.Scope;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

/**
 *
 * @author aris
 */
public class MappingInitializer {
    public static Map<String, Mapping> getAllControllerURLMethods() {
        List<Class<?>> controllers = AnnotationGetter.getClassesWithAnnotation(Controller.class);
        
        // Creating Hash Map containing the url to the controller method and the mapping class
        Map<String, Mapping> urlMapping = new HashMap<>();
        
        // Looping through all the classes annotated with the annotation controller
        for (Class<?> controller : controllers) {
            // Getting all the methods for each controller
            Method[] controllerMethods = controller.getMethods();
            
            for (Method method : controllerMethods) {
                // Creating and putting a Mapping and key in the urlMapping has map
                if (method.isAnnotationPresent(AppRoute.class)) {
                    Mapping mapping = new Mapping();
                    
                    mapping.setClassName(controller.getName());
                    mapping.setMethod(method.getName());
                    
                    // Getting the url to the app route
                    AppRoute appRoute = method.getAnnotation(AppRoute.class);
                    String url = appRoute.url();
                    
                    // Putting the mapping class in the dictionnary
                    urlMapping.put(url, mapping);
                }
            }
        }
         
        return urlMapping;
    }
    
    public static Map<Class<?>, Object> getAllAutoloadsClasses() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException{
        List<Class<?>> controllers = AnnotationGetter.getClassesWithAnnotation(Controller.class);
        
        // Creating Hash Map containing the url to the controller method and the mapping class
        Map<Class<?>, Object> autoloads = new HashMap<>();
        
        // Looping through all the classes annotated with the annotation controller
        for (Class<?> controller : controllers) {
            if (controller.isAnnotationPresent(Scope.class)) {
                Scope scope = controller.getAnnotation(Scope.class);
                if (scope.type() != Scope.ScopeType.SINGLETON) continue;
                
                // Si le scope annotation est present 
                Class<?> classTypeInstance = Class.forName(controller.getName());
                Object classInstance = classTypeInstance.getConstructor().newInstance();
                
                autoloads.put(classTypeInstance, classInstance);
            }
        }
         
        return autoloads;
    }
}
