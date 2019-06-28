package com.test.elaniin.retrofit.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PokemonEntry implements Serializable {

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("entry_number", entry_number);
        result.put("pokemon_species", pokemon_species.toMap());
        return result;
    }

    private int entry_number;
    private Result pokemon_species;

    public int getEntryNumber() {
        return entry_number;
    }

    public void setEntryNumber(int entryNumber) {
        this.entry_number = entryNumber;
    }

    public Result getPokemonSpecies() {
        return pokemon_species;
    }

    public void setPokemonSpecies(Result pokemonSpecies) {
        this.pokemon_species = pokemonSpecies;
    }


}