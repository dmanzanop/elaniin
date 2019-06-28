package com.test.elaniin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.elaniin.adapters.PokemonAdapter;
import com.test.elaniin.retrofit.Util.Util;
import com.test.elaniin.retrofit.model.Pokedex;
import com.test.elaniin.retrofit.model.PokemonEntry;
import com.test.elaniin.retrofit.model.Region;
import com.test.elaniin.retrofit.model.Result;
import com.test.elaniin.retrofit.service.PokemonService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.util.Collections.sort;

public class Team extends BaseActivity {

    List<PokemonEntry> pokemonList;
    List<Result> pokemonTypeList;
    EditText teamName;
    EditText teamNumber;
    EditText teamDescription;
    Spinner pokemonType;
    Spinner pokemon;
    ImageButton addPokemon;
    RecyclerView recyclerView;
    Dialog d;
    String regionName;
    String regionUrl;
    int pokedexCount;
    String pokedexURL;
    List<String> arrayPokemon;
    ArrayAdapter<String> pokemonAdapter;
    PokemonAdapter adapter = new PokemonAdapter();
    DatabaseReference mTeamRef;
    DatabaseReference mDatabase;
    long maxId;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    int pokemonCount = adapter.getItemCount();
                    if(pokemonCount<3 || pokemonCount>6){
                        dialogError();
                    }else{
                        writeNewTeam();
                        Toast.makeText(Team.this,R.string.teamSave,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        pokemonType = findViewById(R.id.teamType);
        pokemon = findViewById(R.id.pokemon);
        addPokemon = findViewById(R.id.addPokemon);
        recyclerView = findViewById(R.id.pokeList);
        teamName = findViewById(R.id.teamName);
        teamNumber = findViewById(R.id.teamNumber);
        teamDescription = findViewById(R.id.teamDescription);
        arrayPokemon = new ArrayList<>();

        pokemonAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, arrayPokemon);
        pokemonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter.setOnItemClickListener(new PokemonAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //delete
                dialog(position);
            }
        });
        recyclerView.setAdapter(adapter);

        pokemon.setAdapter(pokemonAdapter);
        getPokemonType();

        Bundle extras = getIntent().getExtras();
        String mode = extras.getString("mode");
        if(mode.equals("add")){
            regionName = extras.getString("regionName");
            regionUrl = extras.getString("regionUrl");
            pokedexCount = extras.getInt("pokedexCount");
            pokedexURL = extras.getString("pokedexURL");
            pokedexURL = pokedexURL.replace("https://pokeapi.co/api/v2/pokedex/","").replace("/","");
        }else {
            regionName = extras.getString("regionName");
            teamName.setText(extras.getString("teamName"));
            teamNumber.setText("" + extras.getInt("teamNumber"));
            teamNumber.setEnabled(false);
            teamDescription.setText(extras.getString("teamDescription"));
            pokedexURL = extras.getString("pokedexURL");
            pokedexURL = pokedexURL.replace("https://pokeapi.co/api/v2/pokedex/","").replace("/","");
            type = extras.getString("teamType");
            for (int i = 0; i < extras.getInt("pokemonSize");i++) {
                adapter.addItem((PokemonEntry) extras.get("pokemon" + i));
            }
        }
        getPokemonByRegion();
