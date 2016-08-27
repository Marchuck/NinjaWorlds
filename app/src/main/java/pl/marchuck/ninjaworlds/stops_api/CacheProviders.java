package pl.marchuck.ninjaworlds.stops_api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.LifeCache;

/**
 * General cache provider for many REST APIs
 *
 * @author Lukasz Marczak
 *         27.08.16.
 */
public interface CacheProviders {

    @LifeCache(duration = 12, timeUnit = TimeUnit.HOURS)
    rx.Observable<List<String>> provideStops(rx.Observable<List<String>> stopsAsObservable);
}
