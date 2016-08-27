package pl.marchuck.ninjaworlds.util;

import android.support.annotation.Nullable;

/**
 * @author Lukasz Marczak
 * @since 25.08.16.
 */
public class Is {

    public static boolean nonEmpty(@Nullable Object o) {
        if (o instanceof String) return !((String) o).isEmpty();
        return o != null;
    }
}
