package com.cookingapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText etUserName;
    private EditText etPassword;
    private Button bRegister;
    private Button bBack;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        bRegister = findViewById(R.id.bRegister);
        bBack = findViewById(R.id.bBack);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        bRegister.setOnClickListener(this);
        bBack.setOnClickListener(this);
    }

    //something happened after click register
    private void registerUser(){
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty( userName )){
            // userName is empty
            Toast.makeText(this,"Please enter a UserName", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if (TextUtils.isEmpty( password )){
            // password is empty
            Toast.makeText(this,"Please enter a Password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        //if valid are ok,
        //show progressBar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword( userName, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //register succeed and logged in
                            //start profile activity here
                            //now disply
                            Toast.makeText(Register.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(Register.this,"Registered problem, Please try again",Toast.LENGTH_LONG).show();
                    }
                });
    }



    @Override
    public void onClick(View v) {
        if(v == bRegister){
            registerUser();
        }
        if(v == bBack){
            //will open login activity
        }
    }
}




