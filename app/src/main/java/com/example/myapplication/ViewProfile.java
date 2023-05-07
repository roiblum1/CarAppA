package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewProfile extends BaseActivity {
    private TextView textView;
    private EditText etUID;
    private ImageView imageView;
    public String latitude, longitude;
    private TextInputEditText etName, etUserEmail, etPhone;
    private Button btnLocation, btnAddContact;
    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        builder = new AlertDialog.Builder(this);
        Intent intent = getIntent();
        latitude = "0.0";
        longitude = "0.0";
        String EmailSeller = intent.getStringExtra("SellerEmail");
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        etName = (TextInputEditText) findViewById(R.id.et_name);
        etUserEmail = (TextInputEditText) findViewById(R.id.et_userEmail);
        etPhone = (TextInputEditText) findViewById(R.id.et_phone);
        etUID = (EditText) findViewById(R.id.et_UID);
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnAddContact = (Button) findViewById(R.id.btn_addContact);

        etName.setTextColor(getResources().getColor(R.color.white));
        etPhone.setTextColor(getResources().getColor(R.color.white));
        etUserEmail.setTextColor(getResources().getColor(R.color.white));

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DisplayName = etName.getText().toString();
                String MobileNumber = etPhone.getText().toString();
                String emailID = etUserEmail.getText().toString();
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
                    Toast.makeText(ViewProfile.this, "Failed to add seller to Contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == ViewProfile.this.btnLocation && ViewProfile.this.latitude != "0.0" && ViewProfile.this.longitude != "0.0") {
                    builder.setTitle("Open Location");
                    builder.setMessage("Where do you want to show the location ? ");
                    builder.setPositiveButton("Google Maps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openLocationInMaps(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        }
                    });
                    builder.setNegativeButton("In App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ViewProfile.this, ViewLocation.class);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("latitude", latitude);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        etName.setEnabled(false);
        etUserEmail.setEnabled(false);
        etPhone.setEnabled(false);
        if (EmailSeller != null)
            getSeller(EmailSeller);
    }


    private void getSeller(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String EmailSeller = document.get("Email").toString();
                        String PhoneSeller = document.get("Phone").toString();
                        String Name = document.get("Name").toString();
                        String uid = document.get("UID").toString();
                        latitude = document.get("latitude").toString();
                        longitude = document.get("longitude").toString();

                        if (uid != null) {
                            downloadImage(uid, imageView);
                        }
                        etName.setText(Name);
                        etPhone.setText(PhoneSeller);
                        etUserEmail.setText(EmailSeller);
                    }
                }
            }
        });
    }

    public void downloadImage(String Uid, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images/Avatars/");
        storageRef.child(Uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    private void openLocationInMaps(double latitude, double longitude) {
        // Create a URI with the coordinates
        String uri = String.format("geo:%f,%f", latitude, longitude);

        // Create an intent to launch the Google Maps app
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        // Verify that the Google Maps app is installed and launch it
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If the Google Maps app is not installed, display an error message
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }
}