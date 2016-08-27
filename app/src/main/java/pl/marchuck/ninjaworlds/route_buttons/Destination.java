package pl.marchuck.ninjaworlds.route_buttons;

import android.support.annotation.IntDef;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
@IntDef({Destination.FROM, Destination.TO})
public @interface Destination {
    int FROM = 0;
    int TO = 1;
}
