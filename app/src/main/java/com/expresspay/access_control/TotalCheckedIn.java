package com.expresspay.access_control;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.expresspay.access_control.models.GuestItem;
import com.expresspay.access_control.models.DateItem;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.expresspay.access_control.models.ListItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class TotalCheckedIn extends Fragment {

    //define the guestData
    private List<GuestCheckedInData> guestDataList = new ArrayList<>();
    private List<ListItem> consolidatedList = new ArrayList<>();

    //create objects for recyclerView and the adapter
    private RecyclerView checkedInRecyclerView;
    private GuestAdapter adapter;

    private EditText searchList;
    private SwipeRefreshLayout pullToRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_checked_in, container, false);
//reference the objects
        searchList = view.findViewById(R.id.searchList);
        checkedInRecyclerView = view.findViewById(R.id.checkedIn_recyclerVew);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Checked In", "on view created called. items in list "+ guestDataList.size());

        //set the fix size to true(every item of the recycler view has a fixed size)
        checkedInRecyclerView.setHasFixedSize(true);
        checkedInRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get guest list
        adapter = new GuestAdapter(consolidatedList, getActivity());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        checkedInRecyclerView.addItemDecoration(dividerItemDecoration);

        //set adapter to this recycler view
        checkedInRecyclerView.setAdapter(adapter);

//update the list items from the database
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchGuestDataFromApi();
            }
        });

//search through the guests lists using their names
        searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable ) {
                filterGuestData(editable.toString());
            }
        });

    }
    //function to filter the guestsLists data
    private void filterGuestData(String text){
        List<GuestCheckedInData> filteredGuestList = new ArrayList<>();

        for(GuestCheckedInData item : guestDataList){
            if(item.getVisitorName().toLowerCase().contains(text.toLowerCase())){
                filteredGuestList.add(item);
            }
        }




        // group data according to date
        HashMap<String, List<GuestCheckedInData>> groupedListMap = groupDataIntoHashMap(filteredGuestList);

        // convert map back into list for our adapter
        List<ListItem> consolidatedList = consolidatedGuestList(groupedListMap);

        adapter.filterGuestDataList(consolidatedList);

    }

//function to fetch guests data from the api server
    //add to the local database
    private void fetchGuestDataFromApi(){
        pullToRefresh.setRefreshing(true);

        String server_url = AppConstants.BASE_URL+"?request="+AppConstants.GET_ALL_GUESTS+"&api_access_key="+AppConstants.API_ACCESS_KEY;
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", "ResponseBody" + response);
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if(status.equals("0")) {
                                JSONObject guestsObject = response.getJSONObject("output");
                                //getting the json array(guests) needed from the response
                                JSONArray guestsArray = guestsObject.getJSONArray("guests");
                                Gson gson = new Gson();
                                Type guestListType = new TypeToken<ArrayList<GuestCheckedInData>>() {
                                }.getType();
                                List<GuestCheckedInData> guests = gson.fromJson(guestsArray.toString(), guestListType);


                                for (GuestCheckedInData guest : guests) {
                                    Log.e("CheckTime", "GuestCheckTime" + "  " + guest.getCheckedInTime() + "  " + guest.getCheckedOutTime());
                                    addGuestsDataToDataBase(guests);

                                    pullToRefresh.setRefreshing(false);
                                }

                            }else {
                                Log.d("message","message"+" "+ message);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error message", "Something is wrong" + error);

            }
        });

        requestQueue.add(jsonObjectRequest);

    }



//function to update the local database
    Realm realm = Realm.getDefaultInstance();
    private void addGuestsDataToDataBase(final List<GuestCheckedInData> guests){
        pullToRefresh.setRefreshing(false);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(guests);

            }
        });
            adapter.updateCountAndListContents();
    }


//function to fetch checkIn data from the database
    public void fetchCheckedInGuests(){

        Realm realm = Realm.getDefaultInstance();
        //fetch data from the database
        final RealmResults<GuestCheckedInData> guestCheckedInData = realm.where(GuestCheckedInData.class)
                .equalTo("checkedOut",false)
                .findAll();

        guestDataList = new ArrayList<>(realm.copyFromRealm(guestCheckedInData));
        Collections.reverse(guestDataList);

        //close the database
        realm.close();

        // group data according to date
        HashMap<String, List<GuestCheckedInData>> groupedListMap = groupDataIntoHashMap(guestDataList);

        // convert map back into list for our adapter
        consolidatedList = consolidatedGuestList(groupedListMap);

        // this tells the adapter that the data  has changed so it should reload the list that contains the RealmResult
        adapter.update(consolidatedList);

    }


//function to group guests data into Hash map(key,value)
//key = checkInTime && value =  guest data
    private HashMap<String,List<GuestCheckedInData>>
    groupDataIntoHashMap(List<GuestCheckedInData> guestDataList) {

        //create an empty HashMap
        HashMap<String, List<GuestCheckedInData>> groupedHashedMap = new HashMap<>();

        for(GuestCheckedInData guestCheckedInData : guestDataList){
            String checkInTime = formatTime(guestCheckedInData.getCheckedInTime());
            String hashMapKey = checkInTime;

            if(groupedHashedMap.containsKey(hashMapKey)){
                // The key(the date) is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashedMap.get(hashMapKey).add(guestCheckedInData);
            }else {
                //create a new list and then add the key-value pair
                List<GuestCheckedInData> guestList = new ArrayList<>();
                guestList.add(guestCheckedInData);
                groupedHashedMap.put(hashMapKey,guestList);
            }

        }
        return groupedHashedMap;
    }


    private List<ListItem> consolidatedGuestList(HashMap<String,List<GuestCheckedInData>> groupedData){

        List<ListItem> consolidatedList = new ArrayList<>();

        for(String date : groupedData.keySet()){
            DateItem dateItem = new DateItem();
            dateItem.setDate(date);
            consolidatedList.add(dateItem);

            for(GuestCheckedInData guestCheckedInData : groupedData.get(date)) {
                GuestItem guestDataItems = new GuestItem();
                guestDataItems.setGuestCheckedInData(guestCheckedInData);
                consolidatedList.add(guestDataItems);
            }

        }

        return consolidatedList;
    }


    private String formatTime(String dateTime){
        String formattedTime;
        try {
            Date date = new Date(Long.parseLong(dateTime));
            formattedTime = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
        }catch (Exception e){
            //if an error error occurs while formatting the date
            e.printStackTrace();
            formattedTime = "";
        }
        return formattedTime;
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchCheckedInGuests();
        Log.e("Checked In", "on resume called. items in list "+ adapter.getItemCount());
    }
}

