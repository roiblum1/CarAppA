package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<Car> carList = new ArrayList<Car>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onViewReady(savedInstanceState, getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menua, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.LoginPage:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.Register:
                startActivity(new Intent(this, Register.class));
                return true;
            case R.id.AllAD:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, AllAdActivity.class));
                return true;
            case R.id.personal_page:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, PersonalPage.class));
                return true;
            case R.id.View_Profile:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, ViewYourProfile.class));
                return true;
            case R.id.SrartMusic:
                startService(new Intent(this, MyService.class));
                return true;
            case R.id.StopMusic:
                stopService(new Intent(this, MyService.class));
                return true;
            case R.id.About_Us:
                startActivity(new Intent(this, About_us.class));
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.logout:
                this.auth = FirebaseAuth.getInstance();
                this.auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //TODO:
    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        //To be used by child activities
    }
    protected void showToast(String mToastMsg) {
        Toast.makeText(this, mToastMsg, Toast.LENGTH_LONG).show();
    }

    protected void bottomNavigation ( )
    {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.NewAD:
                        Intent intent = new Intent(getApplicationContext() , NewAdActivity.class);
                        intent.putExtra("num",carList.size());
                        startActivity(intent);
                        return true;
                    case R.id.AllAD:
                        startActivity(new Intent(getApplicationContext(), AllAdActivity.class));
                        return true;
                    case R.id.View_Profile:
                        Intent intent2 = new Intent(getApplicationContext() , ViewYourProfile.class);
                        intent2.putExtra("num",carList.size());
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }

    public void deleteImageUser (String UID)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/Avatars/");
        desertRef.child(UID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "File deleted from storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteImageCar (String carID)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/");
        desertRef.child(carID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("File deleted from storage");
            }
        });
    }
}
