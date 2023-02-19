package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllAdActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAGMENU = "mytag";
    Button btn_back;
    String Favorite;
    ArrayList<Car> carList;
    CarAdapter carAdapter;
    ListView lv_all;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_all_ad);
        lv_all = findViewById(R.id.lv_all);
        lv_all.setOnItemClickListener(this);
        bottomNavigation();
        String[] CarsLogo = {"Favorites","Nissan", "Mustang", "Toyota", "Volvo", "BMW", "Honda", "Mercedes", "Jeep", "Infinity", "Subaru"};
        List<String> stringList = Arrays.asList(CarsLogo);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        TopNavigationAdapter adapter2= new TopNavigationAdapter(stringList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter2);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(AllAdActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String str = adapter2.getName(position);

                        getFavorite();
                       ArrayList<Car> cars2 = new ArrayList<Car>();
                        for (int i = 0; i < carList.size(); i++) {
                            if (Favorite!=null && str == "Favorites")
                            {
                                Toast.makeText(AllAdActivity.this, Favorite, Toast.LENGTH_SHORT).show();
                                if (isInString(carList.get(i).getCarID().toString(),Favorite))
                                    cars2.add(carList.get(i));
                            }
                            else
                            {
                                if (carList.get(i).getManufacturer().toString().equals(str)) {
                                    cars2.add(carList.get(i));
                                }
                            }
                        }
                        carAdapter = new CarAdapter(AllAdActivity.this,0,0,cars2);

                        lv_all.setAdapter(carAdapter);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        db = FirebaseFirestore.getInstance();
        retriveAllCar();
    }


    private void retriveAllCar ()//retrive all cars
    {
        db.collection("Cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                carList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult())
                {
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
                carAdapter = new CarAdapter(AllAdActivity.this,0,0,carList);
                lv_all.setAdapter(carAdapter);
            }
        });
    }


    private void retriveCarRelevant (Car c)//retrive current user car
    {
        db.collection("Cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                carList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if (c.getCarID().equals(document.get("carID").toString()))
                    {
                        if (document.get("relevant").toString().equals("true"))
                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", false);
                        else
                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", true);

                    }
                }
            }
        });



    }

    public void getFavorite (){
        db.collection("user").whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        Favorite = document.get("Favorites").toString();
                    }
                }
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Car c = carAdapter.getItem(position);
        view.setBackgroundColor(0x0000FF00);
        Intent intent = new Intent(this, viewAd.class);
        intent.putExtra("carID",c.getCarID());
        intent.putExtra("userID", c.getUserID());
        intent.putExtra("category", c.getCategory());
        intent.putExtra("description", c.getDescription());
        intent.putExtra("km", c.getKm());
        intent.putExtra("manufacturer", c.getManufacturer());
        intent.putExtra("model", c.getModel());
        intent.putExtra("price", c.getPrice());
        intent.putExtra("owner", c.getOwner());
        intent.putExtra("year", c.getYear());
        intent.putExtra("relevant",c.isRelevant());
        startActivity(intent);
    }

    public static boolean isInString(String str, String str2) {
        String[] arr = str2.split(", ");
        for (String s : arr) {
            if (s.equals(str)) {
                return true;

            }
        }
        if (str.equals(str2))
            return true;
        return false;
    }

    protected void bottomNavigation ( )
    {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ArrayList<Car> carArrayList = new ArrayList<Car>();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
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
                        carArrayList.add(car);
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Faild TO fa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.AllAD);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.NewAD:
                        Intent intent = new Intent(getApplicationContext() , NewAdActivity.class);
                        intent.putExtra("num",carArrayList.size());
                        startActivity(intent);
                        return true;
                    case R.id.AllAD:
                        startActivity(new Intent(getApplicationContext(), AllAdActivity.class));
                        return true;
                    case R.id.personal_page:
                        startActivity(new Intent(getApplicationContext(), PersonalPage.class));
                        return true;
                    case R.id.View_Profile:
                        Intent intent2 = new Intent(getApplicationContext() , MainActivity2.class);
                        intent2.putExtra("num",carArrayList.size());
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }
}

