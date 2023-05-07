package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PersonalPage extends BaseActivity implements AdapterView.OnItemClickListener {
    FirebaseUser currentUser;
    ListView listView_Personal;
    TextView tv_title;
    ArrayList<Car> carList;
    CarAdapter carAdapter;
    FirebaseFirestore db;
    static final String TAG = "Read Data Activity";
    static final String TAG2 = "Read Data2 Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        bottomNavigation();
        listView_Personal = findViewById(R.id.lv_personal);
        listView_Personal.setOnItemClickListener(this);
//        btn_allAd = (Button) findViewById(R.id.btn_allAd);
//        btn_newAd = (Button) findViewById(R.id.btn_newAd);
//        btn_ViewProfile = findViewById(R.id.btn_ViewProfile);
        tv_title = (TextView) findViewById(R.id.tv_title);
        Intent intent = new Intent();
        getIntent();
//        btn_newAd.setOnClickListener(this);
//        btn_allAd.setOnClickListener(this);
//        btn_ViewProfile.setOnClickListener(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    protected void onStart() {
        super.onStart();
        retrieveMemberDetails();
        retrieveCars();
    }

    private void retrieveMemberDetails() {
        String email = currentUser.getEmail();
        db.collection("user").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String Name = document.get("Name").toString();
                        Toast.makeText(PersonalPage.this, "Hello " + Name, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(PersonalPage.this, "Failed To Read Data", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void retrieveCars() {
        String email = currentUser.getEmail();
        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(PersonalPage.this, "win TO fa", Toast.LENGTH_SHORT).show();
                    carList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG2, document.getId() + " => " + document.getData());
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
                        Car car = new Car(category, manufacturer, model, year, owner, km, price, description, carID, userID, relevant);
                        carList.add(car);
                    }
                    carAdapter = new CarAdapter(PersonalPage.this, 0, 0, carList);
                    listView_Personal.setAdapter(carAdapter);
                } else {
                    Toast.makeText(PersonalPage.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Car c = carAdapter.getItem(position);
        Intent intent = new Intent(this, EditAd.class);
        intent.putExtra("carID", c.getCarID());
        intent.putExtra("userID", c.getUserID());
        intent.putExtra("category", c.getCategory());
        intent.putExtra("description", c.getDescription());
        intent.putExtra("km", c.getKm());
        intent.putExtra("manufacturer", c.getManufacturer());
        intent.putExtra("model", c.getModel());
        intent.putExtra("price", c.getPrice());
        intent.putExtra("owner", c.getOwner());
        intent.putExtra("year", c.getYear());
        intent.putExtra("relevant", c.isRelevant());
        intent.putExtra("num", carList.size());
        startActivity(intent);
//        view.setBackgroundColor(0x0000FF00);
//        carAdapter.notifyDataSetChanged();

    }
//    private void retriveCarRelevant (Car c)//retrive current user car
//    {
//        String email = currentUser.getEmail();
//        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                carList = new ArrayList<>();
//                for (QueryDocumentSnapshot document : task.getResult())
//                {
//                    if (c.getCarID().equals(document.get("carID").toString()))
//                    {
//                        if (document.get("relevant").toString().equals("true"))
//                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", false);
//                        else
//                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", true);
//
//                    }
//                }
//            }
//        });
//    }

    protected void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ArrayList<Car> carArrayList = new ArrayList<Car>();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
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
                        Car car = new Car(category, manufacturer, model, year, owner, km, price, description, carID, userID, relevant);
                        carArrayList.add(car);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.personal_page);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.NewAD:
                        Intent intent = new Intent(getApplicationContext(), NewAdActivity.class);
                        intent.putExtra("num", carArrayList.size());
                        startActivity(intent);
                        return true;
                    case R.id.AllAD:
                        startActivity(new Intent(getApplicationContext(), AllAdActivity.class));
                        return true;
                    case R.id.personal_page:
                        startActivity(new Intent(getApplicationContext(), PersonalPage.class));
                        return true;
                    case R.id.View_Profile:
                        Intent intent2 = new Intent(getApplicationContext(), ViewYourProfile.class);
                        intent2.putExtra("num", carArrayList.size());
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }


}