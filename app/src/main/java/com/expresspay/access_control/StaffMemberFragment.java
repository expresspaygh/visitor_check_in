package com.expresspay.access_control;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.expresspay.access_control.models.CheckInData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class StaffMemberFragment extends Fragment {

    private Button nextButton;
    private ImageButton backButton;
    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    private TextInputEditText otherTextInputEditText;
    private TextInputEditText staffTextInputEditText;
    private String selectedRadioText;
    private TextInputLayout otherTextInputLayout;

    private String staffName;
    private String purpose;


    private static final String PERSONAL = "Person";
    private static final String OFFICIAL = "Official";
    private static final String OTHER = "Other";

    // create a global variable for the data we are expecting
    private CheckInData checkInData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            checkInData = (CheckInData) getArguments().getSerializable("checkin_data");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmet_staff_member, container, false);
        nextButton = view.findViewById(R.id.nextButton_btn);
        backButton = view.findViewById(R.id.back_imageButton);
        radioGroup = view.findViewById(R.id.radioGroup);
        staffTextInputEditText = view.findViewById(R.id.whoToSee_tv);
        otherTextInputEditText = view.findViewById(R.id.other);
        otherTextInputLayout = view.findViewById(R.id.other_field);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    navigate();
                } else {
                    Toast.makeText(v.getContext(), "Fields Not Filled ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("radio", "checked changed" + checkedId);
                switch (checkedId) {
                    case R.id.personal_rbtn:
                        Log.e("radio", "personal");
                        selectedRadioText = PERSONAL;
                        otherTextInputLayout.setVisibility(View.INVISIBLE);
                        otherTextInputEditText.setText("");
                        break;
                    case R.id.officail_rbtn:
                        Log.e("radio", "official");
                        selectedRadioText = OFFICIAL;
                        otherTextInputLayout.setVisibility(View.INVISIBLE);
                        otherTextInputEditText.setText("");
                        break;

                    case R.id.other_rbtn:
                        Log.e("radio", "other");
                        selectedRadioText = OTHER;
                        otherTextInputLayout.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });
    }


    private void navigate() {


        checkInData.setStaffName(staffName);
        if (selectedRadioText != null && !selectedRadioText.equals(OTHER)){
            checkInData.setPurpose(selectedRadioText);
        } else {
            checkInData.setPurpose(purpose);
        }


        Bundle bundle = new Bundle();
        bundle.putSerializable("staff_data", checkInData);

        Fragment fragmentPassNumber = new PassNumberFragment();
        fragmentPassNumber.setArguments(bundle);
        FragmentTransaction transactionPassNumber = getFragmentManager().beginTransaction();

        transactionPassNumber.replace(R.id.fragment_container_fl, fragmentPassNumber);
        transactionPassNumber.addToBackStack(null);

        transactionPassNumber.commit();
    }

    private boolean validate() {
        staffName = staffTextInputEditText.getText().toString();
        purpose = otherTextInputEditText.getText().toString();

        if (selectedRadioText == null) {

            Toast.makeText(getContext(), "Radio Button Unchecked", Toast.LENGTH_SHORT).show();
            return false;

        } else {

            if (selectedRadioText.equals(OTHER)) {
                if (purpose.isEmpty()) {
                    Toast.makeText(getContext(), "Error, Field Empty", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            // check if staff name is empty
            if (staffName.isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
