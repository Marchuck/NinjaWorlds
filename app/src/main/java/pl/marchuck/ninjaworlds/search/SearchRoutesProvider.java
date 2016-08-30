package pl.marchuck.ninjaworlds.search;

import android.content.Context;

import java.util.List;

import pl.marchuck.ninjaworlds.App;
import pl.marchuck.ninjaworlds.apis.DataRepository;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class SearchRoutesProvider implements SearchProvider {

    private DataRepository repository;

    public SearchRoutesProvider(Context c) {
        repository = App.getDataRepository(c);
    }

    @Override
    public Observable<List<CharSequence>> getSuggestions(final CharSequence sequence) {
        return repository.getStops().flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> strings) {
                return Observable.from(strings);
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.toLowerCase().startsWith(sequence.toString().toLowerCase());
            }
        }).map(new Func1<String, CharSequence>() {
            @Override
            public CharSequence call(String s) {
                return s;
            }
        }).toList();
    }
}
