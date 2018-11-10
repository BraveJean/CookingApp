package com.cookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText etUserName;
    private EditText etPassword;
    private EditText etCPassword;

    private Button bRegister;
    private Button bBack;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        bRegister = findViewById(R.id.bRegister);
        bBack = findViewById(R.id.bBack);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        bRegister.setOnClickListener(this);
        bBack.setOnClickListener(this);
    }

    //something happened after click register
    private void registerUser(){
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String Cpassword = etCPassword.getText().toString().trim();

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

        if (TextUtils.isEmpty( Cpassword )){
            // password is empty
            Toast.makeText(this,"Please Confirm Your Password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        //if valid are ok,
        //show progressBar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        if (Cpassword.equals(password))  {
            firebaseAuth.createUserWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("FirebaseAuth", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            //                        if (!task.isSuccessful()) {
                            //                            Log.d("FirebaseAuth", "onComplete: Failed=" + task.getException().getMessage()); //ADD THIS
                            //
                            //                            Toast.makeText(Register.this, "auth_failed",
                            //                                    Toast.LENGTH_SHORT).show();
                            //                        }

                            //Log.d("FirebaseAuth", "onComplete" + task.getException().getMessage());
                            if (task.isSuccessful()) {
                                Log.d("FirebaseAuth", "onComplete: Successfully"); //ADD THIS
                                //register succeed and logged in
                                //start profile activity here
                                //now disply
                                Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                saveUserInformation();
                            } else {
                                Toast.makeText(Register.this, "Registered problem, Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(Register.this, "Please enter your Password and confirm it again.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserInformation() {
        String name = etUserName.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, pass);
        FirebaseUser user =firebaseAuth.getCurrentUser();
        databaseReference.child("user").child(user.getUid()).setValue(userInformation);
        Toast.makeText(this,"information saved...",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        if(v == bRegister){
            registerUser();

        }
        if(v == bBack){
            //will open login activity
            Intent intent = new Intent(Register.this, LoginActivety.class);
            startActivity(intent);
        }
    }
}




