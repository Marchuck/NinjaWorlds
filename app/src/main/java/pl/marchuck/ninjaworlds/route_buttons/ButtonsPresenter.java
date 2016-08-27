package pl.marchuck.ninjaworlds.route_buttons;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;

import pl.marchuck.ninjaworlds.models.Place;
import pl.marchuck.ninjaworlds.ui.SelectRouteDialog;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class ButtonsPresenter implements ButtonsCallbacks {

    private ButtonsCallbacks callbacks;
    private boolean isFromChosen;
    private boolean isToChosen;

    public ButtonsPresenter(ButtonsCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public Activity getActivity() {
        return callbacks.getActivity();
    }

    @Override
    public void showNavigateButton() {
        callbacks.showNavigateButton();
    }

    @Override
    public void setDestinationText(Place place, @Destination int destination) {
        callbacks.setDestinationText(place, destination);
    }

    public void showDialog(final TextView toText, @Destination final int destination) {
        Dialog dialog = new SelectRouteDialog(getActivity(), true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                }).withSelectionListener(new SelectRouteDialog.SelectionListener() {
            @Override
            public void onRouteSelected(Place route) {
                if (destination == Destination.FROM) {
                    isFromChosen = true;
                }
                if (destination == Destination.TO) {
                    isToChosen = true;
                }
                setDestinationText(route, destination);
                if (chosenBoth()) {
                    showNavigateButton();
                }
            }
        }).withTitle(toText.getTag().toString()).build();
        dialog.show();
    }

    private boolean chosenBoth() {
        return isFromChosen && isToChosen;
    }
}
