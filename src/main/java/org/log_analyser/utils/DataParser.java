package org.log_analyser.utils;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;


@Log
/**
 * author: abhirj87
 * This class converts the given data to POJO
 * If we want to change the data type in future then only the bean needs to be changed.
 * Rest of the program need not be touched.
 */
public class DataParser {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY");
    private String className = "DataParser";
    private HashMap<String, ArrayList<Field>> allFieldsMapping = new HashMap<>();
    /**
     * Methods object creation takes a lot of time.
     * Hence re using the objects instead of creting it every tim
     */
    private HashMap<String, Method> allMethodsMapping = new HashMap<>();

    private HashMap<String, String> getters = new HashMap<>();
    private HashMap<String, String> setters = new HashMap<>();


    public <T> boolean parse(String[] parts, T entityObject) {
        sdf.setLenient(false);
        int i = 0;
        Class<T> cl = (Class<T>) entityObject.getClass();
        for (Field field : getFields(cl)) {
            Class fieldType = field.getType();
            field.setAccessible(true);
            try {
                getMethod(cl, field).invoke(entityObject, getData(parts[i++], fieldType));
            } catch (Exception e) {
                log.log(Level.WARNING, className + " method ==> parse " + "exception while parsing field: " + field.getName() + "\n **Message: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public <T> ArrayList<Field> getFields(Class<T> cl) {
        ArrayList<Field> allFields;
        allFields = allFieldsMapping.get(cl.getSimpleName() + "_AllFields");
        if (allFields == null) {
            allFields = new ArrayList<Field>();
            allFields.addAll(Arrays.asList(cl.getDeclaredFields()));
            allFieldsMapping.put(cl.getSimpleName() + "_AllFields", allFields);
        }
        return allFields;
    }


    @SneakyThrows
    public <T> Method getMethod(Class<T> cl, Field field) {
        Method m = allMethodsMapping.get(field.getName());
        if (m == null) {
            m = cl.getMethod(getSetter(field), field.getType());
        }
        return m;
    }

    private <T> T getData(String input, Class<T> cl) {

        try {
            Object ob = null;
            if (input.contentEquals("")) return null;
            if (cl.getSimpleName().contentEquals("Integer") || cl.getSimpleName().contentEquals("int"))
                ob = Utils.getUtils().parseInt(input);
            if (cl.getSimpleName().contentEquals("Long") || cl.getSimpleName().contentEquals("long"))
                ob = Utils.getUtils().parseLong(input);
            if (cl.getSimpleName().contentEquals("Double") || cl.getSimpleName().contentEquals("double"))
                ob = Utils.getUtils().parseDouble(input);
            if (cl.getSimpleName().contentEquals("Short") || cl.getSimpleName().contentEquals("short"))
                ob = Utils.getUtils().parseShort(input);
            if (cl.getSimpleName().contentEquals("Boolean") || cl.getSimpleName().contentEquals("boolean"))
                ob = Boolean.parseBoolean(input);
            if (cl.getSimpleName().contentEquals("String"))
                ob = new String(input); //String null case will handle automatically
            if (cl.getSimpleName().contentEquals("Date")) {
                ob = new java.sql.Date(sdf.parse(input).getTime());
            }
            if (ob != null) return (T) ob;
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "Number format exception" + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            log.log(Level.WARNING, "Error parsing date field: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public String getSetter(Field field) {
        String name = field.getName();
        String set = setters.get(name);
        if (set != null) return set;
        set = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        setters.put(name, set);
        return set;
    }


    public String getGetter(Field field) {
        String name = field.getName();
        String get = getters.get(name);
        String type = field.getType().toString();
        if (get != null) return get;

        if (type.contains("boolean") || type.contains("Boolean")) {
            get = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            get = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        getters.put(name, get);
        return get;

    }

}
