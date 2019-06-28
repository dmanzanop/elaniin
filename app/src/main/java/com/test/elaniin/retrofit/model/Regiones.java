package com.test.elaniin.retrofit.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Regiones {
    private List<RegionTeam> team;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        List<Map<String,Object>> teamListMap = new ArrayList<>();
        for (RegionTeam regionTeam:team) {
            teamListMap.add(regionTeam.toMap());
        }
        result.put("team", teamListMap);
        return result;
    }
    public List<RegionTeam> getTeam() {
        return team;
    }

    public void setTeam(List<RegionTeam> team) {
        this.team = team;
    }
}
