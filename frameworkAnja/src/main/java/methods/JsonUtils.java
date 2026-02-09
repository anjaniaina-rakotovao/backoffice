package methods;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return quote((String) obj);
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) return mapToJson((Map<?, ?>) obj);
        if (obj instanceof Collection) return collectionToJson((Collection<?>) obj);
        if (obj.getClass().isArray()) return arrayToJson(obj);

        return pojoToJson(obj);
    }

    private static String quote(String s) {
        if (s == null) return "null";
        return '"' + s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r") + '"';
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> e = it.next();
            sb.append(quote(String.valueOf(e.getKey()))).append(':').append(toJson(e.getValue()));
            if (it.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }

    private static String collectionToJson(Collection<?> coll) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(toJson(it.next()));
            if (it.hasNext()) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String arrayToJson(Object array) {
        StringBuilder sb = new StringBuilder();
        int len = Array.getLength(array);
        sb.append('[');
        for (int i = 0; i < len; i++) {
            sb.append(toJson(Array.get(array, i)));
            if (i < len - 1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String pojoToJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                Object val = f.get(obj);
                if (!first) sb.append(',');
                sb.append(quote(f.getName())).append(':').append(toJson(val));
                first = false;
            } catch (Exception ignored) {}
        }
        sb.append('}');
        return sb.toString();
    }
}
