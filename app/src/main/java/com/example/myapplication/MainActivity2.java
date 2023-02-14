package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    public TextView textView;
    public ImageView imageView;
    public Button buttonBack;
    public EditText etName;
    public EditText etUserEmail;
    public EditText etPhone;
    public EditText et_numOfCars;
    public Button buttonEdit;
    public double latitude;
    public double longitude;
    FirebaseUser currentuser;
    FirebaseFirestore db;
    static final String TAG = "Read Data Activity";
    public int num;
    public int carNum;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String WRIte_External = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String Read_External = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String Internat = Manifest.permission.INTERNET;
    public TextView text_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        this.latitude = 0.0;
        this.longitude = 0.0;
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        etName = (EditText) findViewById(R.id.et_name);
        etUserEmail = (EditText) findViewById(R.id.et_userEmail);
        etPhone = (EditText) findViewById(R.id.et_phone);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        et_numOfCars= findViewById(R.id.et_numOfCars);
        text_location = findViewById(R.id.text_location);
        text_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == MainActivity2.this.text_location && MainActivity2.this.latitude != 0.0 && MainActivity2.this.longitude != 0.0) {
                    MainActivity2.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr=" + MainActivity2.this.latitude + "," + MainActivity2.this.longitude)));
                }
            }
        });
        etName.setTextColor(getResources().getColor(R.color.white));
        etPhone.setTextColor(getResources().getColor(R.color.white));
        etUserEmail.setTextColor(getResources().getColor(R.color.white));
        et_numOfCars.setTextColor(getResources().getColor(R.color.white));
        etName.setEnabled(false);
        etUserEmail.setEnabled(false);
        etPhone.setEnabled(false);

        buttonBack.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);

        retriveMemberName();
        Intent intent = getIntent();
        num = intent.getIntExtra("num",0);
        et_numOfCars.setText("" +num);

        checkPermission(WRIte_External,REQUEST_CODE_ASK_PERMISSIONS);
        checkPermission(Read_External,REQUEST_CODE_ASK_PERMISSIONS);
        checkPermission(Internat,REQUEST_CODE_ASK_PERMISSIONS);
        downloadImage(imageView);

    }


    @Override
    public void onClick(View v) {
        if (v == buttonBack)
        {
            startActivity(new Intent(this , PersonalPage.class));
        }
        else if ( v == buttonEdit)
        {
            startActivity(new Intent(this , EditProfileUser.class));
        }
    }


    private void retriveMemberName()//retrive member name
    {
        String email = currentuser.getEmail();
        db.collection("user").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String Name = document.get("Name").toString();
                        String Email = document.get("Email").toString();
                        String Phone = document.get("Phone").toString();
                        String lat = document.get("latitude").toString();
                        String lon = document.get("longitude").toString();
                        MainActivity2.this.latitude = Double.parseDouble(lat);
                        MainActivity2.this.longitude = Double.parseDouble(lon);
                        MainActivity2.this.etName.setText(Name);
                        MainActivity2.this.etUserEmail.setText(Email);
                        MainActivity2.this.etPhone.setText(Phone);
                    }
                }
                else
                {

                    Toast.makeText(MainActivity2.this,"Failed To Read Data",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void downloadImage (ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images/Avatars/");
        storageRef.child(currentuser.getUid().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri.toString()).into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.nopic);
            }
        });
    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity2.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(viewAd.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    
}
