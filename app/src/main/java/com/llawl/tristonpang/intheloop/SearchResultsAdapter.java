package com.llawl.tristonpang.intheloop;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<EventInfo> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mVenue;
        public TextView mDate;

        public ViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.org_row_name);
            mVenue = v.findViewById(R.id.org_row_venue);
            mDate = v.findViewById(R.id.org_row_date);
        }
    }

    public SearchResultsAdapter(List<EventInfo> dataset) {
        mDataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organised_event_row, parent, false);
        Log.d("InTheLoop", "Adapter, onCreateViewHolder");

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SearchResultsAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        EventInfo event = mDataset.get(position);
        Log.d("InTheLoop", "Adapter, Event name: " + event.getName());
        holder.mName.setText(event.getName());
        holder.mVenue.setText(event.getVenue());
        holder.mDate.setText(event.getDate());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
