package pl.marchuck.ninjaworlds.apis.places;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.marchuck.ninjaworlds.search.SearchProvider;
import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 30.08.16.
 */
public class PoiSuggestionProvider implements SearchProvider {
    public static final String TAG = PoiSuggestionProvider.class.getSimpleName();
    private final LatLng northEastKrk = new LatLng(50.114825, 20.147381);
    private final LatLng southWestKrk = new LatLng(49.991820, 19.797192);
    private final GoogleApiClient apiClient;
    private final LatLngBounds latLngBounds = new LatLngBounds(southWestKrk, northEastKrk);
    @Nullable
    private AutocompleteFilter filter;

    public PoiSuggestionProvider(GoogleApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public SearchProvider withSearchFilter(@Nullable AutocompleteFilter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public rx.Observable<List<CharSequence>> getSuggestions(final CharSequence query) {
        return Observable.fromAsync(new Action1<AsyncEmitter<List<CharSequence>>>() {
            @Override
            public void call(final AsyncEmitter<List<CharSequence>> charSequenceAsyncEmitter) {
                Log.i(TAG, "handleSuggestions: ");
                PendingResult<AutocompletePredictionBuffer> result =
                        Places.GeoDataApi.getAutocompletePredictions(apiClient, query.toString(),
                                latLngBounds, filter);
                result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(@NonNull AutocompletePredictionBuffer buff) {
                        List<CharSequence> sequences = new ArrayList<>();
                        int indexOfSuggestion = 0;

                        try {
                            AutocompletePrediction nextPrediction = buff.get(indexOfSuggestion);
                            while (nextPrediction.isDataValid()) {
                                sequences.add(new PoiSequence(nextPrediction));
                                ++indexOfSuggestion;
                                nextPrediction = buff.get(indexOfSuggestion);
                            }
                        } catch (IllegalStateException x) {
                            //gotcha!
                        } finally {
                            charSequenceAsyncEmitter.onNext(sequences);
                            buff.release();
                        }
                    }
                });
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .throttleWithTimeout(300, TimeUnit.MILLISECONDS);
    }
}
