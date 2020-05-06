package com.expresspay.access_control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        requestDataFromApi();


        //start the loading fragment based on whether guests are logged in or not
          //  loadAppropriateFragment();
    }

    private void requestDataFromApi(){
        String server_url = "http://10.0.2.2/exp-iris/api/iris.php?request=get_all_guests";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", "ResponseBody" + response);
                        try {
                            JSONObject guestsObject = response.getJSONObject("output");
                            //getting the json array(guests) needed from the response
                            JSONArray guestsArray = guestsObject.getJSONArray("guests");

                            Gson gson = new Gson();
                            Type guestListType = new TypeToken<ArrayList<GuestCheckedInData>>(){}.getType();
                            List<GuestCheckedInData> guests = gson.fromJson(guestsArray.toString(),guestListType);
                            Log.d("count", "count"+ " "+ guests.size());

                            for(GuestCheckedInData guest : guests){
                                Log.e("CheckTime","GuestCheckTime" + "  "+ guest.getCheckedInTime() + "  "+ guest.getCheckedOutTime());
                            }
                            addGuestsDataToDataBase(guests);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(

        ) {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error message", "Something is wrong" + error);
            }
        }
        );

        requestQueue.add(jsonObjectRequest);



    }



    private void addGuestsDataToDataBase(final List<GuestCheckedInData> guests){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(guests);

                ProgressBar spinner;
                spinner = findViewById(R.id.spinner);
                        spinner.setVisibility(View.GONE);
                loadAppropriateFragment();

            }
        });




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

    boolean areGuestsCheckedIn() {
        // check if guest data exists in our database
        // if yes, return true
        // if no, return false

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<GuestCheckedInData> guestCheckedInData = realm.where(GuestCheckedInData.class).findAll();
        Log.e("Data", "onResume: " + guestCheckedInData.size());

        if (guestCheckedInData.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
