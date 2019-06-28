package com.test.elaniin.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.test.elaniin.R;

import java.util.ArrayList;
import java.util.List;

public class TeamByRegionAdapter extends RecyclerView.Adapter<TeamByRegionAdapter.DataObjectHolder> {

    private static String LOG_TAG = "TeamByRegionAdapter";
    private List<String> teamByRegion;
    private static ClickListener clickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView txt;

        public DataObjectHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.teamNameList);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TeamByRegionAdapter(List<String> teamByRegion) {
        this.teamByRegion = teamByRegion;
    }

    public TeamByRegionAdapter(){
        this.teamByRegion = new ArrayList<>();
    }
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_by_region_detail, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txt.setText(teamByRegion.get(position).toString());

    }

    public void addItem(String dataObj) {
        teamByRegion.add(dataObj);
        notifyItemInserted(teamByRegion.size()-1);
    }

    public void deleteItem(int index) {
        teamByRegion.remove(index);
        notifyItemRemoved(index);
    }
    public void deleteAll(){
        teamByRegion = new ArrayList<>();
    }

    public void addList(List<String> teamByRegion){
        this.teamByRegion = teamByRegion;
        notifyDataSetChanged();
    }
    public List<String> getTeamByRegion(){
        return teamByRegion;
    }
    @Override
    public int getItemCount() {
        return teamByRegion.size();
    }

    public String getItemIdForPosition(int position) {
        return teamByRegion.get(position);
    }

    public interface ClickListener {
        public void onItemClick(int position, View v);
    }
}
