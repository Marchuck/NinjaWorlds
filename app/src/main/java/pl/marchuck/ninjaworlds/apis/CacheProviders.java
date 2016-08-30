package pl.marchuck.ninjaworlds.apis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.LifeCache;
import pl.marchuck.ninjaworlds.apis.smog.model.SmogResponse;
import pl.marchuck.ninjaworlds.apis.weather.model.WeatherResponse;

/**
 * General cache provider for many REST APIs
 *
 * @author Lukasz Marczak
 *         27.08.16.
 */
public interface CacheProviders {

    @LifeCache(duration = 12, timeUnit = TimeUnit.HOURS)
    rx.Observable<List<String>> provideStops(rx.Observable<List<String>> stopsAsObservable);

    @LifeCache(duration = 12, timeUnit = TimeUnit.HOURS)
    rx.Observable<SmogResponse> provideWeatherConditions(rx.Observable<SmogResponse> response);

    @LifeCache(duration = 12, timeUnit = TimeUnit.HOURS)
    rx.Observable<WeatherResponse> provideCurrentWeather(rx.Observable<WeatherResponse> response);
}
