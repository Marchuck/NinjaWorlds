package pl.marchuck.ninjaworlds.apis;

import android.content.Context;

import java.util.List;

import io.rx_cache.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import pl.marchuck.ninjaworlds.apis.smog.SmogAPI;
import pl.marchuck.ninjaworlds.apis.smog.model.SmogResponse;
import pl.marchuck.ninjaworlds.apis.weather.WeatherAPI;
import pl.marchuck.ninjaworlds.apis.weather.model.WeatherResponse;
import rx.Observable;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public class DataRepository {

    private final CacheProviders providers;
    private final SmogAPI smogAPI;
    private final StopsAPI stopsAPI;
    private final WeatherAPI weatherAPI;

    public DataRepository(Context ctx) {

        providers = new RxCache.Builder()
                .useExpiredDataIfLoaderNotAvailable(false)
                .persistence(ctx.getCacheDir(), new GsonSpeaker())
                .using(CacheProviders.class);

        smogAPI = new SmogAPI();
        weatherAPI = new WeatherAPI(ctx);
        stopsAPI = new StopsAPI();
    }

    public Observable<List<String>> getStops() {
        return providers.provideStops(stopsAPI.provideStops());
    }

    public Observable<SmogResponse> getWeatherConditions() {
        return providers.provideWeatherConditions(smogAPI.getWeatherConditions());
    }

    public Observable<WeatherResponse> getCurrentWeather() {
        return providers.provideCurrentWeather(weatherAPI.getCurrentWeather());
    }
}
