package com.test.elaniin.retrofit.model;

import java.util.List;

public class RegionDetail {

    private Integer id;
    private String name;
    private List<Result> locations = null;
    private Result mainGeneration;
    private List<Name> names = null;
    private List<Result> pokedexes = null;
    private List<Result> versionGroups = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Result> getLocations() {
        return locations;
    }

    public void setLocations(List<Result> locations) {
        this.locations = locations;
    }

    public Result getMainGeneration() {
        return mainGeneration;
    }

    public void setMainGeneration(Result mainGeneration) {
        this.mainGeneration = mainGeneration;
    }

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public List<Result> getPokedexes() {
        return pokedexes;
    }

    public void setPokedexes(List<Result> pokedexes) {
        this.pokedexes = pokedexes;
    }

    public List<Result> getVersionGroups() {
        return versionGroups;
    }

    public void setVersionGroups(List<Result> versionGroups) {
        this.versionGroups = versionGroups;
    }

}