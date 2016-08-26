package pl.marchuck.ninjaworlds.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.models.Place;
import pl.marchuck.ninjaworlds.search.RouteSearchEngine;
import pl.marchuck.ninjaworlds.search.SearchEngine;
import pl.marchuck.ninjaworlds.search.SearchRoutesProvider;
import pl.marchuck.ninjaworlds.search.SuggestionAdapter;
import rx.functions.Action1;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */
public class SelectRouteDialog extends Dialog {

    @Nullable
    private SelectionListener selectionListener;
    @Nullable
    private Place currentPlace;
    private RecyclerView.Adapter adapter;
    private SearchEngine searchEngine;

    public SelectRouteDialog(Context context) {
        super(context);
    }

    public SelectRouteDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public SelectRouteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public SelectRouteDialog withSelectionListener(@Nullable SelectionListener listener) {
        this.selectionListener = listener;
        return this;
    }

    public SelectRouteDialog withCurrentRoute(@Nullable Place place) {
        this.currentPlace = place;
        return this;
    }

    public Dialog build() {
        setContentView(R.layout.activity_popup);
        EditText editText = (EditText) findViewById(R.id.searchview);
        View btn = findViewById(R.id.search_fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionListener != null)
                    selectionListener.onRouteSelected(currentPlace);
                dismiss();
            }
        });

        //todo: recycler view setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new SuggestionAdapter(null);
        recyclerView.setAdapter(adapter);
        searchEngine = new RouteSearchEngine()
                .addSearchProvider(new SearchRoutesProvider());
        searchEngine.onSuggestedAction(new Action1<List<Place>>() {
            @Override
            public void call(List<Place> places) {
                ((SuggestionAdapter) adapter).updateDataset(places);
                adapter.notifyDataSetChanged();
            }
        });
        return this;
    }

    public interface SelectionListener {
        void onRouteSelected(Place place);
    }
}
