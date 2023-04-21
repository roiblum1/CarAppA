package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class SelectFragment extends Fragment {
    View view;
    Button firstButton;
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
// get the reference of Button
        ArrayList<Member> myStrings = new ArrayList<>();
        myStrings.add(new Member("Name1","Email1","Phone1"));
        myStrings.add(new Member("Name2","Email2","Phone2"));
        myStrings.add(new Member("Name3","Email3","Phone3"));
        myStrings.add(new Member("Name4","Email4","Phone4"));


        ProfileAdapter adapter = new ProfileAdapter(view.getContext(),0,0,myStrings);
        ListView listView = view.findViewById(R.id.lv);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Member selectedMember = (Member)myStrings.get(position);
            listener.onListItemSelected(selectedMember);
        });
        return view;
    }
}
