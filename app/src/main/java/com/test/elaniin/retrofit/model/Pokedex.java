package com.test.elaniin.retrofit.model;

import java.util.List;

public class Pokedex {

    private Integer id;
    private Boolean isMainSeries;
    private String name;
    private List<Name> names = null;
    private List<PokemonEntry> pokemon_entries = null;
    private Region region;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsMainSeries() {
        return isMainSeries;
    }

    public void setIsMainSeries(Boolean isMainSeries) {
        this.isMainSeries = isMainSeries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public List<PokemonEntry> getPokemonEntries() {
        return pokemon_entries;
    }

    public void setPokemonEntries(List<PokemonEntry> pokemonEntries) {
        this.pokemon_entries = pokemonEntries;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

}