package pl.marchuck.ninjaworlds.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public abstract class SearchEngineBase implements SearchEngine {

    protected List<SearchProvider> searchProviders = new ArrayList<>();
    private EditText editText;

    public Observable<CharSequence> getInputEmitter() {
        return Observable.create(new Observable.OnSubscribe<CharSequence>() {
            @Override
            public void call(final Subscriber<? super CharSequence> subscriber) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        subscriber.onNext(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }).debounce(300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void inject(final EditText editText) {
        this.editText = editText;
    }

    @Override
    public SearchEngine addSearchProvider(SearchProvider engine) {
        searchProviders.add(engine);
        return this;
    }
}
