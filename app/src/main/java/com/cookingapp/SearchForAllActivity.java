package com.cookingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchForAllActivity extends AppCompatActivity {

    private Button buttonSearcn;
    private TextView goBack;
    private RecyclerView recyclerView;
    private DatabaseReference mReference;
    private ArrayList<RecipesInfo> recInfoList;
    private MyAdapter adapter;
    private TextView recipesNameText;
    private String recipeNameString;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_all);

        mReference = FirebaseDatabase.getInstance().getReference().child("Recipes");

        buttonSearcn = findViewById(R.id.buttonSearch);
        goBack = findViewById(R.id.textViewGoBack);
        recyclerView = findViewById(R.id.recyclerViewForAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipesNameText = findViewById(R.id.recipeNameText);

        Bundle bundle = this.getIntent().getExtras();
        String recipeNameBundle = bundle.getString("recipeName");
        recipesNameText.setText(recipeNameBundle);


        if (recipeNameBundle == null || "".equals(recipeNameBundle)) {
            selectAllRecipes();
        } else {
            selectRecipesByName(recipeNameBundle);
        }


        buttonSearcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeNameString = recipesNameText.getText().toString();
                if(recipeNameString == null || "".equals(recipeNameString)){
                    selectAllRecipes();
                }else{
                    selectRecipesByName(recipeNameString);
                }

            }
        });


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeNameString = recipesNameText.getText().toString();
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("recipeName",recipeNameString);
                intent.putExtras(bundle);
                intent.setClass(SearchForAllActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    public void selectAllRecipes(){
        recInfoList = new ArrayList<RecipesInfo>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Recipes").orderByChild("rating");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final RecipesInfo info = dataSnapshot1.getValue(RecipesInfo.class);
                    info.setRecipeId(dataSnapshot1.getKey());
                    uid = info.getUserId();
                    mReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                            info.setUserName(userInfo.getEmail());
                            adapter.flag = "1";
                            recyclerView.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    recInfoList.add(info);
                }
                Collections.reverse(recInfoList);
                adapter = new MyAdapter(SearchForAllActivity.this, recInfoList);
                if(recInfoList.size() == 0){
                    adapter.flag = "1";
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchForAllActivity.this, "Something is Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void selectRecipesByName(final String name){
        recInfoList = new ArrayList<RecipesInfo>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Recipes").orderByChild("ingredient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :  dataSnapshot.getChildren()){
                    final RecipesInfo info = dataSnapshot1.getValue(RecipesInfo.class);
                    info.setRecipeId(dataSnapshot1.getKey());
                    uid  = info.getUserId();
                    String recName = info.getIngredient();
                    int i = recName.indexOf(name);
                    if(i >= 0){
                        mReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                        mReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                                info.setUserName(userInfo.getEmail());
                                adapter.flag = "1";
                                recyclerView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        recInfoList.add(info);
                    }
                }
                adapter = new MyAdapter(SearchForAllActivity.this, recInfoList);
                if(recInfoList.size() == 0){
                    adapter.flag = "1";
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchForAllActivity.this,"Something is Error",Toast.LENGTH_SHORT).show();
            }

        });

    }
}
