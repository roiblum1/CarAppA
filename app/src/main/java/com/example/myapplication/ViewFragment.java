package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

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

            }
        });
        return view;
    }
}
