package com.cookingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private Button buttonMyself;
    private Button buttonForAll;
    private Button button;
    private TextView signOut;
    private TextView recipeNameText;
    private String recipeName;
    private String searchForOne = "1";
    private String searchForAll = "0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonMyself = findViewById(R.id.buttonMyself);
        buttonForAll = findViewById(R.id.buttonForAll);
        button = findViewById(R.id.button);
        signOut = findViewById(R.id.textViewSignOut);
        recipeNameText = findViewById(R.id.recipeName);
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            String recipeNameBudle = bundle.getString("recipeName");
            recipeNameText.setText(recipeNameBudle);
        }


        buttonMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName = recipeNameText.getText().toString();
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("recipeName",recipeName);
                bundle.putString("isButton",searchForOne);
                intent.putExtras(bundle);//设置参数,""
                intent.setClass(MenuActivity.this, SearchForOneActivity.class);//从哪里跳到哪里
                startActivity(intent);
            }
        });

        buttonForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName = recipeNameText.getText().toString();
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("recipeName",recipeName);
                bundle.putString("isButton",searchForAll);
                intent.putExtras(bundle);//设置参数,""
                intent.setClass(MenuActivity.this, SearchForAllActivity.class);//从哪里跳到哪里
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MenuActivity.this, SaveImageActivity.class);//从哪里跳到哪里
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

    }
}
