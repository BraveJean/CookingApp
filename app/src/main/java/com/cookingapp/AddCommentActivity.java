package com.cookingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AddCommentActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener{
    private Bundle bundle;
    private DatabaseReference databaseReference;
    private String recipeName;
    private String CommentsText;
    private String userId;
    private String recipeId;
    private String uid;
    private float RatingFinal;
    private String ratingString;
    private EditText EtComments;
    private RatingBar ratingBar;
    private int CnR;
    private ArrayList<String> userIdList;
    private ArrayList<Rating> ratingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //go Back
        TextView goBack = findViewById(R.id.goBack);
        bundle = this.getIntent().getExtras();
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(AddCommentActivity.this,ShowCommentActivity.class);
                startActivity(intent);
            }
        });

        //get recipeName
        recipeName = bundle.getString("recipeName");
        TextView recipeNameText = findViewById(R.id.RName);
        recipeNameText.setText(recipeName);

        //get userName
        final String userName = bundle.getString("userName");
        TextView userNameText = findViewById(R.id.UName);
        userNameText.setText(userName);

        //get userId
        userId = bundle.getString("userId");

        //get recipeId
        recipeId = bundle.getString("recipeId");

        //Rating
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setMax(5);//set the maximal score
        ratingBar.setProgress(0);//set the score now
        ratingBar.setOnRatingBarChangeListener(this);

        //confirm and add it into comment sheet
        TextView Confirm = findViewById(R.id.butConf);
        Confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //comment
                EtComments =findViewById(R.id.editComment);
                CommentsText = EtComments.getText().toString();
                if ((!"".equals(CommentsText)&& CommentsText!=null) && (!"".equals(ratingString)&&ratingString!=null)){  //comment & rating
                    //add into comment sheet
                    AddComment();
                    //add into rating sheet
                    AddRating();
                }
                else if( ("".equals(CommentsText)||CommentsText==null) && (!"".equals(ratingString)&& ratingString!=null)){ //No comment & rating
                    //add into rating sheet
                    AddRating();
                }
                else if( (!"".equals(CommentsText)&& CommentsText!=null) && ("".equals(ratingString)||ratingString==null)){ //comment & No rating
                    //add into comment sheet
                    AddComment();

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(AddCommentActivity.this,ShowCommentActivity.class);
                    startActivity(intent);

                }

                else{
                    Toast.makeText(AddCommentActivity.this, "Please add some comment and rating", Toast.LENGTH_LONG).show();
                }
            }
        });

        //cancel button and refresh it S
        TextView cancel = findViewById(R.id.butCancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(AddCommentActivity.this, AddCommentActivity.class);
                startActivity(intent);

            }
        });


    }
   //Show Rating
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Toast.makeText(AddCommentActivity.this, "rating:"+rating, Toast.LENGTH_LONG).show();
        RatingFinal = rating;
        ratingString = Float.toString(RatingFinal);
    }

    private void AddComment(){
        uid = FirebaseAuth.getInstance().getUid();
        //add into comment sheet
        userIdList = new ArrayList<>();
        Query query1 = FirebaseDatabase.getInstance().getReference().child("Comment").orderByChild("recipeId").equalTo(recipeId);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Comment com = data.getValue(Comment.class);
                    userIdList.add(com.getCommentUser());
               }
               int count =0 ;
               if(userIdList.size() > 0){
                   for(int i =0;i<userIdList.size();i++){
                        if(uid.equals(userIdList.get(i))){
                            count = count+1;
                        }
                   }
               }
               if(count > 0){
                   Toast.makeText(AddCommentActivity.this, "You have commented it once. Please try to comment other recipes.",Toast.LENGTH_LONG).show();
               }else{
                   DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
                   Comment infoC = new Comment(CommentsText, uid, recipeId,userId);
                   mReference.child("Comment").child(mReference.push().getKey()).setValue(infoC);//random key
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void AddRating(){
        uid = FirebaseAuth.getInstance().getUid();
        //add into comment sheet
        ratingList = new ArrayList<Rating>();
        //add into rating sheet
        Query query2 = FirebaseDatabase.getInstance().getReference().child("Rating").orderByChild("recipeId").equalTo(recipeId);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Rating rate= data.getValue(Rating.class);
                    ratingList.add(rate);
                }
                int count =0 ;
                if(ratingList.size() > 0){
                    for(int i =0;i<ratingList.size();i++){
                        String userId = ratingList.get(i).getRatingUser();
                        if(uid.equals(userId)){
                            count = count+1;
                        }
                    }
                }
                if(count > 0){
                    Toast.makeText(AddCommentActivity.this, "You have commented it once. Please try to comment other recipes.",Toast.LENGTH_LONG).show();
                }else{
                    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
                    Rating infoR = new Rating(ratingString, uid, recipeId,userId);
                    mReference.child("Rating").child(mReference.push().getKey()).setValue(infoR);//random key
                    
                    Float countRating = 0f;
                    for(int i =0;i<ratingList.size();i++){
                        Float rating = Float.parseFloat(ratingList.get(i).getRating());
                        countRating = countRating + rating;
                    }

                    DecimalFormat df2 = new DecimalFormat("###.0");
                    int size = ratingList.size() +1;

                    String rating = df2.format((countRating+ RatingFinal)/ size);

                    HashMap<String, Object> result = new HashMap<>();//store urlV into data
                    result.put("rating", rating);
                    FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);//update dataS

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(AddCommentActivity.this,ShowCommentActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
