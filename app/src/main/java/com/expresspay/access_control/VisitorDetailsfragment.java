package com.expresspay.access_control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.expresspay.access_control.models.CheckInData;
import com.google.android.material.textfield.TextInputEditText;


public class VisitorDetailsfragment extends Fragment {

    private ImageButton backButton;
    private Button nextButton;
    private TextInputEditText nameEdt;
    private TextInputEditText numberEdt;
    private CheckInData checkInData;
    private String name;
    private String number;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor__details, container, false);
        backButton = view.findViewById(R.id.back_imageButton);
        nextButton = view.findViewById(R.id.visitorButton_btn);
        nameEdt = view.findViewById(R.id.name_edt);
        numberEdt = view.findViewById(R.id.number_edt);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    navigate();
                } else {
                    Toast.makeText(v.getContext(), "Full Name or Phone Number Not Valid ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean validate() {
        name = nameEdt.getText().toString();
        number = numberEdt.getText().toString();

        return !name.isEmpty() && !number.isEmpty();
    }

    //create a method to display the next fragment
    private void navigate() {

        checkInData = new CheckInData();
        checkInData.setVisitorName(name);
        checkInData.setVisitorPhone(number);

        Bundle bundle = new Bundle();
        bundle.putSerializable("checkin_data", checkInData);


        //create next fragment and transaction
        Fragment fragmentStaffMember = new StaffMemberFragment();
        fragmentStaffMember.setArguments(bundle);

        FragmentTransaction transactionStaffMember = getFragmentManager().beginTransaction();

        transactionStaffMember.replace(R.id.fragment_container_fl, fragmentStaffMember);
        transactionStaffMember.addToBackStack(null);

        transactionStaffMember.commit();

    }


}
