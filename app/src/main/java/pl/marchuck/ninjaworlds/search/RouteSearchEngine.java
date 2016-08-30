package pl.marchuck.ninjaworlds.search;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.experimantal.TextEmitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public class RouteSearchEngine extends SearchEngineBase {

    private TextEmitter emitter;
    private Action1<List<CharSequence>> newResultsCaller;
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
                getInputEmitter(emitter).flatMap(new Func1<CharSequence, Observable<List<CharSequence>>>() {
                    @Override
                    public Observable<List<CharSequence>> call(CharSequence charSequence) {
                        listener.onUpdate(RouteSearchEngine.this);
                        return searchProviders.get(0).getSuggestions(charSequence);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<CharSequence>>() {
                            @Override
                            public void call(List<CharSequence> places) {
                                newResultsCaller.call(places);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                newResultsCaller.call(new ArrayList<CharSequence>());
                            }
                        });
                break;
            }
            case 2: {
                getInputEmitter(emitter).flatMap(new Func1<CharSequence, Observable<List<CharSequence>>>() {
                    @Override
                    public Observable<List<CharSequence>> call(CharSequence sequence) {
                        return Observable.zip(searchProviders.get(0).getSuggestions(sequence)
                                , searchProviders.get(1).getSuggestions(sequence),
                                new Func2<List<CharSequence>, List<CharSequence>, List<CharSequence>>() {
                                    @Override
                                    public List<CharSequence> call(List<CharSequence> places,
                                                                   List<CharSequence> places2) {
                                        places.addAll(places2);
                                        return places;
                                    }
                                });
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<CharSequence>>() {
                            @Override
                            public void call(List<CharSequence> places) {
                                newResultsCaller.call(places);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                newResultsCaller.call(new ArrayList<CharSequence>());
                            }
                        });
                break;
            }
            case 3: {
                throw new UnsupportedOperationException("Not implemented!");
//                break;
            }
            case 4: {
                throw new UnsupportedOperationException("Not implemented!");
//                break;
            }
        }
        return this;
    }

    @Override
    public void onSuggestedAction(Action1<List<CharSequence>> items) {
        this.newResultsCaller = items;
    }

    public interface AdditionalListener {
        void onUpdate(SearchEngine engine);
    }
}