//        String regionUrlChild = regionUrl.replace("https://pokeapi.co/api/v2/region/","").replace("/","");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UserID = FirebaseAuth.getInstance().getUid();
        mTeamRef = mDatabase.child(UserID).child("Team").child(regionName);
        if(mode.equals("add")){
            teamListener();
        }
        addPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (PokemonEntry p:pokemonList) {
                    if(p.getPokemonSpecies().getName().equals(pokemon.getSelectedItem().toString())){
                        adapter.addItem(p);
                        break;
                    }
                }
            }
        });
    }

    private void teamListener() {
        mTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    maxId = dataSnapshot.getChildrenCount();
                }
                maxId++;
                teamNumber.setText("" + maxId);
                teamNumber.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isValid() {
        int error = 0;

        if (teamName.getText().toString().isEmpty()){
            error++;
            TextInputLayout til = (TextInputLayout) teamName.getParent().getParent();
            til.setErrorEnabled(true);
            til.setError(getString(R.string.requered));
        }else{
            TextInputLayout til = (TextInputLayout) teamName.getParent().getParent();
            til.setErrorEnabled(false);
        }
        if (teamNumber.getText().toString().isEmpty()){
            error++;
            TextInputLayout til = (TextInputLayout) teamNumber.getParent().getParent();
            til.setErrorEnabled(true);
            til.setError(getString(R.string.requered));
        }else{
            TextInputLayout til = (TextInputLayout) teamNumber.getParent().getParent();
            til.setErrorEnabled(false);
        }
        if (teamDescription.getText().toString().isEmpty()){
            error++;
            TextInputLayout til = (TextInputLayout) teamDescription.getParent().getParent();
            til.setErrorEnabled(true);
            til.setError(getString(R.string.requered));
        }else{
            TextInputLayout til = (TextInputLayout) teamDescription.getParent().getParent();
            til.setErrorEnabled(false);
        }
        return error==0;
    }

    private void dialogError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Team.this);
        builder.setTitle(R.string.error);
        builder.setMessage(getString(R.string.pokemonCount));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                d.dismiss();
            }
        });
        d=builder.create();
        d.show();
    }

    private void dialog(final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Team.this);
        builder.setTitle(R.string.delete);
        builder.setMessage(getString(R.string.deleteMessage));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.deleteItem(position);
                d.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d.dismiss();
            }
        });
        d=builder.create();
        d.show();
    }
    private void getPokemonByRegion() {
        Log.i("URLPokemon",pokedexURL);
        Retrofit retrofit = Util.getRetrofit(this);
        PokemonService pokemonService = retrofit.create(PokemonService.class);
        Call<Pokedex> callPokedex = pokemonService.getPokedex(pokedexURL);
        showProgressDialog();
        callPokedex.enqueue(new Callback<Pokedex>() {
            @Override
            public void onResponse(Call<Pokedex> call, Response<Pokedex> response) {
                hideProgressDialog();
                pokemonList = response.body().getPokemonEntries();
                for (PokemonEntry pokemon:pokemonList) {
                    arrayPokemon.add(pokemon.getPokemonSpecies().getName());
                }
                sort(arrayPokemon);
                pokemonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Pokedex> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }


    private void getPokemonType() {
        Retrofit retrofit = Util.getRetrofit(this);
        PokemonService pokemonService = retrofit.create(PokemonService.class);
        Call<Region> callPokemonType = pokemonService.getPokemonType();
        callPokemonType.enqueue(new Callback<Region>() {
            @Override
            public void onResponse(Call<Region> call, Response<Region> response) {
                hideProgressDialog();
                pokemonTypeList = response.body().getResults();

                String[] arrayPokemonType = new String[pokemonTypeList.size()];

                for (int i=0; i<pokemonTypeList.size(); i++){
                    arrayPokemonType[i]=pokemonTypeList.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, arrayPokemonType);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                pokemonType.setAdapter(adapter);

                if(type!=null) {
                    for (int i = 0; i < pokemonTypeList.size(); i++) {
                        if (type.equals(pokemonTypeList.get(i).getName())) {
                            pokemonType.setSelection(i);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<Region> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }
    private void writeNewTeam() {
//        guarda en firebase database de forma unica las regiones


        Map<String, Object> teamSave = new HashMap<String, Object>();
        teamSave.put("teamName",teamName.getText().toString());
        teamSave.put("teamType",pokemonType.getSelectedItem().toString());
        teamSave.put("teamDescription",teamDescription.getText().toString());
        List<Map<String,Object>> pokemons = new ArrayList<>();
        for (PokemonEntry r:adapter.getPokemonList()) {
            Map<String, Object> resultValues = r.toMap();
            pokemons.add(resultValues);
        }
        teamSave.put("teamPokemonList",pokemons);
        mTeamRef.child(teamNumber.getText().toString()).setValue(teamSave);

    }
}
