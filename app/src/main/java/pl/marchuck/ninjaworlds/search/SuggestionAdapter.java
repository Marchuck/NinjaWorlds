package pl.marchuck.ninjaworlds.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.ninjaworlds.R;
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

    List<Place> dataSet;

    public SuggestionAdapter(List<Place> dataSet) {
        this.dataSet = dataSet;
        if (dataSet == null) dataSet = new ArrayList<>();
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
    public void onBindViewHolder(SuggestionAdapterViewHolder holder, int position) {
        final Place item = dataSet.get(position);
        holder.textView.setText(item.route);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class SuggestionAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public SuggestionAdapterViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text);
        }
    }
}


