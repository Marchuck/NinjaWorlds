package pl.marchuck.ninjaworlds.apis.places;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class PoiSequence extends CharacterStyle implements CharSequence {

    public CharSequence primaryText;
    public CharSequence secondaryText;
    public String placeId;
    public List<Integer> placeTypes = new ArrayList<>();

    public PoiSequence(AutocompletePrediction prediction) {
        primaryText = prediction.getPrimaryText(this);
        secondaryText = prediction.getSecondaryText(this);
        placeId = prediction.getPlaceId();
        if (prediction.getPlaceTypes() != null)
            placeTypes.addAll(prediction.getPlaceTypes());
    }

    @Override
    public int length() {
        return primaryText == null ? 0 : primaryText.length();
    }

    @Override
    public char charAt(int index) {
        return primaryText == null ? '\0' : primaryText.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return primaryText == null ? "" : primaryText.subSequence(start, end);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(Color.BLACK);
    }
}
