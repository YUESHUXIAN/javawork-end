package community.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class JsonUtil {
    // 对象转JSON字符串
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }
    // JSON字符串转对象
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
    // List转JSONArray
    public static JSONArray toJsonArray(List<?> list) {
        return (JSONArray) JSONArray.toJSON(list);
    }
}