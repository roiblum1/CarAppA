package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<Member> {
    List<Member> memberList;
    Context context;

    public ProfileAdapter(Context context, int resource, int textViewResourceId, List<Member> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.memberList = objects;
    }

    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.member_layouttest, parent, false);
        CardView cv = (CardView) view.findViewById(R.id.cv);
        ImageView ivUser = (ImageView) view.findViewById(R.id.iv_User);
        TextView tv_Name = view.findViewById(R.id.tv_Name);
        TextView tvEmail = view.findViewById(R.id.tv_Email);
        TextView statusTextView = (TextView) view.findViewById(R.id.statusTextView);
        Member member = memberList.get(position);

        tv_Name.setText(member.getMemberName());
        tvEmail.setText(member.getMemberEmail());
        downloadImage(member.getUserUID(), ivUser);

        cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        return view;
    }


    public void downloadImage(String UID, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://yad2v-202a2.appspot.com/images/Avatars/");
        storageRef.child(UID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
}
