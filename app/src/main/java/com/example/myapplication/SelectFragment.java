package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SelectFragment extends Fragment {
    View view;
    Button firstButton;
    ListView listView;
    ArrayList<Member> memberList;
    private OnListItemSelectedListener listener;

    public interface OnListItemSelectedListener {
        void onListItemSelected(Member selectedMember);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnListItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnListItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select, container, false);
        listView = view.findViewById(R.id.lv);
// get the reference of Button
        getAllUsers();
        listView.setAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_transition));


        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Member selectedMember = (Member)memberList.get(position);
            listener.onListItemSelected(selectedMember);
            Toast.makeText(view.getContext(), "Member selected !", Toast.LENGTH_SHORT).show();
        });
        return view;
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
                    ProfileAdapter adapter = new ProfileAdapter(view.getContext(),0,0,memberList);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
}
