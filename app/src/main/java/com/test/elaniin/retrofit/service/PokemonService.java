package com.test.elaniin.retrofit.service;
//endpoint
//https://pokeapi.co/api/v2/

import com.test.elaniin.retrofit.model.Pokedex;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokemonService {
    @GET("region")
    Call<com.test.elaniin.retrofit.model.Region> getRegions();

    @GET("region/{regionName}")
    Call<com.test.elaniin.retrofit.model.RegionDetail> getRegion(@Path("regionName") String regionName);

    @GET("type")
    Call<com.test.elaniin.retrofit.model.Region> getPokemonType();

    @GET("pokedex/{id}")
    Call<com.test.elaniin.retrofit.model.Pokedex> getPokedex(@Path("id") String id);
}