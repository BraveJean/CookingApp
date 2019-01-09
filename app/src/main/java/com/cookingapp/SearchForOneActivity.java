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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchForOneActivity extends AppCompatActivity{
    private Button buttonSearcn;
    private TextView goBack;
    private DatabaseReference mReference;
    private RecyclerView recyclerView;
    private ArrayList<RecipesInfo> recInfoList;
    private MyAdapter adapter;
    private TextView recipesNameText;
    private String recipeNameString;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_one);

        buttonSearcn = findViewById(R.id.buttonSearch);
        goBack = findViewById(R.id.textViewGoBack);
        recyclerView = findViewById(R.id.recyclerViewForOne);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipesNameText = findViewById(R.id.recipeNameText);
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        uid= user.getUid();

        selectAllRecipes();

        buttonSearcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeNameString = recipesNameText.getText().toString();
                if (recipeNameString == null || "".equals(recipeNameString)) {
                    selectAllRecipes();
                } else {
                    selectRecipesByName(recipeNameString);
                }
            }
        });


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(SearchForOneActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    public void selectAllRecipes() {
        recInfoList = new ArrayList<RecipesInfo>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Recipes").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final RecipesInfo info = dataSnapshot1.getValue(RecipesInfo.class);
                    info.setRecipeId(dataSnapshot1.getKey());
                    //final String userId = info.getUserId();
                    mReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                            info.setUserName(userInfo.getEmail());
                            adapter.flag = "0" ;
                            recyclerView.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    recInfoList.add(info);
                }
                adapter = new MyAdapter(SearchForOneActivity.this, recInfoList);
                if(recInfoList.size() == 0){
                    adapter.flag ="0";
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchForOneActivity.this, "Something is Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void selectRecipesByName(final String name) {
        recInfoList = new ArrayList<RecipesInfo>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Recipes").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :  dataSnapshot.getChildren()){
                    final RecipesInfo info = dataSnapshot1.getValue(RecipesInfo.class);
                    info.setRecipeId(dataSnapshot1.getKey());
                    String recName = info.getIngredient();
                    int i = recName.indexOf(name);
                    if(i >= 0){
                        mReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                        mReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                                info.setUserName(userInfo.getEmail());
                                adapter.flag ="0";
                                recyclerView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        recInfoList.add(info);
                    }
                }
                adapter = new MyAdapter(SearchForOneActivity.this,recInfoList);
                if(recInfoList.size() == 0){
                    adapter.flag ="0";
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchForOneActivity.this,"Something is Error",Toast.LENGTH_SHORT).show();
            }

        });
    }
}
