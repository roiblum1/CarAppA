package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class EditProfileUser extends BaseActivity implements View.OnClickListener {
     TextView textView;
     ImageView imageView;
     Button btnChangeAvatar;
     EditText etName;
     EditText etUserEmail;
     EditText etPhone;
     Button buttonBack;
     Button btnDelete;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 200;
    Button btn_location;
    AlertDialog.Builder builder;
    StorageReference storageReference;
    FirebaseStorage storage ;
    Uri selectedImageUri;
    FirebaseUser currentuser;
    FirebaseDatabase database;
    FirebaseFirestore db;
    public int num;
    public Boolean aBoolean;
    static final String TAG = "Read Data Activity";


    public double latitude;
    LocationTrack locationTrack;
    public double longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.btn_location = (Button) findViewById(R.id.btn_location);
        this.builder = new AlertDialog.Builder(this);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnChangeAvatar = (Button) findViewById(R.id.btnChangeAvatar);
        etName = (EditText) findViewById(R.id.et_name);
        etUserEmail = (EditText) findViewById(R.id.et_userEmail);
        etPhone = (EditText) findViewById(R.id.et_phone);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnChangeAvatar.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        retriveMemberName();

        createFusedLocationProviderClient();
        createLocationRequest();
        downloadImage(imageView);
    }

    @Override
    public void onClick(View v) {
        if (v == btnChangeAvatar)
        {
            if (aBoolean == false)
                build();
            else
            {
                deleteImage();
                build();
            }
        }

        else if ( v == btnDelete)
        {
            deleteUser();
            deleteUserCars();
            delteUser();
        }

        else if (buttonBack == v)
        {
            changeData("Phone", etPhone.getText().toString() ,db);
            changeData("Email", etUserEmail.getText().toString() ,db);
            changeData("Name", etName.getText().toString() ,db);
            double d = this.latitude;
            if (d != 0.0) {
                changeData("latitude", Double.toString(d), this.db);
            }
            double d2 = this.longitude;
            if (d2 != 0.0) {
                changeData("longitude", Double.toString(d2), this.db);
            }
            finish();
        }
        else if (this.btn_location == v)
        {
            LocationTrack locationTrack2 = new LocationTrack(this);
            this.locationTrack = locationTrack2;
            if (locationTrack2.canGetLocation()) {
                this.longitude = this.locationTrack.getLongitude();
                this.latitude = this.locationTrack.getLatitude();
                return;
            }
            this.locationTrack.showSettingsAlert();
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
                        etName.setText(Name);
                        etUserEmail.setText(Email);
                        etPhone.setText(Phone);
                    }
                }
                else
                {
                    showToast("Failed To Read Data");
                }
            }
        });
    }


    public void changeData (String dest , String data ,FirebaseFirestore db)
    {
        db.collection("user").whereEqualTo("Email",currentuser.getEmail() ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    db.collection("user").document(document.getReference().getPath().substring(5)).update(dest, data);
                }
            }
        });
        showToast("saved");
        startActivity(new Intent(this , PersonalPage.class));
    }



    public void deleteUser()
    {
        db.collection("user").whereEqualTo("Email",currentuser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if (document.get("Email").toString().equals(currentuser.getEmail()))
                        document.getReference().delete();
                }
            }
        });
    }

    public void deleteUserCars()
    {
        db.collection("Cars").whereEqualTo("userEmail",currentuser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if (document.get("userEmail").toString().equals(currentuser.getEmail()))
                        document.getReference().delete();
                }
            }
        });
    }

    public void delteUser()
    {
        currentuser.delete();
    }



    @Override
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

    void imageChooser()
    {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }


    public void uploadImage (Uri image)
    {
        StorageReference imageRef = storageReference.child("images/Avatars/"+currentuser.getUid());
        UploadTask uploadTask = imageRef.putFile(image);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                showToast("Fail to Upload to server");
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
                aBoolean = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.nopic);
                aBoolean = false;
            }
        });
    }


    public void deleteImage ()
    {
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/Avatars/");
        desertRef.child(currentuser.getUid().toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Failed to delete from storage");
            }
        });
    }

    public void build() {
        this.builder.setMessage((CharSequence) "Where do you want to take the picture from ?").setCancelable(false).setPositiveButton((CharSequence) "Gallery", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditProfileUser.this.imageChooser();
            }
        }).setNegativeButton((CharSequence) "Camara", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditProfileUser.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), EditProfileUser.CAMERA_REQUEST);
            }
        });
        AlertDialog alert = this.builder.create();
        alert.setTitle("Select");
        alert.show();
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        try {
            return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", (String) null));
        } catch (Exception ex) {
            showToast(ex.toString());
            return null;
        }
    }

    public void build2F() {
        this.builder.setMessage((CharSequence) "Pls add you location").setCancelable(false).setPositiveButton((CharSequence) "Current Location", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditProfileUser.this.locationTrack = new LocationTrack(EditProfileUser.this);
                if (EditProfileUser.this.locationTrack.canGetLocation()) {
                    EditProfileUser editProfileUser = EditProfileUser.this;
                    editProfileUser.longitude = editProfileUser.locationTrack.getLongitude();
                    EditProfileUser editProfileUser2 = EditProfileUser.this;
                    editProfileUser2.latitude = editProfileUser2.locationTrack.getLatitude();
                    return;
                }
                EditProfileUser.this.locationTrack.showSettingsAlert();
            }
        }).setNegativeButton((CharSequence) "Pick An a Location", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditProfileUser.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), EditProfileUser.CAMERA_REQUEST);
            }
        });
        AlertDialog alert = this.builder.create();
        alert.setTitle("Select");
        alert.show();
    }

    private void createFusedLocationProviderClient() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity) this);
    }

    private void createLocationRequest() {
        LocationRequest create = LocationRequest.create();
        this.mLocationRequest = create;
        create.setPriority(100);
        this.mLocationRequest.setInterval(0);
        this.mLocationRequest.setFastestInterval(5000);
    }

}