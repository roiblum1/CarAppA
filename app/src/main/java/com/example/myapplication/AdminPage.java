package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {
    ListView lv_admin;
    List<Member> memberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        lv_admin = (ListView) findViewById(R.id.lv_admin);
        lv_admin.setAnimation(AnimationUtils.loadAnimation(AdminPage.this, R.anim.fade_transition));
        lv_admin.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminPage.this);
                // Set the message show for the Alert time
                builder.setMessage("What Do you want to do with this Account ?");
                // Set Alert Title
                builder.setTitle("Alert");
                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminPage.this);
                    builder2.setMessage("Do you want Delete it or see it ?");
                    builder2.setTitle("More");
                    builder2.setCancelable(false);
                    builder2.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String Email = memberList.get(position).getMemberEmail().toString();
                                    deleteUserCars(Email);
                                    deleteUser(Email);
                                    deleteImage(memberList.get(position).getUserUID().toString());
                                    finish();
                                }
                            });
                    builder2.setNegativeButton("View", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AdminPage.this, ViewProfile.class);
                            intent.putExtra("SellerEmail",memberList.get(position).getMemberEmail());
                            startActivity(intent);
                            finish();
                        }
                    });
                    finish();
                });
                builder.setNegativeButton("Cancle", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
        getAllUsers();
    }

    public void getAllUsers ()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    memberList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        String Name = document.get("Name").toString();
                        String Email = document.get("Email").toString();
                        String UID = document.get("UID").toString();
                        String Phone = document.get("Phone").toString();
                        String Favorites = document.get("Favorites").toString();
                        String latitude = document.get("latitude").toString();
                        String longitude = document.get("longitude").toString();
                        int AdPosted = Integer.parseInt(document.get("AdPosted").toString());
                        Member member = new Member(UID,Email,Name,Phone,latitude,longitude,AdPosted);
                        memberList.add(member);
                    }
                    ProfileAdapter adapter = new ProfileAdapter(AdminPage.this,0,0,memberList);
                    lv_admin.setAdapter(adapter);
                }
            }
        });
    }

    public void deleteUserCars(String email)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cars").whereEqualTo("userEmail",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if (document.get("userEmail").toString().equals(email))
                        document.getReference().delete();
                }
            }
        });
    }

    public void deleteUser(String email)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").whereEqualTo("Email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if (document.get("Email").toString().equals(email))
                        document.getReference().delete();
                }
            }
        });
    }

    public void deleteImage (String UID)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/Avatars/");
        desertRef.child(UID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminPage.this, "File Delete From ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}