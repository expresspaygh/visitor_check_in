package com.expresspay.access_control;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.expresspay.access_control.models.GuestData;
import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.Inflater;

import io.realm.Realm;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.ViewHolder> {
    //define the list(guestData)
    private List<GuestCheckedInData> guestDataList;

    //Context is changed to FragmentActivity to be able to get access to my fragments
    private FragmentActivity context;

    private GuestCheckedInData selectedGuest;

    ViewHolder viewHolder;

    private int[] arrayColor = {R.color.violet, R.color.lemon, R.color.brown, R.color.light_green, R.color.light_blue, R.color.blue, R.color.deep_green, R.color.orange};



    //constructor to get the list's objects and context
    public GuestAdapter(List<GuestCheckedInData> guestDataList, FragmentActivity context) {
        this.guestDataList = guestDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind the data to the view (Recycler_item.xml) objects
        holder.bindView(position);
    }

    @Override
    //the size of the guestData items
    public int getItemCount() {
        return guestDataList.size();
    }

    //update adapter list
    public void update(List<GuestCheckedInData> newList) {
        //clear old list to become empty
        guestDataList.clear();

        //insert all new data into the old list
        guestDataList.addAll(newList);

        // tell the adapter that the dataSet/list has changed to it should update itself
         notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //define the view objects
        private TextView textViewFullName;
        private TextView textViewCheckedTime;
        private MaterialLetterIcon letterIcon;
        private ImageView imageViewUser_x;
        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            //reference the objects
            textViewFullName = itemView.findViewById(R.id.fullName_tv);
            textViewCheckedTime = itemView.findViewById(R.id.checked_time_tv);
            letterIcon = itemView.findViewById(R.id.letterIcon);
            imageViewUser_x = itemView.findViewById(R.id.user_x);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  selectedGuest = guestDataList.get(getAdapterPosition());
                  if(!selectedGuest.isCheckedOut()){

                        showButtomSheetDialogFragment();
                  }
                  else {
                        showGuestCheckOutInfo();
                  }



                }
            });

            imageViewUser_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //save an instance of the guest the user selects
                      selectedGuest = guestDataList.get(getAdapterPosition());
                      if(!selectedGuest.isCheckedOut()){
                          updateCheckedInGuests(selectedGuest,false);
                      }else {
                         // do nothing
                      }




                }
            });
        }
        //function to show the bottom sheet dialog fragment(ie. GuestCheckOutInfo)
        public void showGuestCheckOutInfo(){
            GuestCheckOutInfo guestCheckOutInfo = new GuestCheckOutInfo(selectedGuest);
            guestCheckOutInfo.show(context.getSupportFragmentManager(),guestCheckOutInfo.getTag());

        }
        //function to show the bottom sheet dialog fragment(ie. VisitorInfoFragment)
        public void showButtomSheetDialogFragment(){
            VisitorInfoFragmnt visitorInfoFragmnt = new VisitorInfoFragmnt(selectedGuest,viewHolder);
            visitorInfoFragmnt.show(context.getSupportFragmentManager(),visitorInfoFragmnt.getTag());
            notifyDataSetChanged();
        }
        //function to update the database
        public void updateCheckedInGuests(final GuestCheckedInData selectedGuest,final boolean undo){
            Realm realm = Realm.getDefaultInstance();

            //Asynchronously update objects on a background thread
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    GuestCheckedInData guestCheckedInData = realm.where(GuestCheckedInData.class)
                            .equalTo("checkedOut", selectedGuest.isCheckedOut())//either true or false
                            .and()
                            .equalTo("checkedInTime", selectedGuest.getCheckedInTime())
                            .findFirst();

                    Log.e("REALM", guestCheckedInData.getVisitorName());
                    guestCheckedInData.setCheckedOut(!selectedGuest.isCheckedOut());
                    selectedGuest.setCheckedOut(!selectedGuest.isCheckedOut());


                    String CheckOutCurrentTime = String.valueOf(System.currentTimeMillis());
                    Log.e("Timestamp", CheckOutCurrentTime);
                    guestCheckedInData.setCheckedOutTime(CheckOutCurrentTime);

                    //  guestCheckedInData.setCheckedOutTime(!selectedGuest.setCheckedOutTime(););

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    // do this if update is successful
                    if(!undo){
                        showSuccessSnackBar();

                    }
                    updateCountAndListContents();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    //do if update fails
               error.printStackTrace();
                    Log.e("UNDO", "Guest Checked Out failed: " + selectedGuest.getCheckedInTime());
              }

             });
            realm.close();
         }



        void bindView(int position) {
            //reference the GuestData
            GuestCheckedInData guestCheckedInData = guestDataList.get(position);


            // gather current items from guestDataList
            textViewFullName.setText(guestCheckedInData.getVisitorName());
            if (guestCheckedInData.isCheckedOut()) {
                //format time
                String formattedTime = formatTime((guestCheckedInData.getCheckedOutTime()));
                textViewCheckedTime.setText( "Checked out @" + formattedTime);
                //set the drawable to user icon
                imageViewUser_x.setImageResource(R.drawable.ic_guest_check_in_icon);
                imageViewUser_x.setVisibility(View.INVISIBLE);
            } else {
                String formattedTime = formatTime((guestCheckedInData.getCheckedInTime()));

                textViewCheckedTime.setText("Checked in @ " +  formattedTime);
                imageViewUser_x.setImageResource(R.drawable.ic_user_x);
                imageViewUser_x.setVisibility(View.VISIBLE);
            }
            // letter icon
            letterIcon.setLetter(guestCheckedInData.getVisitorName());

            letterIcon.setLettersNumber(1);
            letterIcon.setInitials(true);
            letterIcon.setInitialsNumber(2);

            //setting random colors to letterIcon shape color
            Random random = new Random();
            int r = random.nextInt(7);
//            Log.e("random", r + "" + arrayColor[r]);
            //   Color color = arrayColor[];
            letterIcon.setShapeColor(context.getResources().getColor(arrayColor[r]));
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

        //create a method for the snackBar
                void showSuccessSnackBar(){
                    Snackbar snackbar = Snackbar.make(itemView,"Guest Checked Out",Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           updateCheckedInGuests(selectedGuest,true);
                        }
                    });
                    snackbar.show();
                }

                //update the display
                void updateCountAndListContents(){
                    //update the count at the top
                    updateCount();
                    updateList();
                }

                private void updateCount(){
                    //find CheckedInPopulatedFragment using the tag set when loading the fragment
                    CheckInPopulatedStateFragment f = (CheckInPopulatedStateFragment) context.getSupportFragmentManager().findFragmentByTag("CheckInPopulatedStateFragment");

                    // make sure its not null before you update
                    if (f != null) {
                        f.getUpdateCount();
                    } else {
                        Log.e("UPDATE", "CheckInPopulatedStateFragment is null");
                    }
                }

                private void updateList() {
                    // Find TotalCheckedIn and TotalCheckedOut
                    TotalCheckedIn totalCheckedIn = (TotalCheckedIn) context.getSupportFragmentManager().findFragmentByTag(getDefaultFragmentTag(0));
                    TotalCheckedOut totalCheckedOut = (TotalCheckedOut) context.getSupportFragmentManager().findFragmentByTag(getDefaultFragmentTag(1));

                    // make sure none of them are null
                    if (totalCheckedIn != null && totalCheckedOut  != null) {

                        // call the functions that update their various lists
                        totalCheckedIn.fetchCheckedInGuests();
                        totalCheckedOut.fetchCheckedOutGuest();
                    } else {
                        Log.e("UPDATE", "either is null");
                    }
                }

                // this is the default tag generated for fragments that are in a viewPager
                // (CheckInPopulatedStateFragment is not a viewpager so there is no default tag)
                private String getDefaultFragmentTag(int pos){
                    return "android:switcher:"+R.id.viewPager+":"+pos;
                }
    }
      public void filterList(List<GuestCheckedInData> filteredList){
        guestDataList = filteredList;
        notifyDataSetChanged();
    }



}
