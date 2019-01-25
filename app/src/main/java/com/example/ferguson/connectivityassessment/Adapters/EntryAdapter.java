package com.example.ferguson.connectivityassessment.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ferguson.connectivityassessment.Models.Entry;
import com.example.ferguson.connectivityassessment.R;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private static ClickListener clickListener;
    private LayoutInflater layoutInflater;
    private List<Entry> entryList;


    public EntryAdapter(LayoutInflater layoutInflater, List<Entry> entryList) {
        this.layoutInflater = layoutInflater;
        this.entryList = entryList;
    }


    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View entryItem = layoutInflater.inflate(R.layout.entry_item, parent, false);
        EntryViewHolder entryViewHolder = new EntryViewHolder(entryItem);
        entryViewHolder.view = entryItem;
        entryViewHolder.schoolname = entryItem.findViewById(R.id.school_name);
        entryViewHolder.schoolLat = entryItem.findViewById(R.id.school_lat);
        entryViewHolder.schoolLng = entryItem.findViewById(R.id.school_lng);
        entryViewHolder.date_of_entry = entryItem.findViewById(R.id.school_dor);

        return entryViewHolder;

    }

    @Override
    public void onBindViewHolder(final EntryViewHolder holder, int position) {
        final Entry entry = entryList.get(position);
        holder.schoolname.setText(entry.getSchool_name());
        holder.schoolLat.setText(String.valueOf(entry.getSchool_latitude()));
        holder.schoolLng.setText(String.valueOf(entry.getSchool_longitude()));
        holder.date_of_entry.setText(String.valueOf(entry.getDate_of_entry()));

        holder.setItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView schoolname;
        public TextView schoolLat;
        public TextView schoolLng;
        public TextView date_of_entry;
        int position;
        View view;

        public TextView getSchoolname() {
            return schoolname;
        }

        public void setSchoolname(TextView schoolname) {
            this.schoolname = schoolname;
        }

        public TextView getSchoolLat() {
            return schoolLat;
        }

        public void setSchoolLat(TextView schoolLat) {
            this.schoolLat = schoolLat;
        }

        public TextView getSchoolLng() {
            return schoolLng;
        }

        public void setSchoolLng(TextView schoolLng) {
            this.schoolLng = schoolLng;
        }

        public TextView getDate_of_entry() {
            return date_of_entry;
        }

        public void setDate_of_entry(TextView date_of_entry) {
            this.date_of_entry = date_of_entry;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public EntryViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }

        public void setItemClickListener(ClickListener clickListener) {
            EntryAdapter.clickListener = clickListener;
        }


    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
}
