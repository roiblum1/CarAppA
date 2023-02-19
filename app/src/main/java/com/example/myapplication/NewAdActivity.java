package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewAdActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText et_cat, et_man,et_mod,et_year,et_km,et_yad,et_price,et_des;
    Button btn_share,btn_addImage,btn_ViewImage,btn_back;
    FirebaseDatabase database;
    FirebaseUser currentuser;
    Uri selectedImageUri;
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage ;
    public int num;
    ImageView imageView;
    AlertDialog.Builder builder;
    Spinner spinner;
    private static final String CAMARA = "android.permission.CAMERA";
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int SELECT_PICTURE = 200;
    private static final String WRi = "android.permission.WRITE_EXTERNAL_STORAGE";
    String[] CarsLogo = {"Nissan", "Mustang", "Toyota", "Volvo", "BMW", "Honda", "Mercedes", "Jeep", "Infinity", "Subaru"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        builder = new AlertDialog.Builder(this);
        bottomNavigation();
        et_cat= findViewById(R.id.et_cat);
        et_des = findViewById(R.id.et_des);
        et_km = findViewById(R.id.et_km);
        spinner = findViewById(R.id.spinner);
        et_mod =findViewById(R.id.et_mod);
        et_price =findViewById(R.id.et_price);
        et_yad =findViewById(R.id.et_yad);
        et_year =findViewById(R.id.et_year);
        btn_addImage =findViewById(R.id.btn_addImage);
        btn_share =findViewById(R.id.btn_share);
        btn_back = findViewById(R.id.btn_back);
        //btn_ViewImage = findViewById(R.id.btn_ViewImage);
        imageView = findViewById(R.id.imagev);

        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,CarsLogo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(this);
        btn_share.setOnClickListener(this);
        btn_addImage.setOnClickListener(this);
        btn_back.setOnClickListener(this);
//        btn_ViewImage.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        num = intent.getIntExtra("num",0);

        checkPermission(CAMARA, 1);
        checkPermission(WRi, 1);
    }


    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == -1) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.personal_page:
                startActivity(new Intent(this, PersonalPage.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==btn_share)
        {
            String email = currentuser.getEmail();
            Map<String, Object> Car = new HashMap<>();
            Car.put("carID", currentuser.getUid()+(num+1));
            Car.put("category", et_cat.getText().toString());
            Car.put("description", et_des.getText().toString());
            Car.put("km", et_km.getText().toString());
            Car.put("manufacturer", spinner.getSelectedItem().toString());
            Car.put("model", et_mod.getText().toString());
            Car.put("owner", et_yad.getText().toString());
            Car.put("price", et_price.getText().toString());
            Car.put("year", et_year.getText().toString());
            Car.put("userEmail", currentuser.getEmail());
            Car.put("relevant", true);
            imageView = findViewById(R.id.imagev);
            AddOneAdPosted();
            db.collection("Cars").add(Car).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
            {
                @Override
                public void onSuccess(DocumentReference documentReference)
                {
                    Toast.makeText(NewAdActivity.this, "Successful Post", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull @NotNull Exception e)
                {

                    Toast.makeText(NewAdActivity.this, "Failed Post", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (btn_addImage==view)
        {

            build();
        }
        else if (view == btn_back )
        {
            deleteImage();
            startActivity(new Intent(this,PersonalPage.class));
        }
    }

    @Override
    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 200:
                    if (requestCode == 200) {
                        Uri data2 = data.getData();
                        this.selectedImageUri = data2;
                        if (data2 != null) {
                            this.imageView.setImageURI(data2);
                            uploadImage(this.selectedImageUri);
                            return;
                        }
                        return;
                    }
                    return;
                case CAMERA_REQUEST /*1888*/:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    this.selectedImageUri = tempUri;
                    uploadImage(tempUri);
                    this.imageView.setImageBitmap(photo);
                    return;
                default:
                    return;
            }
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        try {
            return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", (String) null));
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
    }


    public void uploadImage (Uri image)
    {
        StorageReference imageRef = storageReference.child("images/"+currentuser.getUid()+(num+1));
        UploadTask uploadTask = imageRef.putFile(image);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(NewAdActivity.this, "Fail to Upload to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteImage ()
    {
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/");
        desertRef.child(currentuser.getUid()+(num+1)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(NewAdActivity.this, "File Delete From ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void build() {
        this.builder.setMessage((CharSequence) "Where do you want to take the picture from ?").setCancelable(false).setPositiveButton((CharSequence) "Gallery", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NewAdActivity.this.imageChooser();
            }
        }).setNegativeButton((CharSequence) "Camara", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NewAdActivity.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), NewAdActivity.CAMERA_REQUEST);
            }
        });
        AlertDialog alert = this.builder.create();
        alert.setTitle("Select");
        alert.show();
    }

    public void AddOneAdPosted ()
    {
        db.collection("user").whereEqualTo("Email",currentuser.getEmail() ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    db.collection("user").document(document.getReference().getPath().substring(5)).update("AdPosted",Integer.parseInt(document.get("AdPosted").toString())+1);
                }
            }
        });
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this , PersonalPage.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(),CarsLogo[i] , Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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