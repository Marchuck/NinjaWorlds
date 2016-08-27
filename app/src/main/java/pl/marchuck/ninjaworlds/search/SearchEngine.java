package pl.marchuck.ninjaworlds.search;

import java.util.List;

import pl.marchuck.ninjaworlds.experimantal.TextEmitter;
import pl.marchuck.ninjaworlds.models.Place;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public interface SearchEngine {
    Observable<CharSequence> getInputEmitter(TextEmitter emitter);

    SearchEngine addSearchProvider(SearchProvider engine);

    void onSuggestedAction(Action1<List<Place>> items);

    SearchEngine init();
}
