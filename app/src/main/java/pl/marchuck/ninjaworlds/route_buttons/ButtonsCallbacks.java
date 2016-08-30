package pl.marchuck.ninjaworlds.route_buttons;

import android.app.Activity;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public interface ButtonsCallbacks {
    Activity getActivity();

    void showNavigateButton();

    void setDestinationText(CharSequence place, @Destination int destination);
}
