package pl.marchuck.ninjaworlds.search;

import java.util.List;

import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public interface SearchProvider {
    Observable<List<CharSequence>> getSuggestions(CharSequence sequence);
}
