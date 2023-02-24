package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final String Email = "emailKey";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Pass = "passKey";
    Button btn_login;
    EditText et_mail;
    EditText et_pass;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    TextView tv_create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tv_create = (TextView) findViewById(R.id.tv_create);
        this.et_mail = (EditText) findViewById(R.id.et_mail);
        this.et_pass = (EditText) findViewById(R.id.et_pass);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.btn_login.setOnClickListener(this::onClick);
        this.tv_create.setOnClickListener(this::onClick);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString(Email,null)!=null)
            et_mail.setText(sharedpreferences.getString(Email,null));
        if (sharedpreferences.getString(Pass,null)!=null)
            et_pass.setText(sharedpreferences.getString(Pass,null));
        mAuth = FirebaseAuth.getInstance();
        this.progressDialog = new ProgressDialog(this);

        Intent intent = new Intent();
        getIntent();
    }
    public void onClick(View v)
    {
        if (v == this.btn_login)
        {
            if (!this.et_mail.getText().toString().equals("") && !this.et_pass.getText().toString().equals(""))
            {
                if (et_mail.getText().toString().equals("admin")&&et_pass.getText().toString().equals("admin"))
                    startActivity(new Intent(this, AdminPage.class));
                else
                    logIn(this.et_mail.getText().toString(), this.et_pass.getText().toString());
            }
        } else if (v == this.tv_create) {
            startActivity(new Intent(this, Register.class));
        }
    }


    private void logIn(String mail, String pass) {
        this.progressDialog.setMessage("Verificating...");
        this.progressDialog.show();
        this.mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = MainActivity.this.mAuth.getCurrentUser();
                    SharedPreferences.Editor editor = MainActivity.this.sharedpreferences.edit();
                    editor.putString(MainActivity.Pass, MainActivity.this.et_pass.getText().toString());
                    editor.putString(MainActivity.Email, MainActivity.this.et_mail.getText().toString());
                    editor.commit();
                    startService(new Intent(MainActivity.this, MyService.class));
                    MainActivity.this.progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                    MainActivity.this.intent();
                    return;
                }
                MainActivity.this.progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void intent() {
        startActivity(new Intent(this, SplashScreen.class));
    }

    public boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            this.et_mail.setError("Email field is empty.");
            if (password.isEmpty()) {
                this.et_pass.setError("Password is empty.");
            }
            return false;
        } else if (!password.isEmpty()) {
            return true;
        } else {
            this.et_pass.setError("Password is empty.");
            if (email.isEmpty()) {
                this.et_pass.setError("Email field is empty.");
            }
            return false;
        }
    }


}