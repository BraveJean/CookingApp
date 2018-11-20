package com.cookingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {


    Context context;
    ArrayList<RecipesInfo> recipesInfos;


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
        myViewHolder.repiceName.setText(recipesInfos.get(i).getRecipeName());
        myViewHolder.repiceId.setText(recipesInfos.get(i).getRecipeId());
        String path = recipesInfos.get(i).getImage();
        if(path != null && !"".equals(path)) {
            Picasso.get().load(path).into(myViewHolder.imageRepice);
        }
    }

    @Override
    public int getItemCount() {
        return recipesInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView repiceName;
        TextView userName;
        TextView repiceId;
        ImageView imageRepice;
        Button checkDetail;
        Button deleteButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            repiceName = itemView.findViewById(R.id.recipesNameText);
            userName = itemView.findViewById(R.id.userName);
            imageRepice = itemView.findViewById(R.id.image);
            repiceId = itemView.findViewById(R.id.recipeId);
            checkDetail = itemView.findViewById(R.id.checkDetial);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String repiceIdString = repiceId.getText().toString();
                    Intent intent = new Intent(context ,RecipeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("recipeName",repiceIdString);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("Yes","YES!!!!!!!!!!!!!!!!!");
                            String repiceIdString = repiceId.getText().toString();
                            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child(repiceIdString);
                            mReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

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
