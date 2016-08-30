package pl.marchuck.ninjaworlds.util;

import java.util.List;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class LogUtil {
    public static <T> String printList(List<T> list) {
        if (list == null) {
            return "null";
        }
        if (list.size() == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(list.size() * 6);
        sb.append('[');
        sb.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            sb.append(", ");
            sb.append(list.get(i));
        }
        sb.append(']');
        return sb.toString();
    }
}
