package com.cookingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ShowCommentActivity extends AppCompatActivity{

    private Bundle bundle;
    private String Flag;
    private RatingBar ratingBar;
    private ArrayList<Comment> comList;
    private RecyclerView recyclerView;
    private ComAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);

        bundle = this.getIntent().getExtras();
        Flag = bundle.getString("flag");

        //get recipeName
        String recipeName = bundle.getString("recipeName");
        TextView recipeNameText = findViewById(R.id.recipeName);
        recipeNameText.setText(recipeName);

        //get userName
        String userName = bundle.getString("userName");
        TextView userNameText = findViewById(R.id.userName);
        userNameText.setText(userName);

        //get user Id of this recipe
        String userId = bundle.getString("userId");
        //get the user who already login.
        String uid = FirebaseAuth.getInstance().getUid();

        //get rating
        String ratingString =bundle.getString("rating");
        if(ratingString != null && !"".equals(ratingString)){

            Float rating = Float.parseFloat(ratingString);
            ratingBar = findViewById(R.id.ratingBar);
            ratingBar.setMax(5);//set the maximal score
            ratingBar.setProgress(0);//set the score now
            ratingBar.setRating(rating);
        }

        TextView addNewComment = findViewById(R.id.addCom);
        TextView goBack = findViewById(R.id.goBack);
        recyclerView = findViewById(R.id.commentView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(uid.equals(userId)){
            addNewComment.setVisibility(INVISIBLE);
        }else{
            addNewComment.setVisibility(VISIBLE);
        }

        //get recipeName
        String recipeId = bundle.getString("recipeId");
        searchAllComments(recipeId);


        addNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(ShowCommentActivity.this,AddCommentActivity.class);
                startActivity(intent);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(ShowCommentActivity.this,RecipeDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchAllComments(String recipeId) {
        comList = new ArrayList<Comment>();
        Query res = FirebaseDatabase.getInstance().getReference().child("Comment").orderByChild("recipeId").equalTo(recipeId);
        res.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot date :dataSnapshot.getChildren()){
                    Comment com = date.getValue(Comment.class);
                    comList.add(com);

                }
                adapter = new ComAdapter(ShowCommentActivity.this,comList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
