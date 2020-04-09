package com.expresspay.access_control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuestCheckOutInfo extends BottomSheetDialogFragment {
    private TextView selectedName,checkOutTime,staffName,purpose,passNum;
    private ImageButton cancel;
    private GuestCheckedInData selectedGuest;


    public GuestCheckOutInfo(GuestCheckedInData selectedGuest) {
        this.selectedGuest = selectedGuest;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guest_checkout_info,container,false);
        selectedName = view.findViewById(R.id.selectedName);
        checkOutTime = view.findViewById(R.id.checkedOut_time_tv);
        staffName = view.findViewById(R.id.staff_name_tv);
        purpose = view.findViewById(R.id.purpose_tv);
        passNum = view.findViewById(R.id.passNum_tv);
        cancel = view.findViewById(R.id.cancel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingFields();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
    }

    public void settingFields(){
        String formattedTime = formatTime((selectedGuest.getCheckedOutTime()));

        selectedName.setText(selectedGuest.getVisitorName());
        checkOutTime.setText(formattedTime);
        staffName.setText(selectedGuest.getStaffName());
        passNum.setText(selectedGuest.getPassNumber());
        purpose.setText(selectedGuest.getPurpose());
    }

    String formatTime(String dateTime){
        String formattedTime;
        try {
            Date date = new Date(Long.parseLong(dateTime));
            formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
        }catch (Exception e){
            //if an error error occurs while formatting the date
            e.printStackTrace();
            formattedTime = "";
        }
        return formattedTime;
    }
}
