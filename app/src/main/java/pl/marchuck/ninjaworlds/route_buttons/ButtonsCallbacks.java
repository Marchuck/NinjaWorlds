package pl.marchuck.ninjaworlds.route_buttons;

import android.app.Activity;

import pl.marchuck.ninjaworlds.models.Place;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public interface ButtonsCallbacks {
    Activity getActivity();

    void showNavigateButton();

    void setDestinationText(Place place, @Destination int destination);
}
