package pl.marchuck.ninjaworlds.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.experimantal.Call;
import pl.marchuck.ninjaworlds.experimantal.SearchViewEmitter;
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
    public static final String TAG = SelectRouteDialog.class.getSimpleName();
    @Nullable
    private SelectionListener selectionListener;
    private RecyclerView.Adapter adapter;
    private SearchEngine searchEngine;
    private String title = "";

    public SelectRouteDialog(Context context) {
        super(context);
    }

    public SelectRouteDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public SelectRouteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setTitle("XDXDXD");
    }

    public SelectRouteDialog withSelectionListener(@Nullable SelectionListener listener) {
        this.selectionListener = listener;
        return this;
    }

    public SelectRouteDialog withTitle(String s) {
        Log.d(TAG, "withTitle: " + s);
        this.title = s;
        return this;
    }

    public Dialog build() {
        setContentView(R.layout.activity_popup);
        SearchViewEmitter somethingWhichTextChanges = (SearchViewEmitter) findViewById(R.id.searchview);
        //may be changed to edittext?
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new SuggestionAdapter(null).withClickListener(new Call<CharSequence>() {
            @Override
            public void call(CharSequence place) {
                if (selectionListener != null) selectionListener.onRouteSelected(place);
                dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        searchEngine = new RouteSearchEngine(somethingWhichTextChanges)
                .addSearchProvider(new SearchRoutesProvider(getContext()))
                .init();
        searchEngine.onSuggestedAction(new Action1<List<CharSequence>>() {
            @Override
            public void call(List<CharSequence> places) {
                ((SuggestionAdapter) adapter).updateDataset(places);
                adapter.notifyDataSetChanged();
            }
        });
        return this;
    }

    public interface SelectionListener {
        void onRouteSelected(CharSequence place);
    }
}
