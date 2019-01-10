package com.cookingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


public class NewRecipesActivity extends AppCompatActivity{ //implements View.OnClickListener{
    private Context context;
    private EditText etNewRName;
    private EditText etNewIngre;
    private EditText etNewIngre2;
    private EditText etNewIngre3;
    private EditText etNewStep;
    private DatabaseReference databaseReference;
    private String uid;
    private String recipeId;

    private TextView bNewAdd;
    private TextView bNewCancel;
    private TextView bNewGB;
    private Button iBPic;
    private Button iBVideo;
    private TextView textViewGoBack;
    private TextView butCancel;
    private ImageView imageView;
    private VideoView vView1;
    private Uri filePathI;
    private Uri filePathV;
    public String urlI;
    public String urlV;
    private String Rating;
    private StorageReference mStorage;
    private static final int PICK_IMAGE_REQUEST =71;
    private static final int PICK_VIDEO_REQUEST =2;

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_new);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

        etNewRName = findViewById(R.id.etNewRName);
        etNewIngre = findViewById(R.id.etNewIngre);
        etNewIngre2 = findViewById(R.id.etNewIngre2);
        etNewIngre3 = findViewById(R.id.etNewIngre3);
        etNewStep =findViewById(R.id.etNewStep);

        bNewAdd = findViewById(R.id.bNewAdd);
        bNewCancel = findViewById(R.id.bNewCancel);
        bNewGB =findViewById(R.id.bNewGB);
        iBPic =findViewById(R.id.iBPic);
        iBVideo = findViewById(R.id.iBVideo);
