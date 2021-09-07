package com.example.selfjournaling.ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfjournaling.R;
import com.example.selfjournaling.model.Journal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Journal journal = journalList.get(position);
        String imageUrl;

        viewHolder.title.setText(journal.getTitle());
        viewHolder.thoughts.setText(journal.getThought());
        viewHolder.name.setText(journal.getUsername());
        imageUrl = journal.getImageUrl();

        //1 hour ago....
        //source for time: https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal
                .getTimeAdded()
                .getSeconds() * 1000);
        viewHolder.dateAdded.setText(timeAgo);


        //Use picasso library
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_three)
                .fit()
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                title,
                thoughts,
                dateAdded,
                name;

        public ImageView image;
        public ImageButton shareButton;


        public ViewHolder(@NonNull View itemView, Context ctx ){
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.journalTitleList);
            thoughts = itemView.findViewById(R.id.journalThoughtList);
            dateAdded = itemView.findViewById(R.id.journalTimestampList);
            image = itemView.findViewById(R.id.journalImageList);
            name = itemView.findViewById(R.id.journalRowUsername);

            shareButton = itemView.findViewById(R.id.journalRowShareButton);
            shareButton.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "This is the message from implicit intent");
                intent.putExtra(Intent.EXTRA_SUBJECT, "I am sharing my thoughts");
                context.startActivity(intent);

            });
        }
    }
}
