package com.expresspay.access_control;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expresspay.access_control.models.CheckInData;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.textfield.TextInputEditText;

import io.realm.Realm;

public class PassNumberFragment extends Fragment {

    private ImageButton backButton;
    private Button checkInButton;
    private TextInputEditText passNumber;

    private String  passNum ;
    private CheckInData checkInData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!= null){
            checkInData = (CheckInData) getArguments().getSerializable("staff_data");
        }
    }

    @Nullable
    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passnumber,container,false);
        backButton= view.findViewById(R.id.back_imageButton);
        checkInButton = view.findViewById(R.id.checkedIn_btn);
        passNumber = view.findViewById(R.id.pass_Edt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    getActivity().onBackPressed();
                }
            }
        });

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    saveToDataBase();
                   // getActivity().finish();
                }
                else {
                    Toast.makeText(v.getContext(), "Pass Number  Not Filled ", Toast.LENGTH_SHORT).show();
                }
            }
        });
  }

        private boolean validate(){
        passNum = passNumber.getText().toString();

        return !passNum.isEmpty();
        }

        private void saveToDataBase(){

        checkInData.setPassNumber( passNum );
            Log.e("anything", "saveToDataBase: " + checkInData.getVisitorName());
            Log.e("anything", "saveToDataBase: " + checkInData.getVisitorPhone());
            Log.e("anything", "saveToDataBase: " + checkInData.getStaffName());
            Log.e("anything", "saveToDataBase: " + checkInData.getPurpose());
            Log.e("anything", "saveToDataBase: " + checkInData.getPassNumber());

            // insert into db
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    GuestCheckedInData guestCheckedInData = new GuestCheckedInData();

                    String currentTime = String.valueOf(System.currentTimeMillis());
                    Log.e("Timestamp", currentTime);

                    guestCheckedInData.setVisitorName(checkInData.getVisitorName());
                    guestCheckedInData.setVisitorPhone(checkInData.getVisitorPhone());
                    guestCheckedInData.setStaffName(checkInData.getStaffName());
                    guestCheckedInData.setPurpose(checkInData.getPurpose());
                    guestCheckedInData.setPassNumber(checkInData.getPassNumber());
                    guestCheckedInData.setCheckedInTime(currentTime);
                    guestCheckedInData.setCheckedOutTime("");
                    guestCheckedInData.setCheckedOut(false);

                    realm.insert(guestCheckedInData);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_LONG).show();
                }


            });

        }


    }
