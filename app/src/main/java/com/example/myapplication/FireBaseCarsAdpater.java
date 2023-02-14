package com.example.myapplication;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FireBaseCarsAdpater
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference carsRef = db.collection("cars");
    Car car;

    public void addCar(Car car,Activity activity)
    {
        carsRef.add(car).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {

            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(activity, "Successful Post", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Failed Post", Toast.LENGTH_SHORT).show();
            }
        });
    }



}



