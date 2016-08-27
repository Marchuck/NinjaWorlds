package pl.marchuck.ninjaworlds.search;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import java.util.List;

import pl.marchuck.ninjaworlds.R;
import pl.marchuck.ninjaworlds.experimantal.Call;
import pl.marchuck.ninjaworlds.models.Place;

/**
 * @author Lukasz Marczak
 * @since 26.08.16.
 */


/**
 * @author Lukasz Marczak
 * @since 08.05.16.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionAdapterViewHolder> {

    private List<Place> dataSet;
    private Call<Place> caller;

    public SuggestionAdapter(@Nullable List<Place> dataSet) {
        this.dataSet = dataSet;
    }

    public SuggestionAdapter() {
    }

    public void updateDataset(List<Place> places) {
        this.dataSet = places;
        notifyDataSetChanged();
    }

    @Override
    public SuggestionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, null, false);
        return new SuggestionAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuggestionAdapterViewHolder holder, int position) {
        final Place item = dataSet.get(position);
        holder.textView.setText(item.place);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
                if (caller != null) caller.call(item);
                holder.textView.setAlpha(0.5f);
                holder.textView.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public RecyclerView.Adapter withClickListener(Call<Place> listener) {
        this.caller = listener;
        return this;
    }

    public static class SuggestionAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public SuggestionAdapterViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text);
        }
    }
}


