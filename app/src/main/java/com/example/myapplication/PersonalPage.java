package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PersonalPage extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Button btn_allAd;
    Button btn_newAd;
    Button btn_ViewProfile;
    FirebaseUser currentuser;
    ImageView iv_car;
    RelativeLayout l1;
    ListView lv_personal;
    Task<QuerySnapshot> memberNameRef;
    DatabaseReference carRef;
    String s;
    TextView tv_price;
    TextView tv_title;
    TextView tv_titleO;
    ArrayList<Car> carList;
    CarAdapter carAdapter;
    FirebaseFirestore db;
    static final String TAG = "Read Data Activity";
    static final String TAG2 = "Read Data2 Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        bottomNavigation();
        l1 = (RelativeLayout) findViewById(R.id.l1);
        lv_personal = findViewById(R.id.lv_personal);
        lv_personal.setOnItemClickListener(this);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_titleO = (TextView) findViewById(R.id.tv_titleO);
        iv_car = (ImageView) findViewById(R.id.iv_car);
//        btn_allAd = (Button) findViewById(R.id.btn_allAd);
//        btn_newAd = (Button) findViewById(R.id.btn_newAd);
//        btn_ViewProfile = findViewById(R.id.btn_ViewProfile);
        tv_title = (TextView) findViewById(R.id.tv_title);
        Intent intent = new Intent();
        getIntent();
//        btn_newAd.setOnClickListener(this);
//        btn_allAd.setOnClickListener(this);
//        btn_ViewProfile.setOnClickListener(this);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
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
                        Toast.makeText(PersonalPage.this, "Hello " +Name, Toast.LENGTH_SHORT).show();
                    }



                }else {

                    Toast.makeText(PersonalPage.this,"Failed To Read Data",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void retriveCar ()//retrive current user car
    {
//        carRef = FirebaseDatabase.getInstance().getReference("cars");
//        carRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                carList = new ArrayList<>();
//                for(DataSnapshot data: dataSnapshot.getChildren())
//                {
//                Car c = data.getValue(Car.class);
//                if(currentuser.getUid().equals(c.getUserID()))
//                {
//                    carList.add(c);
//                }
//                }
//                carAdapter = new CarAdapter(PersonalPage.this,0,0,carList);
//                lv_personal.setAdapter(carAdapter);
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        String email = currentuser.getEmail();
        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(PersonalPage.this, "win TO fa", Toast.LENGTH_SHORT).show();

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
                        Car car = new Car( category,  manufacturer,  model,  year,  owner,  km,  price,  description,  carID,  userID,  relevant);
                        carList.add(car);
                    }
                    carAdapter = new CarAdapter(PersonalPage.this,0,0,carList);
                    lv_personal.setAdapter(carAdapter);
                }
                else
                {
                    Toast.makeText(PersonalPage.this, "Faild TO fa", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    protected void onStart() {
        super.onStart();
        retriveMemberName();
        retriveCar();
    }

    public void onClick(View v) {
        if (v == this.btn_newAd) {
            Intent intent = new Intent(this , NewAdActivity.class);
            intent.putExtra("num",carList.size());
            startActivity(intent);
        }
        else
        if(v==btn_allAd)
        {
            startActivity(new Intent(this, AllAdActivity.class));
        }
        else if (v  == btn_ViewProfile)
        {
            Intent intent = new Intent(this , MainActivity2.class);
            intent.putExtra("num",carList.size());
            startActivity(intent);

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        Car c = carAdapter.getItem(position);
//        c.setRelevant(false);
        // retriveCarRelevant(c);
        Intent intent = new Intent(this, EditAd.class);
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
        intent.putExtra("num",carList.size());
        startActivity(intent);
//        view.setBackgroundColor(0x0000FF00);
//        carAdapter.notifyDataSetChanged();



    }
    private void retriveCarRelevant (Car c)//retrive current user car
    {
        String email = currentuser.getEmail();
        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        bottomNavigationView.setSelectedItemId(R.id.personal_page);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}