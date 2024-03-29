package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class EditAd extends BaseActivity {
    EditText et_carID, et_cat, et_man, et_mod, et_year, et_km, et_yad, et_price, et_userEmail, et_relevant, et_des;
    FirebaseFirestore db;
    FirebaseUser currentuser;
    ImageView imageView;
    public Boolean aBoolean;
    Car car;
    AlertDialog.Builder builder;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 200;
    StorageReference storageReference;
    FirebaseStorage storage;
    Uri selectedImageUri;

    Button btnDelete, btnAddImage, btnSave;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);
        Intent intent = getIntent();
        this.builder = new AlertDialog.Builder(this);
        String carID = intent.getStringExtra("carID");
        String userID = intent.getStringExtra("userID");
        String category = intent.getStringExtra("category");
        String description = intent.getStringExtra("description");
        String km = intent.getStringExtra("km");
        String manufacturer = intent.getStringExtra("manufacturer");
        String model = intent.getStringExtra("model");
        String price = intent.getStringExtra("price");
        String owner = intent.getStringExtra("owner");
        String year = intent.getStringExtra("year");
        Boolean relevant = intent.getBooleanExtra("relevant", true);

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnAddImage = (Button) findViewById(R.id.btn_addImage);
        btnSave = (Button) findViewById(R.id.btn_Save);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();


        imageView = findViewById(R.id.image21);

        car = new Car(category, manufacturer, model, year, owner, km, price, description, carID, userID, relevant);
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
//        et_carID = findViewById(R.id.et_carID);
        et_cat = findViewById(R.id.et_cat);
        et_man = findViewById(R.id.et_man);
        et_mod = findViewById(R.id.et_mod);
        et_year = findViewById(R.id.et_year);
        et_km = findViewById(R.id.et_km);
        et_yad = findViewById(R.id.et_yad);
        et_price = findViewById(R.id.et_price);
        et_userEmail = findViewById(R.id.et_userEmail);
        et_relevant = findViewById(R.id.et_relevant);
        et_des = findViewById(R.id.et_des);

        //        et_carID.setText(carID);
        et_cat.setText(category);
        et_man.setText(manufacturer);
        et_mod.setText(model);
        et_year.setText(year);
        et_km.setText(km);
        et_yad.setText(owner);
        et_price.setText(price);
        et_userEmail.setText(userID);
        et_relevant.setText(relevant.toString());
        et_des.setText(description);


//        et_carID.setTextColor(getResources().getColor(R.color.white));
        et_cat.setTextColor(getResources().getColor(R.color.white));
        et_man.setTextColor(getResources().getColor(R.color.white));
        et_mod.setTextColor(getResources().getColor(R.color.white));
        et_year.setTextColor(getResources().getColor(R.color.white));
        et_km.setTextColor(getResources().getColor(R.color.white));
        et_yad.setTextColor(getResources().getColor(R.color.white));
        et_price.setTextColor(getResources().getColor(R.color.white));
        et_userEmail.setTextColor(getResources().getColor(R.color.white));
        et_relevant.setTextColor(getResources().getColor(R.color.white));
        et_des.setTextColor(getResources().getColor(R.color.white));
        downloadImage();

    }

    public void viewImage(View view) {
    }

    public void saveC(View view) {
        changeData("category", et_cat.getText().toString(), db, car.getCarID());
        changeData("description", et_des.getText().toString(), db, car.getCarID());
        changeData("km", et_km.getText().toString(), db, car.getCarID());
        changeData("price", et_price.getText().toString(), db, car.getCarID());
        changeData("manufacturer", et_man.getText().toString(), db, car.getCarID());
        changeData("model", et_mod.getText().toString(), db, car.getCarID());
        changeData("owner", et_yad.getText().toString(), db, car.getCarID());
        changeData("year", et_year.getText().toString(), db, car.getCarID());
        changeData("relevant", et_relevant.getText().toString(), db, car.getCarID());
        startActivity(new Intent(this, PersonalPage.class));
    }

    //an function that is OnClick of btnSave button and it will Save all Of the changes.
    public void addImage(View view) {
        if (aBoolean == false)
            build();
        else {
            deleteImageCar(car.getCarID().toString());
            build();
        }
    }
    //an function that is OnClick of btnAddImage button and
    // it will check if there is an image already and then it will delete it from the FireBase Storage else
    //and only then call to build function.

    public void deleteAD(View view) {
        db.collection("Cars").whereEqualTo("carID", car.getCarID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.get("carID").toString().equals(car.getCarID())) {
                        String carID = document.get("carID").toString();
                        deleteImageCar(carID);
                        document.getReference().delete();
                    }
                }
            }
        });
        //changeDataUser(FirebaseFirestore.getInstance());
        showToast("deleted");
        startActivity(new Intent(this, PersonalPage.class));
    }
    //an function that is Onclick to btnDelete button and it will delete the Ad

    public void changeData(String dest, String data, FirebaseFirestore db, String carID) {
        db.collection("Cars").whereEqualTo("carID", carID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (dest.equals("relevant"))
                        if (data.equals("true"))
                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", true);
                        else
                            db.collection("Cars").document(document.getReference().getPath().substring(5)).update("relevant", false);
                    else
                        db.collection("Cars").document(document.getReference().getPath().substring(5)).update(dest, data);
                }
            }
        });
        showToast("saved");
        startActivity(new Intent(this, PersonalPage.class));
    }
    //an function to change a specific data from the FireStore Ad document.

    public void downloadImage() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images");
        storageRef.child(car.getCarID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
    //an function to download the image of the ad from FireBase Storage and insert it into ImageView and turn the aBoolean to true
    //that only if there is an image, else it will put an other image an aBoolean will stay false.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    //on activity result function, in case of camara will convert the result to Uri using the function getImageUri
    //and in case of Gallery will upload the image. and in both cases the imageView image will changed to the new one.
    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }
    //an function to choose an image from the Gallery with OnActivityResult

    public void uploadImage(Uri image) {
        StorageReference imageRef = storageReference.child("images/" + car.getCarID().toString());
        UploadTask uploadTask = imageRef.putFile(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to Upload to server");
            }
        });
    }

    //an function to upload an Uri Image to Firebase Storage.
    public void build() {
        this.builder.setMessage((CharSequence) "Where do you want to take the picture from ?").setCancelable(false).setPositiveButton((CharSequence) "Gallery", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditAd.this.imageChooser();
            }
        }).setNegativeButton((CharSequence) "Camara", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditAd.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), CAMERA_REQUEST);
            }
        });
        AlertDialog alert = this.builder.create();
        alert.setTitle("Select");
        alert.show();
    }
    //an function to build the camara or gallery dialog.

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        try {
            return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", (String) null));
        } catch (Exception ex) {
            showToast(ex.toString());
            return null;
        }
    }
    //an function that converts an bitmap image to Uri.


}