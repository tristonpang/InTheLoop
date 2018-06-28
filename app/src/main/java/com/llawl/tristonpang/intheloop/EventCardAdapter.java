package com.llawl.tristonpang.intheloop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.CardViewHolder> {
    private Context mContext;
    private List<EventInfo> mEventsData;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date;
        public ImageView cardImage;

        public CardViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.cardEventName);
            date = v.findViewById(R.id.cardEventDate);
            cardImage = v.findViewById(R.id.cardImg);
        }
    }

    public EventCardAdapter(Context context, List<EventInfo> dataset) {
        this.mContext = context;
        this.mEventsData = dataset;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_card, parent, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        //set text views
        EventInfo event = mEventsData.get(position);
        holder.name.setText(event.getName());
        holder.date.setText(event.getDate());

        //find and load card image from storage
        String imgName = event.getImageName();
        StorageReference pathRef = FirebaseStorage.getInstance().getReference().child("images/" + imgName);
        Glide.with(mContext).using(new FirebaseImageLoader()).load(pathRef).into(holder.cardImage);
    }

    @Override
    public int getItemCount() {
        return mEventsData.size();
    }
}
