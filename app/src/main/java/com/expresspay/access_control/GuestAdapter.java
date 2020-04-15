package com.expresspay.access_control;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.expresspay.access_control.models.DateItem;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.expresspay.access_control.models.GuestItem;
import com.expresspay.access_control.models.ListItem;
import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.realm.Realm;

public class GuestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListItem> consolidatedList;

    //Context is changed to FragmentActivity to be able to get access to my fragments
    private FragmentActivity context;

    private GuestCheckedInData selectedGuest;

    RecyclerView.ViewHolder viewHolder;

    private int[] arrayColor = {R.color.violet, R.color.lemon, R.color.brown, R.color.light_green, R.color.light_blue, R.color.blue, R.color.deep_green, R.color.orange};



    //constructor to get the list's objects and context
    public GuestAdapter(List<ListItem> consolidatedList, FragmentActivity context) {
        this.consolidatedList = consolidatedList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    @Override
    //the size of the guestData items
    public int getItemCount() {
        return consolidatedList !=null ? consolidatedList.size() : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ListItem.TYPE_GUEST_DATA:
                View view = inflater.inflate(R.layout.item_recyclerview,parent,false);
                viewHolder = new GuestDataViewHolder(view);
                break;

            case ListItem.TYPE_DATE:
                View v = inflater.inflate(R.layout.item_date,parent,false);
                viewHolder = new DateViewHolder(v);
                break;
        }
        return viewHolder;
        // viewHolder = new ViewHolder(view);
//        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //bind the data to the view (Recycler_item.xml) objects
        // holder.bindView(position);
        switch (holder.getItemViewType()){
            case ListItem.TYPE_GUEST_DATA:
                ((GuestDataViewHolder) holder).bindView(position);
//                GuestItem guestDataItems = (GuestItem) consolidatedList.get(position);
//                GuestDataViewHolder guestDataViewHolder = (GuestDataViewHolder) holder;
//                guestDataItems.setGuestCheckedInData(guestDataItems.getGuestCheckedInData());
//                (guestDataViewHolder).GuestDataBindView(position);
                break;

            case ListItem.TYPE_DATE:
                ((DateViewHolder) holder).bindView(position);
//                DateItem dateItem = (DateItem) consolidatedList.get(position);
//                DateViewHolder dateViewHolder = (DateViewHolder) holder;
//                dateItem.setDate(dateItem.toString());
//                (dateViewHolder).DateTimeBindView(position);
                break;
        }
    }


    //update adapter list
    //change to listItem
    public void update(List<ListItem> newList) {
        //clear old list to become empty
        consolidatedList.clear();

        //insert all new data into the old list
        consolidatedList.addAll(newList);

        // tell the adapter that the dataSet/list has changed to it should update itself
        notifyDataSetChanged();
    }

    public  class DateViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.dateTime_tv);
        }

        public void bindView(int position) {
            //dont know what to do nxt

            // get the  date
            DateItem dateItem = (DateItem) consolidatedList.get(position);
            // putting braces around a class is called type-casting... read on it.

            // set the textviewDate
            textViewDate.setText(dateItem.getDate());
        }
    }


    public class GuestDataViewHolder extends RecyclerView.ViewHolder {
        //define the view objects
        private TextView textViewFullName;
        private TextView textViewCheckedTime;

        private MaterialLetterIcon letterIcon;
        private ImageView imageViewUser_x;
        private View itemView;

        public GuestDataViewHolder(@NonNull View itemView) {
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

                    selectedGuest = ((GuestItem) consolidatedList.get(getAdapterPosition())).getGuestCheckedInData();
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
                    selectedGuest =  ((GuestItem) consolidatedList.get(getAdapterPosition())).getGuestCheckedInData();
                    if(!selectedGuest.isCheckedOut()){
                        updateCheckedInGuests(selectedGuest,false);
                    }else {
                        // do nothing
                    }

                }
            });
        }

        void bindView(int position) {
            //reference the GuestData
            GuestCheckedInData guestCheckedInData =  ((GuestItem) consolidatedList.get(position)).getGuestCheckedInData();

            // gather current items from guestDataList
            textViewFullName.setText(guestCheckedInData.getVisitorName());
            if (guestCheckedInData.isCheckedOut()) {
                //format time
                String formattedTime = formatTime((guestCheckedInData.getCheckedOutTime()));
                textViewCheckedTime.setText("Checked out @" + formattedTime);
                //set the drawable to user icon
                imageViewUser_x.setImageResource(R.drawable.ic_guest_check_in_icon);
                imageViewUser_x.setVisibility(View.INVISIBLE);
            } else {
                String formattedTime = formatTime((guestCheckedInData.getCheckedInTime()));

                textViewCheckedTime.setText("Checked in @ " + formattedTime);
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

        //function to show the bottom sheet dialog fragment(ie. GuestCheckOutInfo)
        public void showGuestCheckOutInfo(){
            GuestCheckOutInfo guestCheckOutInfo = new GuestCheckOutInfo(selectedGuest);
            guestCheckOutInfo.show(context.getSupportFragmentManager(),guestCheckOutInfo.getTag());

        }
        //function to show the bottom sheet dialog fragment(ie. VisitorInfoFragment)
        public void showButtomSheetDialogFragment(){
            VisitorInfoFragmnt visitorInfoFragmnt = new VisitorInfoFragmnt(selectedGuest, (GuestDataViewHolder) viewHolder);
            visitorInfoFragmnt.show(context.getSupportFragmentManager(), visitorInfoFragmnt.getTag());
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

    public void filterList(List<ListItem> filteredList){
        consolidatedList = filteredList;
        notifyDataSetChanged();
    }


}
