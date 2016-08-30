package pl.marchuck.ninjaworlds.apis.weather;

import android.content.Context;

import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.apis.Common;
import pl.marchuck.ninjaworlds.apis.weather.model.WeatherResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class WeatherAPI {
    private final String endpoint = "http://api.openweathermap.org/data/2.5/";
    // http://api.openweathermap.org/data/2.5/weather?q=Cracow&APPID=9827159fd98029f8eac17455fc826ecf

    private String apiKey;
    private API api;

    public WeatherAPI(Context context) {
        apiKey = context.getResources().getString(R.string.weather_app_key);
        api = Common.buildRetrofit(endpoint).create(API.class);
    }

    public rx.Observable<WeatherResponse> getCurrentWeather() {
        return api.getCurrentWeather("Cracow", apiKey);
    }

    private interface API {
        @GET("weather")
        rx.Observable<WeatherResponse> getCurrentWeather(@Query("q") String city, @Query("APPID") String apiKey);
    }

}
