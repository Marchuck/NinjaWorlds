package pl.marchuck.ninjaworlds.apis.smog;

import pl.marchuck.ninjaworlds.apis.Common;
import pl.marchuck.ninjaworlds.apis.smog.model.SmogResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class SmogAPI {
    private final String endpoint = "http://powietrze.malopolska.pl/_powietrzeapi/";

    private final API api;

    public SmogAPI() {
        api = Common.buildRetrofit(endpoint).create(API.class);
    }


    public rx.Observable<SmogResponse> getWeatherConditions() {
        return api.getWeatherConditions("danemiasta", 1, "json");
    }

    interface API {
        @GET("api/dane")
        rx.Observable<SmogResponse> getWeatherConditions(@Query("act") String danemiasta,
                                                         @Query("ci_id") int id,
                                                         @Query("format") String format);
    }
}
