package pl.marchuck.ninjaworlds.search;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.experimantal.TextEmitter;
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

    private TextEmitter emitter;
    private Action1<List<Place>> newResultsCaller;
    private AdditionalListener listener;

    public RouteSearchEngine(TextEmitter emitter) {
        this.emitter = emitter;
    }

    public RouteSearchEngine optionalListner(AdditionalListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public SearchEngine init() {
        switch (searchProviders.size()) {
            case 1: {
                getInputEmitter(emitter).flatMap(new Func1<CharSequence, Observable<List<Place>>>() {
                    @Override
                    public Observable<List<Place>> call(CharSequence charSequence) {
                        listener.onListen(RouteSearchEngine.this);
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
        return this;
    }

    @Override
    public void onSuggestedAction(Action1<List<Place>> items) {
        this.newResultsCaller = items;
    }

    public interface AdditionalListener {
        void onListen(SearchEngine engine);
    }
}
