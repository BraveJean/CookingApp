package com.cookingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewRecipesActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etNewRName;
    private EditText etNewIngre;
    private EditText etNewStep;
    private DatabaseReference databaseReference;

    private Button bNewAdd;
    private Button bNewCancel;
    private Button bNewGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_new);
        etNewRName = findViewById(R.id.etNewRName);
        etNewIngre = findViewById(R.id.etNewIngre);
        etNewStep =findViewById(R.id.etNewStep);

        bNewAdd = findViewById(R.id.bNewAdd);
        bNewCancel = findViewById(R.id.bNewCancel);
        bNewGB =findViewById(R.id.bNewGB);

        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        bNewAdd.setOnClickListener(this);
        bNewCancel.setOnClickListener(this);
        bNewGB.setOnClickListener(this);
    }

    private void saveRecipesInfo() {
        String Ingredient = etNewIngre.getText().toString().trim();
        String RName = etNewRName.getText().toString().trim();
        String RStpe =etNewStep.getText().toString().trim();
//        private String recipeName;
//        private String recipeId;
//        private String userId;
//        private String userName;
//        private String image;
//        private String video;
//        private String steps;
//        private String ingredient;

        UserInformation userInformation = new UserInformation(Ingredient, RName, RStpe);
        databaseReference.child("Recipes").child(Recipes.getUid()).setValue(userInformation);
        Toast.makeText(this,"Modify Saved...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if(v == bNewAdd) {
            //submit
        }

        if(v == bNewCancel) {
            //cancel
        }

        if(v == bNewGB) {
            //cancel
        }

        }
    }
}
