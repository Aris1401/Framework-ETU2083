/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package etu2083.framework;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author aris
 */
public class ConverterExtension {
    public static Object ConvertStringToType(Class<?> objectType, String value) throws ParseException {
        if (objectType.equals(String.class)) {
            return value;
        } else if (objectType.equals(Integer.class) || objectType.equals(int.class)) {
            return Integer.valueOf(value);
        } else if (objectType.equals(Double.class) || objectType.equals(double.class)) {
            return Double.valueOf(value);
        } else if (objectType.equals(Date.class)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(value);
        } else if (objectType.equals(java.sql.Date.class)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date date = java.sql.Date.valueOf(df.parse(value).toString());
            return date;
        } else {
            throw new IllegalArgumentException("Unsupported parameter type: " + objectType.getName());
        }
    }
    
    public static Object[] ConvertStringArrayToType(Class<?> objectType, String[] values) throws ParseException {
        Object[] convertedValues = new Object[values.length];
        
        if (!objectType.isArray()) return null;
        objectType = objectType.getComponentType();
        
        int index = 0;
        for (String value : values) {
            convertedValues[index] = ConverterExtension.ConvertStringToType(objectType, value);
            index++;
        }
        
        return convertedValues;
    }
}
