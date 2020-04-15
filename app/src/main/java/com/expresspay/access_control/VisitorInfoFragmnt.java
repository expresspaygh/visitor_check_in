package com.expresspay.access_control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisitorInfoFragmnt extends BottomSheetDialogFragment {
    private TextView selectedName,checkInTime,staffName,purpose,passNum;
    private ImageButton cancel;
    private Button checkedOutBtn;
    private GuestCheckedInData selectedGuest;
    GuestAdapter.GuestDataViewHolder viewHolder;

    public VisitorInfoFragmnt(GuestCheckedInData selectedGuest , GuestAdapter.GuestDataViewHolder viewHolder) {
        this.selectedGuest = selectedGuest;
        this.viewHolder = viewHolder;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor_info,container,false);
        selectedName = view.findViewById(R.id.selectedName);
        checkInTime = view.findViewById(R.id.checkedIn_time_tv);
        staffName = view.findViewById(R.id.staff_name_tv);
        purpose = view.findViewById(R.id.purpose_tv);
        passNum = view.findViewById(R.id.passNum_tv);
        checkedOutBtn = view.findViewById(R.id.checked_out_btn);
        cancel = view.findViewById(R.id.cancel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingFields();


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        checkedOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedGuest.isCheckedOut()){

                     viewHolder.updateCheckedInGuests(selectedGuest,false);
                    dismiss();

                }else {
                    // do nothing
                }
            }
        });




    }

    public void settingFields(){
        String formattedTime = formatTime((selectedGuest.getCheckedInTime()));

        selectedName.setText(selectedGuest.getVisitorName());
        checkInTime.setText(formattedTime);
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