//        imgBS1=findViewById(R.id.imgBS1);
//        imgBS2=findViewById(R.id.imgBS2);
//        imgBD1=findViewById(R.id.imgBD1);
//        imgBD2=findViewById(R.id.imgBD2);

        imageView = findViewById(R.id.imgView);
        vView1 =findViewById(R.id.vView1);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        //add an empty sheet in database
        String RName = etNewRName.getText().toString().trim();
        String Ingredient = etNewIngre.getText().toString().trim() +","+ etNewIngre2.getText().toString().trim() +","+ etNewIngre3.getText().toString().trim();
        String RStpe =etNewStep.getText().toString().trim();
        recipeId = mReference.push().getKey();// creat an key
        uid= user.getUid();
        urlV="";
        urlI="";
        Rating="";
        RecipesInfo info = new RecipesInfo(RName, recipeId, uid, urlI, urlV, RStpe,Ingredient,Rating);
        mReference.child("Recipes").child(recipeId).setValue(info);

        //choose picture button
        iBPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });

        //choose Video button
        iBVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseVideo();
            }
        });

        //confirm button, add new data to database
        bNewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String RName = etNewRName.getText().toString().trim();//recipes name

                if (RName == null|| "".equals(RName)) {
                    Toast.makeText(NewRecipesActivity.this,"Please fill in the recipe name",Toast.LENGTH_SHORT).show();
                }
                else{
                    UpdateSheet();// update the sheet
                    Intent intent = new Intent();
                    intent.setClass(NewRecipesActivity.this,MenuActivity.class);
                    startActivity(intent);
                }

            }
        });

        // cancel button delete + refresh
        bNewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete the storage
                FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).removeValue();
                refresh();
            }
        });

        //go back -> 1) save data or not 2) Intent
        bNewGB.setOnClickListener(new View.OnClickListener() {
            //context = new Context();
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewRecipesActivity.this);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("Yes","YES!!!!!!!!!!!!!!!!!");//if yes,updata the data and save it
                        String RName = etNewRName.getText().toString().trim();//recipes name

                        if (RName == null|| "".equals(RName)) {
                            Toast.makeText(NewRecipesActivity.this,"Please fill in the recipe name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            UpdateSheet();// update the sheet
                            Intent intent = new Intent();
                            intent.setClass(NewRecipesActivity.this,MenuActivity.class);
                            startActivity(intent);
                        }
                        //Intent intent = new Intent(NewRecipesActivity ,SearchForOneActivity.class);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //Delete the sheet
                        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).removeValue();
                        //Intent intent = new Intent(NewRecipesActivity ,SearchForOneActivity.class);
                        //UpdateSheet();// update the sheet
                        Intent intent = new Intent();
                        intent.setClass(NewRecipesActivity.this,MenuActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setTitle("Message");
                builder.setMessage("Do You Want to Save this Recipe?");
                builder.show();

            }
        });



    }

    //refresh the page
    private void refresh() {
            finish();
            Intent intent = new Intent(NewRecipesActivity.this, NewRecipesActivity.class);
            startActivity(intent);
    }

    //upload Video to storage and update the database
    private void uploadV( ) {
        if (filePathV != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            ///////////// [START upload_get_download_url]
            final StorageReference ref = mStorage.child(uid + "/" + UUID.randomUUID().toString());

            //upload video
            UploadTask uploadTaskV=ref.putFile(filePathV); //upload image
            //ref.putFile(filePathI)
            Task<Uri> urlTaskV = ref.putFile(filePathV)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            //continue with the task to get the download URl
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUriV = task.getResult();
                                //update new URL
                                urlV = downloadUriV.toString();
                                HashMap<String, Object> result = new HashMap<>();//store urlI into data
                                result.put("video", urlV);
                                FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
                            }else{
                                //handle failures
                                dialog.dismiss();
                                Toast.makeText(NewRecipesActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            // [END upload_get_download_url]
//            ref.putFile(filePathV)//Video's Path in the phone
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            dialog.dismiss();
//                            Toast.makeText(NewRecipesActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                            //urlV = taskSnapshot.getUploadSessionUri().toString();//Video's URl
//
//
//                            HashMap<String, Object> result = new HashMap<>();//store urlV into data
//                            result.put("video", urlV);
//                            FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            Toast.makeText(NewRecipesActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            dialog.setMessage("Uploaded" + (int) progress);
//                        }
//                    });
        }
    }

    //upload Image to storage and update the database
    private void uploadI( ) {
        if(filePathI != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            ////////// [START upload_get_download_url]
            final StorageReference ref = mStorage.child(uid+"/"+ UUID.randomUUID().toString());
            //upload image
            UploadTask uploadTask=ref.putFile(filePathI); //upload image
            //ref.putFile(filePathI)
            Task<Uri> urlTask = ref.putFile(filePathI)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    //continue with the task to get the download URl
                    return ref.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                //update new URL
                                urlI = downloadUri.toString();
                            HashMap<String, Object> result = new HashMap<>();//store urlI into data
                            result.put("image", urlI);
                            FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
                            }else{
                                //handle failures
                            dialog.dismiss();
                            Toast.makeText(NewRecipesActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                // [END upload_get_download_url]
//////////////////////////////////20190109 Url


//            ref.putFile(filePathI)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            dialog.dismiss();
//                            Toast.makeText(NewRecipesActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
//                            urlI = taskSnapshot.getUploadSessionUri().toString();
//                            //urlI=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//
//                            HashMap<String, Object> result = new HashMap<>();//store urlI into data
//                            result.put("image", urlI);
//                            FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            Toast.makeText(NewRecipesActivity.this,"Failed" + e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                            dialog.setMessage("Uploaded" + (int)progress);
//                        }
//                    });
////////////////////////////////
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

    //update database
    private void UpdateSheet(){
        uploadI();
        uploadV();
        //ingredient information
        String Ingredient = etNewIngre.getText().toString().trim() + "," + etNewIngre2.getText().toString().trim() + "," + etNewIngre3.getText().toString().trim();// connect the ingredient
        String RName = etNewRName.getText().toString().trim();//recipes name
        String RStpe = etNewStep.getText().toString().trim();//recipes steps

        HashMap<String, Object> result = new HashMap<>();//store urlV into data
        result.put("recipeName", RName);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);//update dataS

        result.put("ingredient", Ingredient);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);

        result.put("steps", RStpe);
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipeId).updateChildren(result);
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
                vView1.setVideoURI(filePathV);
                MediaController mediaController = new MediaController(this);
                vView1.setMediaController(mediaController);
                mediaController.setAnchorView(vView1);

            } catch (Exception ex) {
                //e.printStackTrace();
            }
        }
    }

}

