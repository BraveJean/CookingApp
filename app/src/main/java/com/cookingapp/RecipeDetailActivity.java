package com.cookingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class RecipeDetailActivity extends AppCompatActivity {
    private TextView butGoBack;
    private TextView butViewCom;
    private TextView butEdit;
    private Bundle bundle;
    private String Flag;
    private RatingBar ratingBar;
    private Button butPic;
    private Button butVid;
    private Uri filePathI;
    private Uri filePathV;
    private String uid;
    private StorageReference mStorage;
    private TextView recipeNameText;
    private TextView userNameText;
    private TextView ingredientText;
    private TextView ingredientOneText;
    private TextView ingredientTwoText;
    private TextView stepsText;
    private ImageView imageView;
    private VideoView videoView;
    private String videoUrl;
    private String imagePath;
    private String recipeName;
    private String userName;
    private String ingredient;
    private String steps;
    private String recipeId;
    private String userId;
    private String rating;
    private static final int PICK_IMAGE_REQUEST =71;
    private static final int PICK_VIDEO_REQUEST =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipesdetail);
        bundle = this.getIntent().getExtras();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        //get recipeId
        uid= user.getUid();//login ID
        userId = bundle.getString("userId");//userId wring the recipe

        Flag = bundle.getString("flag");////////////////check the flag in MyAdapter.java ___>bungle get flag
        String errorFlag = bundle.getString("error");

        if("1".equals(errorFlag)){
            Intent intent = new Intent();
            bundle = new Bundle();
            bundle.putString("flag",Flag);
            intent.putExtras(bundle);
            Toast.makeText(RecipeDetailActivity.this, "No corresponding data found....", Toast.LENGTH_SHORT).show();

            //goBack
            if("1".equals(Flag)){
                intent.setClass(RecipeDetailActivity.this,SearchForAllActivity.class);
                startActivity(intent);
            }else if("0".equals(Flag)){
                intent.setClass(RecipeDetailActivity.this,SearchForOneActivity.class);
                startActivity(intent);
            }else{
                intent.setClass(RecipeDetailActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        }

        //get rating
        rating = bundle.getString("rating");

        //get recipeName
        recipeName = bundle.getString("recipeName");
        recipeNameText = findViewById(R.id.recipeNameText);
        recipeNameText.setText(recipeName);

        //get recipeId
        recipeId = bundle.getString("recipeId");

        //get recipeId
        userId = bundle.getString("userId");//userId wring the recipe

        //get userName
        userName = bundle.getString("userName");
        userNameText = findViewById(R.id.userName);
        userNameText.setText(userName);

        //get ingredient
        ingredient = bundle.getString("ingredient");
        ingredientText = findViewById(R.id.ingredient);
        ingredientOneText = findViewById(R.id.ingredientOne);
        ingredientTwoText = findViewById(R.id.ingredientTwo);
        String[] ingredientList = ingredient.split(",");
        for(int i =0;i<ingredientList.length;i++){
            if(i == 0){
                ingredientText.setText(i+1  +" : "+ingredientList[i]);
            }else if(i ==1){
                ingredientOneText.setText(i+1 +" : "+ingredientList[i]);
            }else{
                ingredientTwoText.setText(i+1 +" : "+ingredientList[i]);
            }
        }

        //get steps
        stepsText = findViewById(R.id.steps);
        steps = bundle.getString("steps");
        stepsText.setText(steps);

        //get the url of imageView
        imageView = findViewById(R.id.imgView);
        imagePath = bundle.getString("imageUrl");
        if(imagePath != null && !"".equals(imagePath)){
            Picasso.get().load(imagePath).into(imageView);
        }

        //get the url of videoView
        videoView = findViewById(R.id.videoView);
        videoUrl = bundle.getString("videoUrl");
        if(videoUrl != null && !"".equals(videoUrl)){
            videoView.setVideoPath(videoUrl);
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
        }
        //edit recipes
        butEdit =findViewById(R.id.butEdit);

        //add picture
        butPic =findViewById(R.id.butPic);

        //add video
        butVid =findViewById(R.id.butVid);

        if(uid.equals(userId)){

            butEdit.setVisibility(View.VISIBLE);
            butPic.setVisibility(View.VISIBLE);
            butVid.setVisibility(View.VISIBLE);

            ingredientText.setFocusable(true);
            ingredientText.setFocusableInTouchMode(true);

            ingredientOneText.setFocusable(true);
            ingredientOneText.setFocusableInTouchMode(true);

            ingredientTwoText.setFocusable(true);
            ingredientTwoText.setFocusableInTouchMode(true);

            stepsText.setFocusable(true);
            stepsText.setFocusableInTouchMode(true);

        }else{
            butEdit.setVisibility(View.INVISIBLE);
            butPic.setVisibility(View.INVISIBLE);
            butVid.setVisibility(View.INVISIBLE);

            ingredientText.setFocusable(false);
            ingredientText.setFocusableInTouchMode(false);

            ingredientOneText.setFocusable(false);
            ingredientOneText.setFocusableInTouchMode(false);

            ingredientTwoText.setFocusable(false);
            ingredientTwoText.setFocusableInTouchMode(false);

            stepsText.setFocusable(false);
            stepsText.setFocusableInTouchMode(false);
        }

        /////////////////Edit and Save
        //butEdit =findViewById(R.id.butEdit);
        butEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSheet();
            }
        });

        //choose picture button
        butPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });

        //choose Video button
        butVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseVideo();
            }
        });

        //go back
        butGoBack = findViewById(R.id.butGoBack);
        butGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                bundle = new Bundle();
                bundle.putString("flag",Flag);
                intent.putExtras(bundle);

                if("1".equals(Flag)){
                    intent.setClass(RecipeDetailActivity.this,SearchForAllActivity.class);
                    startActivity(intent);
                }else if("0".equals(Flag)){
                    intent.setClass(RecipeDetailActivity.this,SearchForOneActivity.class);
                    startActivity(intent);
                }else{
                    intent.setClass(RecipeDetailActivity.this,MenuActivity.class);
                    startActivity(intent);
                }

            }
        });
        // view comment
        butViewCom =findViewById(R.id.butViewCom);
        butViewCom.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(RecipeDetailActivity.this,ShowCommentActivity.class);
                startActivity(intent);
            }
        });
    }
    ///////////edit and save part
    //upload Video to storage and update the database
    private void uploadV( ) {
        if (filePathV != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            StorageReference ref = mStorage.child(uid + "/" + UUID.randomUUID().toString());
            ref.putFile(filePathV)//Video's Path in the phone
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(RecipeDetailActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            videoUrl = taskSnapshot.getUploadSessionUri().toString();//Video's URl

                            HashMap<String, Object> result = new HashMap<>();//store urlV into data
                            result.put("video", videoUrl);
                            FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(RecipeDetailActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded" + (int) progress);
                        }
                    });
        }
    }

    //upload Image to storage and update the database
    private void uploadI( ) {
        if(filePathI != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            StorageReference ref = mStorage.child(uid+"/"+ UUID.randomUUID().toString());
            ref.putFile(filePathI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(RecipeDetailActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                            imagePath = taskSnapshot.getUploadSessionUri().toString();

                            HashMap<String, Object> result = new HashMap<>();//store urlI into data
                            result.put("image", imagePath);
                            FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(RecipeDetailActivity.this,"Failed" + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded" + (int)progress);
                        }
                    });
        }
    }

    //chooseImage from local
    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select"),PICK_IMAGE_REQUEST);
    }

    //chooseVideo from local
    private void ChooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO_REQUEST);
    }


    private void UpdateSheet(){
        uploadI();
        uploadV();
        //ingredient information
        String ingredientOne = ingredientText.getText().toString().trim();
        int index =0;
        if(ingredientOne != null &&!"".equals(ingredientOne)){
            index =  ingredientOne.indexOf(":") + 1;
            ingredientOne = ingredientOne.substring(index).trim();
        }

        String ingredientTwo = ingredientOneText.getText().toString().trim();
        if(ingredientTwo != null &&!"".equals(ingredientTwo)){
            index =  ingredientTwo.indexOf(":") + 1;
            ingredientTwo = ingredientTwo.substring(index).trim();
        }

        String ingredientThree = ingredientTwoText.getText().toString().trim();
        if(ingredientThree != null &&!"".equals(ingredientThree)){
            index =  ingredientThree.indexOf(":") + 1;
            ingredientThree = ingredientThree.substring(index).trim();
        }
        String Ingredient = ingredientOne + "," + ingredientTwo + "," + ingredientThree;// connect the ingredient
        String RName = recipeNameText.getText().toString().trim();//recipes name
        String RStpe = stepsText.getText().toString().trim();//recipes steps

        HashMap<String, Object> result = new HashMap<>();//store urlV into data
        result.put("recipeName", RName);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);//update recipeName

        result.put("ingredient", Ingredient);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);//update ingredient

        result.put("steps", RStpe);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);//update steps

        final AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                bundle = new Bundle();
                bundle.putString("flag",Flag);
                intent.putExtras(bundle);

                if("1".equals(Flag)){
                    intent.setClass(RecipeDetailActivity.this,SearchForAllActivity.class);
                    startActivity(intent);
                }else if("0".equals(Flag)){
                    intent.setClass(RecipeDetailActivity.this,SearchForOneActivity.class);
                    startActivity(intent);
                }else{
                    intent.setClass(RecipeDetailActivity.this,MenuActivity.class);
                    startActivity(intent);
                }

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setTitle("Message");
        builder.setMessage("Saved successfully,Do you want to leave this current page?");
        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            filePathI= data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePathI);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                //e.printStackTrace();
            }

        }
        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK){
            filePathV= data.getData();
            try {
                //MICRO_KIND, size: 96 x 96 thumbnail
                videoView.setVideoURI(filePathV);
                MediaController mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);

            } catch (Exception ex) {
                //e.printStackTrace();
            }
        }
    }
}
