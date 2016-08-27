package pl.marchuck.ninjaworlds.stops_api;

import java.io.File;
import java.util.List;

import io.rx_cache.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class DataRepository {

    private final CacheProviders providers;

    public DataRepository(File cacheDir) {
        providers = new RxCache.Builder()
                .useExpiredDataIfLoaderNotAvailable(true)
                .persistence(cacheDir, new GsonSpeaker())
                .using(CacheProviders.class);
    }

    public Observable<List<String>> getStops() {
        return providers.provideStops(StopsAPI.provideStops());
    }
}
