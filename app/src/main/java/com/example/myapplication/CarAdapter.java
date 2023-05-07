package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarAdapter extends ArrayAdapter<Car> {
    List<Car> carList;
    Context context;
    CardView cv;
    public String Favorites;


    public CarAdapter(Context context, int resource, int textViewResourceId, List<Car> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.carList = objects;
    }

    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.car_layout, parent, false);
        CardView cv = (CardView) view.findViewById(R.id.cv);
        TextView tvTitle = view.findViewById(R.id.tv_titleO);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView year_km = view.findViewById(R.id.year_km);
        TextView Star = cv.findViewById(R.id.Star);

        ImageView ivCar = view.findViewById(R.id.iv_car);
        cv = view.findViewById(R.id.cv);
        Car c = carList.get(position);
        retrieveFavorites();
        Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Favorites != null) {
                    if (!isInString(Favorites, c.getCarID())) {
                        if (Favorites == "") {
                            Favorites += "" + c.getCarID().toString();
                        } else {
                            Favorites += ", " + c.getCarID().toString();
                        }
                        changeData(Favorites);
                    }
                } else
                    Toast.makeText(context.getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                Star.setText("Saved");
            }
        });


        tvTitle.setText("Model : " + c.model);
        tvPrice.setText("Price : " + c.price + " $");
        year_km.setText("Year : " + c.getYear() + " • KM : " + c.getKm());

        downloadImage(ivCar, c);

        cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));


        return view;
    }

    public void downloadImage(ImageView ivCar, Car c) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images");
        storageRef.child(c.getCarID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri.toString()).into(ivCar);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ivCar.setImageResource(R.drawable.nopic);
            }
        });
    }

    private void retrieveFavorites()//retrive member name
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        db.collection("user").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("Tag", document.getId() + " => " + document.getData());
                    Favorites = document.get("Favorites").toString();
                    //Toast.makeText(context, "Hello " +Favorites, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeData(String data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user").whereEqualTo("Email", currentuser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().update("Favorites", data);
                    }
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        });
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

    public void sharedIN() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String value = "Hello, World!";
        editor.putString("key", value);
        editor.apply();
    }

    public void sharedOUT() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("Favorites", "");
    }


}