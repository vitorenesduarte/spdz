package sdc.avoidingproblems;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.reflect.Type;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class JSONManager {

    private static final Gson gson = new Gson();

    public static String toJSON(Object o) {
        return gson.toJson(o);
    }

    public static String toJSON(Object o, Type type) {
        return gson.toJson(o, type);
    }

    public static void toJSON(Object o, Class<?> clazz, JsonWriter writer) {
        gson.toJson(o, clazz, writer);
    }

    public static <T extends Object> T fromJSON(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T extends Object> T fromJSON(String json, Type type) {
        return gson.fromJson(json, type);
    }
    
    public static <T extends Object> T fromJSON(JsonReader reader, Class<?> clazz){
        return gson.fromJson(reader, clazz);
    }
}
