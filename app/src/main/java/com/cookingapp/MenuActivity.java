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

public class MenuActivity extends AppCompatActivity {

    private TextView buttonMyself;
    private Button buttonForAll;
    private TextView addNewRecipe;
    private TextView signOut;
    private TextView recipeNameText;
    private String recipeName;
    private DatabaseReference databaseReference;
    private DatabaseReference mReference;
    private String uid;
    public ArrayList<RecipesInfo> recInfoList;
    public ArrayList<String> userIdList;
    public MyAdapter adapter;
    public RecyclerView recyclerView;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //get userId
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        uid= user.getUid();

        buttonMyself = findViewById(R.id.buttonMyself);
        buttonForAll = findViewById(R.id.buttonForAll);
        addNewRecipe = findViewById(R.id.addNewRecipe);
        signOut = findViewById(R.id.textViewSignOut);
        recipeNameText = findViewById(R.id.recipeName);

        recyclerView = findViewById(R.id.menuView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            String recipeNameBundle = bundle.getString("recipeName");
            recipeNameText.setText(recipeNameBundle);
        }

        recommend();

        buttonMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag","0");
                intent.putExtras(bundle);
                intent.setClass(MenuActivity.this, SearchForOneActivity.class);
                startActivity(intent);
            }
        });

        buttonForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName = recipeNameText.getText().toString();
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag","1");
                bundle.putString("recipeName",recipeName);
                intent.putExtras(bundle);
                intent.setClass(MenuActivity.this, SearchForAllActivity.class);
                startActivity(intent);
            }
        });

        addNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MenuActivity.this, NewRecipesActivity.class);
                startActivity(intent);
            }
        });

        setupFirebaseListener();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });

    }

    public void recommend(){
        recInfoList = new ArrayList<RecipesInfo>();
        userIdList = new ArrayList<String>();
        Query query = FirebaseDatabase.getInstance().getReference().child("Rating").orderByChild("ratingUser").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final Rating info = dataSnapshot1.getValue(Rating.class);
                    String userId = info.getUserId();
                    userIdList.add(userId);
                }

                for(int i = 0;i<userIdList.size();i++){
                    for (int j = userIdList.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
                    {

                        if (userIdList.get(i).equals(userIdList.get(j)))
                        {
                            userIdList.remove(j);
                        }

                    }
                }

                for(int n=0;n<userIdList.size();n++){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Recipes").orderByChild("userId").equalTo(userIdList.get(n));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                                final RecipesInfo recipeInfo = dataSnapshot2.getValue(RecipesInfo.class);
                                mReference = FirebaseDatabase.getInstance().getReference().child("user").child(recipeInfo.getUserId());
                                mReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                                        recipeInfo.setUserName(userInfo.getEmail());
                                        adapter.flag = "3" ;
                                        recyclerView.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                if(recipeInfo.getRating() != null && !"".equals(recipeInfo.getRating())){
                                    Float rating = Float.valueOf(recipeInfo.getRating());
                                    if(rating - 4.0 >=0){
                                        recInfoList.add(recipeInfo);
                                    }
                                }
                            }
                            adapter = new MyAdapter(MenuActivity.this, recInfoList);
                            if(recInfoList.size() == 0){
                                adapter.flag = "3";
                                recyclerView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MenuActivity.this, "Something is Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, "Something is Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){

                }else{
                    Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){

            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}
