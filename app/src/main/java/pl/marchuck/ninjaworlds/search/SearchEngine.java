package pl.marchuck.ninjaworlds.search;

import android.widget.EditText;

import java.util.List;

import pl.marchuck.ninjaworlds.models.Place;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public interface SearchEngine {
    void inject(EditText editText);

    SearchEngine addSearchProvider(SearchProvider engine);

    void onSuggestedAction(Action1<List<Place>> items);

}
