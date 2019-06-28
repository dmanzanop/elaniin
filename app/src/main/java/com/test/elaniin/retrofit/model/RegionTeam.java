package com.test.elaniin.retrofit.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionTeam implements Serializable {
    private String teamDescription;
    private String teamName;
    private List<PokemonEntry> teamPokemonList;
    private String teamType;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("teamDescription", teamDescription);
        result.put("teamName", teamName);
        List<Map<String,Object>> teamPokemonListMap = new ArrayList<>();
        for (PokemonEntry pokemonEntry:teamPokemonList) {
            teamPokemonListMap.add(pokemonEntry.toMap());
        }
        result.put("teamPokemonList", teamPokemonListMap);
        result.put("teamType",teamType);
        return result;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<PokemonEntry> getTeamPokemonList() {
        return teamPokemonList;
    }

    public void setTeamPokemonList(List<PokemonEntry> teamPokemonList) {
        this.teamPokemonList = teamPokemonList;
    }

    public String getTeamType() {
        return teamType;
    }

    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }
}
