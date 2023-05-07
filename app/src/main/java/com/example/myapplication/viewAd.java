package com.example.myapplication;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;


public class viewAd extends BaseActivity implements View.OnClickListener {
    private static final String Internet = "android.permission.INTERNET";
    private static final String READ = "android.permission.READ_CONTACTS";
    private static final String Read_External = "android.permission.READ_EXTERNAL_STORAGE";
    static final String TAG = "Read Data Activity";
    private static final String WRITE = "android.permission.WRITE_CONTACTS";
    private static final String WRIte_External = "android.permission.WRITE_EXTERNAL_STORAGE";
    Button btn_viewUserProfile, contact;
    Car car;
    FirebaseFirestore dataBase;
    EditText et_cat, et_des, et_km, et_man, et_mod, et_price, et_relevant, et_userEmail, et_yad, et_year;
    ImageView image2;
    Member seller;
    String sellerUser;
    FirebaseStorage storage;
    StorageReference storageReference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_view_ad);
        Intent intent = getIntent();
        this.dataBase = FirebaseFirestore.getInstance();
        FirebaseStorage instance = FirebaseStorage.getInstance();
        this.storage = instance;
        this.storageReference = instance.getReference();
        String carID2 = intent.getStringExtra("carID");
        String userID2 = intent.getStringExtra("userID");
        String category2 = intent.getStringExtra("category");
        String description2 = intent.getStringExtra("description");
        String km = intent.getStringExtra("km");
        String manufacturer2 = intent.getStringExtra("manufacturer");
        String model2 = intent.getStringExtra("model");
        String price2 = intent.getStringExtra("price");
        String owner2 = intent.getStringExtra("owner");
        String year2 = intent.getStringExtra("year");
        Boolean relevant2 = Boolean.valueOf(intent.getBooleanExtra("relevant", true));
        Intent intent2 = intent;
        String price3 = price2;
        String km2 = km;
        String km3 = carID2;
        String str = carID2;
        String description3 = description2;
        this.image2 = (ImageView) findViewById(R.id.image2);
        this.et_cat = (EditText) findViewById(R.id.et_cat);
        this.et_man = (EditText) findViewById(R.id.et_man);
        this.et_mod = (EditText) findViewById(R.id.et_mod);
        this.et_year = (EditText) findViewById(R.id.et_year);
        this.et_km = (EditText) findViewById(R.id.et_km);
        this.et_yad = (EditText) findViewById(R.id.et_yad);
        this.et_price = (EditText) findViewById(R.id.et_price);
        this.et_userEmail = (EditText) findViewById(R.id.et_userEmail);
        this.et_relevant = (EditText) findViewById(R.id.et_relevant);
        this.et_des = (EditText) findViewById(R.id.et_des);
        this.btn_viewUserProfile = (Button) findViewById(R.id.btn_viewUserProfile);
        Button button = (Button) findViewById(R.id.cont);
        this.contact = button;
        button.setOnClickListener(this);
        this.btn_viewUserProfile.setOnClickListener(this);
//        Button button2 = (Button) findViewById(R.id.btn_back);
//        this.btn_back = button2;
//        button2.setOnClickListener(this);
        this.et_cat.setText(category2);
        this.et_man.setText(manufacturer2);
        this.et_mod.setText(model2);
        this.et_year.setText(year2);
        this.et_km.setText(km2);
        this.et_yad.setText(owner2);
        this.et_price.setText(price3);
        this.et_userEmail.setText(userID2);
        this.et_relevant.setText(relevant2.toString());
        this.et_des.setText(description3);
        this.et_cat.setTextColor(getResources().getColor(R.color.white));
        this.et_man.setTextColor(getResources().getColor(R.color.white));
        this.et_mod.setTextColor(getResources().getColor(R.color.white));
        this.et_year.setTextColor(getResources().getColor(R.color.white));
        this.et_km.setTextColor(getResources().getColor(R.color.white));
        this.et_yad.setTextColor(getResources().getColor(R.color.white));
        this.et_price.setTextColor(getResources().getColor(R.color.white));
        this.et_userEmail.setTextColor(getResources().getColor(R.color.white));
        this.et_relevant.setTextColor(getResources().getColor(R.color.white));
        this.et_des.setTextColor(getResources().getColor(R.color.white));
        this.et_cat.setEnabled(false);
        this.et_man.setEnabled(false);
        this.et_mod.setEnabled(false);
        this.et_year.setEnabled(false);
        this.et_km.setEnabled(false);
        this.et_yad.setEnabled(false);
        this.et_price.setEnabled(false);
        this.et_userEmail.setEnabled(false);
        this.et_relevant.setEnabled(false);
        this.et_des.setEnabled(false);
        this.sellerUser = this.et_userEmail.getText().toString();
        this.seller = new Member();
        this.car = new Car(et_cat.getText().toString(), et_man.getText().toString(), et_mod.getText().toString(), et_year.getText().toString(), et_yad.getText().toString(), et_km.getText().toString(), et_price.getText().toString(), et_des.getText().toString(), carID2, et_userEmail.getText().toString(), Boolean.parseBoolean(et_relevant.getText().toString()));
        retrieveSeller();
        checkPermission(WRITE, 1);
        checkPermission(READ, 1);
        checkPermission(WRIte_External, 1);
        checkPermission(Read_External, 1);
        checkPermission(Internet, 1);
        downloadImage();
    }

    private void retrieveSeller() {
        String email = this.sellerUser;
        this.seller.setMemberEmail(email);
        this.dataBase.collection("user").whereEqualTo("Email", (Object) email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Iterator<QueryDocumentSnapshot> it = task.getResult().iterator();
                    while (it.hasNext()) {
                        QueryDocumentSnapshot document = it.next();
                        Log.d(viewAd.TAG, document.getId() + " => " + document.getData());
                        viewAd.this.seller.setMemberName(document.get("Name").toString());
                        viewAd.this.seller.setMemberPhone(document.get("Phone").toString());
                    }
                    return;
                }
                Toast.makeText(viewAd.this, "Failed To Read Data", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View v) {
        if (this.contact == v) {
            String DisplayName = this.seller.getMemberName();
            String MobileNumber = this.seller.getMemberPhone();
            String emailID = this.seller.getMemberEmail();
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue("account_type", (Object) null).withValue("account_name", (Object) null).build());
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data1", DisplayName).build());
            }
            if (MobileNumber != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/phone_v2").withValue("data1", MobileNumber).withValue("data2", 2).build());
            }
            if (emailID != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/email_v2").withValue("data1", emailID).withValue("data2", 2).build());
            }
            try {
                getContentResolver().applyBatch("com.android.contacts", ops);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to add seller to Contacts", Toast.LENGTH_SHORT).show();
            }
        } else if (v == this.btn_viewUserProfile) {
            Intent intent = new Intent(this, ViewProfile.class);
            intent.putExtra("SellerEmail", this.et_userEmail.getText().toString());
            startActivity(intent);
        }
//        else if (v == this.btn_back) {
//            finish();
//        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == -1) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    public void downloadImage() {
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images").child(this.car.getCarID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(viewAd.this.image2);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception exception) {
            }
        });
    }

}