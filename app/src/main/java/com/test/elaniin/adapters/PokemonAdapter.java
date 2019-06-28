package com.test.elaniin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.test.elaniin.R;
import com.test.elaniin.retrofit.model.PokemonEntry;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.DataObjectHolder> {

    private static String LOG_TAG = "PokemonAdapter";
    private List<PokemonEntry> pokemonList;
    private static ClickListener clickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView txtPokemon;
        ImageView imgPokemon;

        public DataObjectHolder(View itemView) {
            super(itemView);
            txtPokemon = itemView.findViewById(R.id.txtPokemon);
            imgPokemon= itemView.findViewById(R.id.imgPokemon);
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

    public PokemonAdapter(List<PokemonEntry> pokemonList) {
        this.pokemonList = pokemonList;
    }

    public PokemonAdapter(){
        this.pokemonList = new ArrayList<>();
    }
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_detail, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txtPokemon.setText(pokemonList.get(position).getPokemonSpecies().getName());

    }

    public void addItem(PokemonEntry dataObj) {
        pokemonList.add(dataObj);
        notifyItemInserted(pokemonList.size()-1);
    }

    public void deleteItem(int index) {
        pokemonList.remove(index);
        notifyItemRemoved(index);
    }

    public List<PokemonEntry> getPokemonList(){
        return pokemonList;
    }
    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public PokemonEntry getItemIdForPosition(int position) {
        return pokemonList.get(position);
    }

    public interface ClickListener {
        public void onItemClick(int position, View v);
    }
}
