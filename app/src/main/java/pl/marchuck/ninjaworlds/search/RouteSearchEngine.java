package pl.marchuck.ninjaworlds.search;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.models.Place;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public class RouteSearchEngine extends SearchEngineBase {


    private Action1<List<Place>> newResultsCaller;

    public RouteSearchEngine() {
        switch (searchProviders.size()) {
            case 1: {
                getInputEmitter().flatMap(new Func1<CharSequence, Observable<List<Place>>>() {
                    @Override
                    public Observable<List<Place>> call(CharSequence charSequence) {
                        return searchProviders.get(0).getSuggestions(charSequence);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<Place>>() {
                            @Override
                            public void call(List<Place> places) {
                                newResultsCaller.call(places);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                newResultsCaller.call(new ArrayList<Place>());
                            }
                        });
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }

        }
    }

    @Override
    public void onSuggestedAction(Action1<List<Place>> items) {
        this.newResultsCaller = items;
    }
}
