package com.test.elaniin;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.elaniin.adapters.TeamByRegionAdapter;
import com.test.elaniin.retrofit.Util.Util;
import com.test.elaniin.retrofit.model.PokemonEntry;
import com.test.elaniin.retrofit.model.Region;
import com.test.elaniin.retrofit.model.RegionDetail;
import com.test.elaniin.retrofit.model.RegionTeam;
import com.test.elaniin.retrofit.model.Result;
import com.test.elaniin.retrofit.service.PokemonService;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    Menu menu;
    MenuItem previousMenuItem;
    List<Result> resultList;
    private DatabaseReference mDatabase;
    DatabaseReference mRegionRef;
    Retrofit retrofit;
    PokemonService pokemonService;
    List<Result> pokedex;
    Result region;
    DatabaseReference mTeamRef;
    Map<String,List<RegionTeam>> teamByRegion = new HashMap<>();
    RecyclerView teamlist;
    TeamByRegionAdapter adapter = new TeamByRegionAdapter();
    ValueEventListener teamListener;
    boolean onPause = false;
    List<RegionTeam> adapterRegion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(region!=null) {
                    Intent intent = new Intent(MainActivity.this, Team.class);
                    intent.putExtra("mode", "add");
                    intent.putExtra("regionName", region.getName());
                    intent.putExtra("regionUrl", region.getUrl());
                    intent.putExtra("pokedexCount", pokedex.size());
                    intent.putExtra("pokedexURL", pokedex.get(pokedex.size()-1).getUrl());
                    startActivity(intent);
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        menu.clear();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRegionRef = mDatabase.child("Region");
//        String UserID = FirebaseAuth.getInstance().getUid();
//        mTeamRef = mDatabase.child(UserID).child("Team");
//        teamListener();
        teamlist = findViewById(R.id.teamList);
        setTeamList();
//        agrega listener de firebase database
        ValueEventListener regionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    resultList = new ArrayList<>();
                    Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                    for (DataSnapshot resultSnapshot: dataSnapshot.getChildren()) {
                        Result result = resultSnapshot.getValue(Result.class);
                        resultList.add(result);
                    }
                    setMenu();
                }
                else {
                    getRegion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("Firebase Database", "Region:onCancelled", databaseError.toException());

            }
        };
        mRegionRef.addValueEventListener(regionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
        deleteListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String UserID = FirebaseAuth.getInstance().getUid();
        mTeamRef = mDatabase.child(UserID).child("Team");
        onPause = false;
        teamListener();
    }

    private void setTeamList() {
        teamlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter.setOnItemClickListener(new TeamByRegionAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //delete
                Intent intent = new Intent(MainActivity.this,Team.class);
                intent.putExtra("mode","edit");
                intent.putExtra("regionName", region.getName());
                intent.putExtra("teamName", adapterRegion.get(position).getTeamName());
                intent.putExtra("teamNumber", (position + 1));
                intent.putExtra("teamDescription", adapterRegion.get(position).getTeamDescription());
                intent.putExtra("teamType", adapterRegion.get(position).getTeamType());
                intent.putExtra("pokemonSize", adapterRegion.get(position).getTeamPokemonList().size());
                intent.putExtra("pokedexURL", pokedex.get(pokedex.size()-1).getUrl());
                for (int i = 0; i < adapterRegion.get(position).getTeamPokemonList().size();i++) {
                    intent.putExtra("pokemon" + i ,adapterRegion.get(position).getTeamPokemonList().get(i));
                }

                startActivity(intent);
            }
        });
        teamlist.setAdapter(adapter);
    }

    private void teamListener() {
        teamListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("onPause" , "" + onPause);
                if(dataSnapshot.exists()) {
                    for (DataSnapshot resultSnapshot: dataSnapshot.getChildren()) {
                        List<RegionTeam> list = new ArrayList<>();
                        for (DataSnapshot team:resultSnapshot.getChildren()) {
                            RegionTeam teamDetail = team.getValue(RegionTeam.class);
                            List<PokemonEntry> p = new ArrayList<>();
                            for (DataSnapshot campos:team.getChildren()) {
                                if(campos.getKey().equals("teamPokemonList")){
                                    PokemonEntry pokemonEntry = new PokemonEntry();
                                    for (DataSnapshot l:campos.getChildren()) {
                                        for (DataSnapshot m:l.getChildren()) {
                                            if(m.getKey().equals("entry_number")){
                                                pokemonEntry.setEntryNumber(Integer.parseInt(m.getValue().toString()));
                                            }
                                            else {
                                                Result r = new Result();
                                                for (DataSnapshot po:m.getChildren()) {
                                                    if(po.getKey().equals("name")){
                                                        r.setName(po.getValue().toString());
                                                    }
                                                    else {
                                                        r.setUrl(po.getValue().toString());
                                                    }
                                                }
                                                pokemonEntry.setPokemonSpecies(r);
                                            }
                                            if(pokemonEntry.getEntryNumber()!=0 && pokemonEntry.getPokemonSpecies() != null){
                                                p.add(pokemonEntry);
                                                pokemonEntry = new PokemonEntry();
                                            }
                                        }

                                    }
                                }
                            }
                            teamDetail.setTeamPokemonList(p);
                            list.add(teamDetail);
                        }
                        teamByRegion.put(resultSnapshot.getKey(),list);
                    }

                    setListTeamByRegion();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mTeamRef.addValueEventListener(teamListener);
    }

    public void deleteListener(){
        for(int i = 0; i<10;i++){
            mTeamRef.removeEventListener(teamListener);
        }
        mTeamRef = null;
    }
    private void setListTeamByRegion() {
        List<String> adapterList = new ArrayList<>();
//        adapter.deleteAll();
        if(teamByRegion.size()>0 && previousMenuItem != null) {
            adapterRegion = teamByRegion.get(previousMenuItem.getTitle().toString());
            if(adapterRegion!=null) {
                for (RegionTeam r : adapterRegion) {
//                    adapter.addItem(r.getTeamName());
                    adapterList.add(r.getTeamName());
                }
            }
            adapter.addList(adapterList);
        }
    }

    public void setMenu(){
        for (Result r:resultList) {
            menu.add(r.getName());
        }
        region = resultList.get(0);
        setPokedex();
        previousMenuItem = navigationView.getMenu().getItem(0);
        navigationView.getMenu().getItem(0).setChecked(true);
        setTitulo(navigationView.getMenu().getItem(0).getTitle().toString());
        setListTeamByRegion();
    }
    private void getRegion() {

//        si no esta en firebase database la informacion se busca en el web service
        retrofit = Util.getRetrofit(this);
        pokemonService = retrofit.create(PokemonService.class);
        showProgressDialog();
        Call<Region> callRegion = pokemonService.getRegions();
        callRegion.enqueue(new Callback<Region>() {
            @Override
            public void onResponse(Call<Region> call, Response<Region> response) {
                hideProgressDialog();
                resultList = response.body().getResults();
                writeNewRegions(resultList);
            }

            @Override
            public void onFailure(Call<Region> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }

    public void setTitulo(String menu){
        MainActivity.this.setTitle("Equipos para: " + menu);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setPokedex(){
        String regionName = region.getName();
        if(pokemonService==null) {
            retrofit = Util.getRetrofit(this);
            pokemonService = retrofit.create(PokemonService.class);
        }
        showProgressDialog();
        Call<RegionDetail> callRegionDetail = pokemonService.getRegion(regionName);
        callRegionDetail.enqueue(new Callback<RegionDetail>() {
            @Override
            public void onResponse(Call<RegionDetail> call, Response<RegionDetail> response) {
                hideProgressDialog();
                pokedex = response.body().getPokedexes();
            }

            @Override
            public void onFailure(Call<RegionDetail> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String menu = item.getTitle().toString();

        for (Result r:resultList) {
            if(r.getName().equals(menu)){
                region = r;
                setPokedex();
                setTitulo(r.getName());
            }
        }
        if (previousMenuItem != null) {
            previousMenuItem.setChecked(false);
        }
        previousMenuItem = item;
        setListTeamByRegion();
        item.setChecked(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void writeNewRegions(List<Result> resultList) {
//        guarda en firebase database de forma unica las regiones
        List<Map<String, Object>> childUpdates = new ArrayList<>();
        for (Result r:resultList) {
            Map<String, Object> resultValues = r.toMap();
            childUpdates.add(resultValues);
        }
        mRegionRef.setValue(childUpdates);
    }
}
