package com.expresspay.access_control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start the loading fragment based on whether guests are logged in or not
            loadAppropriateFragment();
    }


    @Override
    public void onBackPressed() {

        // get the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_fl);
        if (currentFragment instanceof NoCheckedInGuestFragment ||currentFragment instanceof CheckInPopulatedStateFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    //function to get current fragment
    Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container_fl);
    }

    private void loadAppropriateFragment(){
      if(areGuestsCheckedIn()){
            Fragment fragmentGuest = new CheckInPopulatedStateFragment();
            loadFragment(fragmentGuest, false,"CheckInPopulatedStateFragment");
        }
        else {
            Fragment fragmentNoGuest  = new NoCheckedInGuestFragment();
            loadFragment(fragmentNoGuest, false, "CheckInPopulatedStateFragment");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //get current fragment
        boolean b = getCurrentFragment() instanceof NoCheckedInGuestFragment;

        // if the current fragment loaded is NoCheckedInGuestFragment && the database is not empty (that is items exist in the database),
        // it means the first guest was just added to the database
        // so we load the CheckInPopulatedStateFragment

        if(b && areGuestsCheckedIn()){
            Fragment fragmentGuest = new CheckInPopulatedStateFragment();
            loadFragment(fragmentGuest, true,"CheckInPopulatedStateFragment");
        }else {
            //do nothing
        }

    }

    // adding a "tag" parameter to the function so in the future you can find a particular
    // fragment by tag.
    void loadFragment(Fragment fragment, boolean replace , String tag){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(replace) {
            // "fragmentTransaction.replace" replaces the current fragment with a new one
            fragmentTransaction.replace(R.id.fragment_container_fl, fragment,tag);
        }else {
            // "fragmentTransaction.add" adds a new fragment on top of the current one
            // (using the stack illustration )
           fragmentTransaction.add(R.id.fragment_container_fl, fragment,tag);
        }
        fragmentTransaction.addToBackStack(null);
         fragmentTransaction.commit();
    }

    boolean areGuestsCheckedIn(){
        // check if guest data exists in our database
        // if yes, return true
        // if no, return false

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<GuestCheckedInData> guestCheckedInData = realm.where(GuestCheckedInData.class).findAll();
        Log.e("Data", "onResume: " + guestCheckedInData.size());

        if (guestCheckedInData.size() == 0){
            return false;
        } else {
            return true;
        }
    }
}
