package com.expresspay.access_control;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.expresspay.access_control.models.GuestData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TotalCheckedOut extends Fragment {
    private RecyclerView checkedOutRecyclerView;
    private GuestAdapter adapter;

    private EditText searchList;


    //define the guestData
    List<GuestCheckedInData> guestDataList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_total_checked_out,container,false);

        searchList =  view.findViewById(R.id.searchList);
        checkedOutRecyclerView = view.findViewById(R.id.checkedOut_recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("Checked Out", "on view created called. items in list "+ guestDataList.size());

        checkedOutRecyclerView.setHasFixedSize(true);
        checkedOutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //set adapter to this recycler view
        adapter = new GuestAdapter(guestDataList, getActivity());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        checkedOutRecyclerView.addItemDecoration(dividerItemDecoration);


        checkedOutRecyclerView.setAdapter(adapter);

        searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable ) {
                filter(editable.toString());
            }
        });

    }
    private void filter(String text){
        List<GuestCheckedInData> filteredList = new ArrayList<>();

        for(GuestCheckedInData item : guestDataList){
            if(item.getVisitorName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }


    public void fetchCheckedOutGuest(){
        //create an instance of the realm
        Realm realm = Realm.getDefaultInstance();
        //fetch data from the database
        final RealmResults<GuestCheckedInData> guestCheckedInData = realm.where(GuestCheckedInData.class)
                .equalTo("checkedOut",true).findAll();

        guestDataList = new ArrayList<>(realm.copyFromRealm(guestCheckedInData));


        //close the database
        realm.close();

        // this tells the adapter that the data  has changed so it should reload the list
        adapter.update(guestDataList);
        Collections.reverse(guestDataList);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Checked Out", "on resume called. items in list "+ adapter.getItemCount());
        fetchCheckedOutGuest();
    }


}

