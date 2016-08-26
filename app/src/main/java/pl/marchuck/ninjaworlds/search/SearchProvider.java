package pl.marchuck.ninjaworlds.search;

import java.util.List;

import pl.marchuck.ninjaworlds.models.Place;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public interface SearchProvider {
    Observable<List<Place>> getSuggestions(CharSequence sequence);
}
