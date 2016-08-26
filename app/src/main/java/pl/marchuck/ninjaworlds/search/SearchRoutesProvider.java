package pl.marchuck.ninjaworlds.search;

import java.util.List;
import java.util.concurrent.Callable;

import pl.marchuck.ninjaworlds.models.Place;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class SearchRoutesProvider implements SearchProvider {
    @Override
    public Observable<List<Place>> getSuggestions(CharSequence sequence) {
        return Observable.fromCallable(new Callable<List<Place>>() {
            @Override
            public List<Place> call() throws Exception {

                return null;
            }
        });
    }
}
