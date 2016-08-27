package pl.marchuck.ninjaworlds.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.marchuck.ninjaworlds.experimantal.TextEmitter;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public abstract class SearchEngineBase implements SearchEngine {

    protected List<SearchProvider> searchProviders = new ArrayList<>();

    @Override
    public Observable<CharSequence> getInputEmitter(TextEmitter emitter) {
        return emitter.emit().debounce(300, TimeUnit.MILLISECONDS);
    }

    @Override
    public SearchEngine addSearchProvider(SearchProvider engine) {
        searchProviders.add(engine);
        return this;
    }
}
