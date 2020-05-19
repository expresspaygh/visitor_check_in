package com.expresspay.access_control;

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

import com.expresspay.access_control.models.GuestItem;
import com.expresspay.access_control.models.DateItem;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.expresspay.access_control.models.ListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class TotalCheckedOut extends Fragment {
    //define the guestData
    List<GuestCheckedInData> guestDataList = new ArrayList<>();
    private RecyclerView checkedOutRecyclerView;
    private GuestAdapter adapter;
    private EditText searchList;
    private SwipeRefreshLayout pullToRefresh;
    private List<ListItem> consolidatedList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_checked_out, container, false);

        searchList = view.findViewById(R.id.searchList);
        checkedOutRecyclerView = view.findViewById(R.id.checkedOut_recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Checked Out", "on view created called. items in list " + guestDataList.size());

        checkedOutRecyclerView.setHasFixedSize(true);
        checkedOutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //set adapter to this recycler view
        adapter = new GuestAdapter(consolidatedList, getActivity());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        checkedOutRecyclerView.addItemDecoration(dividerItemDecoration);


        checkedOutRecyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCheckOutDataFromDataBase();
            }
        });

        searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterGuestData(editable.toString());
            }
        });

    }

    private void filterGuestData(String text) {
        List<GuestCheckedInData> filteredGuestList = new ArrayList<>();

        for (GuestCheckedInData item : guestDataList) {
            if (item.getVisitorName().toLowerCase().contains(text.toLowerCase())) {
                filteredGuestList.add(item);
            }
        }

        // group data according to date
        HashMap<String, List<GuestCheckedInData>> groupedListMap = groupDataIntoHashMap(filteredGuestList);

        // convert map back into list for our adapter
        List<ListItem> consolidatedList = consolidatedGuestList(groupedListMap);

        adapter.filterGuestDataList(consolidatedList);
    }

    private void refreshCheckOutDataFromDataBase() {
        fetchCheckedOutGuest();
    }

    public void fetchCheckedOutGuest() {
        //create an instance of the realm
        Realm realm = Realm.getDefaultInstance();
        //fetch data from the database
        final RealmResults<GuestCheckedInData> guestCheckedInData = realm.where(GuestCheckedInData.class)
                .equalTo("checkedOut", true).findAll();

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
        pullToRefresh.setRefreshing(false);

    }

    private HashMap<String, List<GuestCheckedInData>>
    groupDataIntoHashMap(List<GuestCheckedInData> guestDataList) {

        //create an empty HashMap
        HashMap<String, List<GuestCheckedInData>> groupedHashedMap = new HashMap<>();

        for (GuestCheckedInData guestCheckedInData : guestDataList) {
            String checkInTime = formatTime(guestCheckedInData.getCheckedInTime());
            String hashMapKey = checkInTime;

            if (groupedHashedMap.containsKey(hashMapKey)) {
                // The key(the date) is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashedMap.get(hashMapKey).add(guestCheckedInData);
            } else {
                //create a new list and then add the key-value pair
                List<GuestCheckedInData> guestList = new ArrayList<>();
                guestList.add(guestCheckedInData);
                groupedHashedMap.put(hashMapKey, guestList);
            }

        }
        return groupedHashedMap;
    }


    private List<ListItem> consolidatedGuestList(HashMap<String, List<GuestCheckedInData>> groupedData) {

        List<ListItem> consolidatedList = new ArrayList<>();

        for (String date : groupedData.keySet()) {
            DateItem dateItem = new DateItem();
            dateItem.setDate(date);
            consolidatedList.add(dateItem);

            for (GuestCheckedInData guestCheckedInData : groupedData.get(date)) {
                GuestItem guestDataItems = new GuestItem();
                guestDataItems.setGuestCheckedInData(guestCheckedInData);
                consolidatedList.add(guestDataItems);
            }

        }

        return consolidatedList;
    }


    private String formatTime(String dateTime) {
        String formattedTime;
        try {
            Date date = new Date(Long.parseLong(dateTime));
            formattedTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            //if an error error occurs while formatting the date
            e.printStackTrace();
            formattedTime = "";
        }
        return formattedTime;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("Checked Out", "on resume called. items in list " + adapter.getItemCount());
        fetchCheckedOutGuest();
    }


}

