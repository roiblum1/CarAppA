package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class ViewFragment extends Fragment {
    View view;
    private TextView title;
    private ImageView imageView;
    private LinearLayout LL1;
    private TextInputEditText etName;
    private TextInputEditText etUserEmail;
    private TextInputEditText etPhone;
    private Button buttonDelete;
    private Button buttonEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view, container, false);
// get the reference of Button
        title = (TextView) view.findViewById(R.id.title);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        LL1 = (LinearLayout) view.findViewById(R.id.LL1);
        etName = (TextInputEditText) view.findViewById(R.id.et_name);
        etUserEmail = (TextInputEditText) view.findViewById(R.id.et_userEmail);
        etPhone = (TextInputEditText) view.findViewById(R.id.et_phone);
        buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
        buttonEdit = (Button) view.findViewById(R.id.buttonEdit);

        Bundle bundle = getArguments();
        assert bundle != null;
        String Name = bundle.getString("Name");
        String Email = bundle.getString("Email");
        String Phone = bundle.getString("Phone");
        String UID = bundle.getString("UID");
        Member member = new Member(Name, Email, Phone);

        etName.setText(member.getMemberName());
        etUserEmail.setText(member.getMemberEmail());
        etPhone.setText(member.getMemberPhone());

        etName.setEnabled(false);
        etUserEmail.setEnabled(false);
        etPhone.setEnabled(false);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                etName.setEnabled(true);
//                etUserEmail.setEnabled(true);
//                etPhone.setEnabled(true);
                Toast.makeText(getContext(), "Not Work", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage(UID);
                deleteUser(Email);
                deleteUserCars(Email);
            }
        });

        downloadImage(UID, imageView);
        return view;
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


    public void deleteUserCars(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cars").whereEqualTo("userEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.get("userEmail").toString().equals(email))
                        document.getReference().delete();
                }
            }
        });
    }

    public void deleteUser(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.get("Email").toString().equals(email))
                        document.getReference().delete();
                }
            }
        });
    }

    public void deleteImage(String UID) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child("images/Avatars/");
        desertRef.child(UID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(view.getContext(), "File Delete From ", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
