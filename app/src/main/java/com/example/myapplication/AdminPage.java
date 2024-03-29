package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AdminPage extends AppCompatActivity implements SelectFragment.OnListItemSelectedListener {
    //Admin Page for the admin, here you can see and mange all the accounts that are in the fireBase
    private Button firstFragment;
    private Button secondFragment;
    private FrameLayout frameLayout;
    public Member member;
    public Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        member = new Member("", "", "");
        firstFragment = (Button) findViewById(R.id.firstFragment);
        secondFragment = (Button) findViewById(R.id.secondFragment);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        //lv_admin.setAnimation(AnimationUtils.loadAnimation(AdminPage.this, R.anim.fade_transition));

        firstFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load First Fragment
                loadFragment(new SelectFragment());
            }
        });
        secondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load Second Fragment
                // Create a bundle and set the data you want to pass
                Bundle bundle = new Bundle();
                bundle.putString("Name", member.getMemberName());
                bundle.putString("Email", member.getMemberEmail());
                bundle.putString("Phone", member.getMemberPhone());
                bundle.putString("UID", member.getUserUID());
                // Create the destination fragment and set the arguments
                ViewFragment fragment = new ViewFragment();
                fragment.setArguments(bundle);
                // Replace the current fragment with the destination fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    public void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("data_key", "This is some data");
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onListItemSelected(Member selectedMember) {
        if (selectedMember != null) {
            member.setMemberName(selectedMember.getMemberName());
            member.setMemberEmail(selectedMember.getMemberEmail());
            member.setMemberPhone(selectedMember.getMemberPhone());
            member.setUserUID(selectedMember.getUserUID());
        }
    }
}
