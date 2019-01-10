package com.cookingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {


    Context context;
    ArrayList<RecipesInfo> recipesInfos;
    String flag;


    public MyAdapter(Context c, ArrayList<RecipesInfo> r) {
        context = c;
        recipesInfos = r;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipeview, viewGroup, false);
        context = viewGroup.getContext();
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.userName.setText(recipesInfos.get(i).getUserName());
        myViewHolder.recipeName.setText(recipesInfos.get(i).getRecipeName());
        myViewHolder.recipeId.setText(recipesInfos.get(i).getRecipeId());
        String path = recipesInfos.get(i).getImage();
        if(path != null && !"".equals(path)) {
            Uri uri = Uri.parse(path);
            Picasso.get().load(uri).fit().into(myViewHolder.imageRecipe);
        }
    }

    @Override
    public int getItemCount() {
        return recipesInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView recipeName;
        TextView userName;
        TextView recipeId;
        ImageView imageRecipe;
        Button checkDetail;
        Button deleteButton;
        DatabaseReference mReference;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeName = itemView.findViewById(R.id.recipeNameText);
            userName = itemView.findViewById(R.id.userName);
            imageRecipe = itemView.findViewById(R.id.image);
            recipeId = itemView.findViewById(R.id.recipeId);
            checkDetail = itemView.findViewById(R.id.checkDetial);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            if("0".equals(flag)){
                deleteButton.setVisibility(itemView.VISIBLE);
            }else{
                deleteButton.setVisibility(itemView.INVISIBLE);
            }
            checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String recipeIdString = recipeId.getText().toString();
                    mReference = FirebaseDatabase.getInstance().getReference();
                    Query query = mReference.child("Recipes").orderByKey().equalTo(recipeIdString);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                final RecipesInfo info = dataSnapshot1.getValue(RecipesInfo.class);
                                final String userId = info.getUserId();
                                Query query = mReference.child("user").child(userId);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                                        info.setUserName(userInfo.getUserName());
                                        Intent intent = new Intent(context ,RecipeDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag",flag);
                                        bundle.putString("recipeId",info.getRecipeId());
                                        bundle.putString("userId",userId);
                                        bundle.putString("recipeName", info.getRecipeName());
                                        bundle.putString("imageUrl", info.getImage());
                                        bundle.putString("videoUrl",info.getVideo());
                                        bundle.putString("ingredient",info.getIngredient());
                                        bundle.putString("steps",info.getSteps());
                                        bundle.putString("userName",info.getUserName());
                                        bundle.putString("rating",info.getRating());
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Intent intent = new Intent(context ,RecipeDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag",flag);
                                        bundle.putString("error","1");
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String repiceIdString = recipeId.getText().toString();
                            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child(repiceIdString);
                            mReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(context ,SearchForOneActivity.class);
                                        Bundle bundle = new Bundle();
                                        String name = recipeName.getText().toString();
                                        bundle.putString("recipeName", name);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    }
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setTitle("Message");
                    builder.setMessage("Do you want to delete this recipe?");
                    builder.show();

                }
            });
        }
    }

}
