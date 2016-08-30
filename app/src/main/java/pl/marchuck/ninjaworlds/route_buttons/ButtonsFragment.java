package pl.marchuck.ninjaworlds.route_buttons;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import pl.marchuck.ninjaworlds.MainActivity;
import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.apis.places.PoiSuggestionProvider;
import pl.marchuck.ninjaworlds.experimantal.Call;
import pl.marchuck.ninjaworlds.experimantal.SearchViewEmitter;
import pl.marchuck.ninjaworlds.search.RouteSearchEngine;
import pl.marchuck.ninjaworlds.search.SearchEngine;
import pl.marchuck.ninjaworlds.search.SearchPresenter;
import pl.marchuck.ninjaworlds.search.SearchProvider;
import pl.marchuck.ninjaworlds.search.SearchRoutesProvider;
import pl.marchuck.ninjaworlds.search.SuggestionAdapter;
import pl.marchuck.ninjaworlds.util.WeakHandler;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 17.08.16.
 */
@EFragment(R.layout.searchviews_layout)
public class ButtonsFragment extends Fragment implements ButtonsCallbacks, Call<CharSequence>, RouteSearchEngine.AdditionalListener {

    public static final String TAG = ButtonsFragment.class.getSimpleName();

    @ViewById(R.id.rootView)
    RelativeLayout rootView;

    @ViewById(R.id.search)
    FloatingActionButton searchFab;

    @ViewById(R.id.from)
    SearchViewEmitter searchFrom;

    @ViewById(R.id.to)
    SearchViewEmitter searchTo;

    @ViewById(R.id.recyclerview)
    RecyclerView recyclerView;
    SearchEngine searchFromEngine, searchToEngine;
    SearchViewEmitter currentEmitterInUse;
    private SearchPresenter searchPresenter;
    private boolean clicksReady;
    private final Runnable buttonsActiveCallback = new Runnable() {
        @Override
        public void run() {
            clicksReady = true;
            if (searchFrom != null) searchFrom.requestFocus();
        }
    };
    private RecyclerView.Adapter adapter;

    @Click(R.id.from)
    void onClickFrom() {
        Log.d(TAG, "onClickFrom: ");
        currentEmitterInUse = searchFrom;
    }

    @Click(R.id.to)
    void onClickTo() {
        Log.d(TAG, "onClickTo: ");
        currentEmitterInUse = searchTo;
    }

    private Interpolator interpolator() {
        return new AccelerateInterpolator();
    }

    @AfterViews
    public void init() {
        searchFab.setScaleY(0f);
        searchFab.setScaleX(0f);

        searchFrom.setScaleY(0f);
        searchFrom.setScaleX(0f);

        searchTo.setScaleY(0f);
        searchTo.setScaleX(0f);

        rootView.setVisibility(View.VISIBLE);

        searchFrom.animate().scaleX(1).scaleY(1)
                .setDuration(400).setInterpolator(interpolator())
                .setStartDelay(200)
                .start();

        searchTo.animate().scaleX(1).scaleY(1)
                .setDuration(400).setInterpolator(interpolator())
                .setStartDelay(400)
                .start();

        searchFab.animate().scaleX(1).scaleY(1)
                .setDuration(300).setInterpolator(interpolator())
                .setStartDelay(600)
                .start();

        WeakHandler weakHandler = new WeakHandler();
        weakHandler.postDelayed(buttonsActiveCallback, 500);

        Context ctx = getActivity().getApplicationContext();

        adapter = new SuggestionAdapter().withClickListener(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SearchProvider routesProvider = new SearchRoutesProvider(ctx);
        SearchProvider googleProvider = new PoiSuggestionProvider(getApiClient());
        searchFromEngine = new RouteSearchEngine(searchFrom)
                .optionalListner(this)
                .addSearchProvider(routesProvider)
                .addSearchProvider(googleProvider)
                .init();

        searchToEngine = new RouteSearchEngine(searchTo)
                .optionalListner(this)
                .addSearchProvider(routesProvider)
                .addSearchProvider(googleProvider)
                .init();

        searchFromEngine.onSuggestedAction(new Action1<List<CharSequence>>() {
            @Override
            public void call(List<CharSequence> places) {
                if (!newResultIsTheSameAsCurrentInput(searchFrom, places))
                    refreshRecyclerView(places);
            }
        });

        searchToEngine.onSuggestedAction(new Action1<List<CharSequence>>() {
            @Override
            public void call(List<CharSequence> places) {
                if (!newResultIsTheSameAsCurrentInput(searchTo, places))
                    refreshRecyclerView(places);
            }
        });
    }

    private GoogleApiClient getApiClient() {
        return ((MainActivity) getActivity()).apiClient;
    }

    private boolean newResultIsTheSameAsCurrentInput(SearchViewEmitter searchTo, List<CharSequence> places) {
        if (places.size() != 1) return false;
        String textInSearchView = searchTo.getQuery().toString().toLowerCase();
        String textInSuggestion = places.get(0).toString().toLowerCase();
        return textInSearchView.equals(textInSuggestion);
    }

    private void refreshRecyclerView(List<CharSequence> places) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAlpha(.5f);
        ((SuggestionAdapter) adapter).updateDataset(places);
        recyclerView.animate().alpha(1).setDuration(200).setInterpolator(interpolator()).start();
    }

    public void initView() {
        WeakHandler weakHandler = new WeakHandler();
        weakHandler.postDelayed(buttonsActiveCallback, 500);
    }

    @Override
    public void showNavigateButton() {

    }

    @Override
    public void setDestinationText(CharSequence place, @Destination int destination) {

    }

    @Override
    public void call(CharSequence place) {
        currentEmitterInUse.setQuery(place.toString(), false);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onUpdate(SearchEngine engine) {
        Log.d(TAG, "onUpdate: ");
        if (engine == searchFromEngine) {
            currentEmitterInUse = searchFrom;
        } else currentEmitterInUse = searchTo;
    }
}
