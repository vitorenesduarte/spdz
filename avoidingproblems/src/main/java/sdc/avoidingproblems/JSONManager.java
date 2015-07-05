package sdc.avoidingproblems;

import com.google.gson.Gson;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class JSONManager {

    private static final Gson gson = new Gson();

    public static String toJSON(Object o) {
        return gson.toJson(o);
    }

    public static <T extends Object> T fromJSON(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
