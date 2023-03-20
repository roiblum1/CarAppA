package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
{
    FirebaseFirestore db;
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
                    startActivity(new Intent(this, MainActivity2.class));
                return true;
            case R.id.SrartMusic:
                startService(new Intent(this, MyService.class));
                return true;
            case R.id.StopMusic:
                stopService(new Intent(this, MyService.class));
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
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
                        Intent intent2 = new Intent(getApplicationContext() , MainActivity2.class);
                        intent2.putExtra("num",carList.size());
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }

    public void getAdPosterd ()
    {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        db = FirebaseFirestore.getInstance();
        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    carList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG2", document.getId() + " => " + document.getData());
                        String carID = document.get("carID").toString();
                        String category = document.get("category").toString();
                        String description = document.get("description").toString();
                        String km = document.get("km").toString();
                        String manufacturer = document.get("manufacturer").toString();
                        String model = document.get("model").toString();
                        String owner = document.get("owner").toString();
                        String price = document.get("price").toString();
                        boolean relevant = (boolean) document.get("relevant");
                        String userID = document.get("userEmail").toString();
                        String year = document.get("year").toString();
                        Car car = new Car( category,  manufacturer,  model,  year,  owner,  km,  price,  description,  carID,  userID,  relevant);
                        carList.add(car);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Faild TO fa", Toast.LENGTH_SHORT).show();
                }
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
                Toast.makeText(getApplicationContext(), "File Delete From ", Toast.LENGTH_SHORT).show();
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
                showToast("Failed to delete from storage");
            }
        });
    }
}
