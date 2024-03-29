package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends BaseActivity {
    Button btn_signup;

    /* renamed from: db */
    FirebaseFirestore db;
    EditText et_mail;
    EditText et_name;
    EditText et_pass;
    EditText et_phone;
    public double latitude;
    LocationTrack locationTrack;
    public double longitude;

    public FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    String mail;
    String name;
    String pass;
    String phone;
    TextView tv_login;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register);
        Premission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.longitude = 0.0d;
        this.latitude = 0.0d;
        new Intent();
        getIntent();
        this.mAuth = FirebaseAuth.getInstance();
        this.et_mail = (EditText) findViewById(R.id.et_mail);
        this.et_name = (EditText) findViewById(R.id.et_name);
        this.et_pass = (EditText) findViewById(R.id.et_pass);
        this.et_phone = (EditText) findViewById(R.id.et_phone);
        this.btn_signup = (Button) findViewById(R.id.btn_signup);
        this.tv_login = (TextView) findViewById(R.id.tv_login);
        this.btn_signup.setOnClickListener(this::onClick);
        this.tv_login.setOnClickListener(this::onClick);

    }

    public void onClick(View v) {
        this.mail = this.et_mail.getText().toString();
        this.pass = this.et_pass.getText().toString();
        this.name = this.et_name.getText().toString();
        String obj = this.et_phone.getText().toString();
        this.phone = obj;
        if (v == this.tv_login) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (v == this.btn_signup && validateInput(this.name, this.pass, this.mail, obj)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Do something with the latitude and longitude values
                    }
                });
            }
            signUp(this.mail, this.pass);
        }
    }

    private void signUp(String mail2, String pass2) {
        this.mAuth.createUserWithEmailAndPassword(mail2, pass2).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = Register.this.mAuth.getCurrentUser();
                    Toast.makeText(Register.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Register.this.createMember();
                    Register.this.finish();
                    startActivity(new Intent(Register.this, MainActivity.class));
                    return;
                }
                Toast.makeText(Register.this, "Sign Up failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMember() {
        Map<String, Object> user = new HashMap<>();
        user.put("Name", this.et_name.getText().toString());
        user.put("Email", this.et_mail.getText().toString());
        user.put("Phone", this.et_phone.getText().toString());
        user.put("latitude", Double.toString(this.latitude));
        user.put("longitude", Double.toString(this.longitude));
        user.put("UID", this.mAuth.getCurrentUser().getUid());
        user.put("Favorites", "");
        user.put("AdPosted", 0);
        this.db.collection("user").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Register.this, "Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void Premission() {
        checkPermission("android.permission.ACCESS_FINE_LOCATION", 1);
        checkPermission("android.permission.ACCESS_COARSE_LOCATION", 1);
        checkPermission("android.permission.ACCESS_BACKGROUND_LOCATION", 1);
        checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", 1);
        checkPermission("android.permission.READ_EXTERNAL_STORAGE", 1);
        checkPermission("android.permission.CAMERA", 1);
        checkPermission("android.permission.READ_CONTACTS", 1);
        checkPermission("android.permission.WRITE_CONTACTS", 1);
        checkPermission("android.permission.INTERNET", 1);
        checkPermission("android.permission.ACCESS_NETWORK_STATE", 1);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == -1) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    private boolean validateInput(String name2, String password, String email, String phone2) {
        int num = 0;
        if (name2.isEmpty()) {
            this.et_name.setError("Name is empty.");
            num = 0 + 1;
        }
        if (password.isEmpty()) {
            this.et_pass.setError("Password is empty.");
            num++;
        }
        if (email.isEmpty()) {
            this.et_mail.setError("Email is empty.");
            num++;
        }
        if (phone2.isEmpty()) {
            this.et_phone.setError("Phone is empty.");
            num++;
        }
        if (num != 0) {
            return false;
        }
        return true;
    }


}
