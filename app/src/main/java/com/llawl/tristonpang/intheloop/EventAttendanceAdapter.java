package com.llawl.tristonpang.intheloop;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EventAttendanceAdapter extends RecyclerView.Adapter<EventAttendanceAdapter.ViewHolder> {
    private List<UserData> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mRC;
        public TextView mEmail;

        public ViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.attendance_row_name);
            mRC = v.findViewById(R.id.attendance_row_rc);
            mEmail = v.findViewById(R.id.attendance_row_email);
        }
    }

    public EventAttendanceAdapter(List<UserData> dataset) {
        mDataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventAttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_attendance_row, parent, false);
        Log.d("InTheLoop", "Adapter, onCreateViewHolder");

        return new EventAttendanceAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EventAttendanceAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        UserData user = mDataset.get(position);
        Log.d("InTheLoop", "Adapter, User name: " + user.getName());
        holder.mName.setText(user.getName());
        holder.mRC.setText(user.getRC());
        holder.mEmail.setText(user.getEmail());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
