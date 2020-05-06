package com.expresspay.access_control;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;

import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;

public class CheckInPopulatedStateFragment extends Fragment {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;


    private static final String CHECKED_IN = "Checked In";
    private static final String CHECKED_OUT = "Checked Out";

    private int checkedInCount = 0;
    private int checkedOutCount = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in_populated_state, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        fab = view.findViewById(R.id.fab);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        collapsingToolbar = view.findViewById(R.id.collapsingToolbar);


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout.addTab(tabLayout.newTab().setText(generateCountMsg(checkedInCount, false)));
        tabLayout.addTab(tabLayout.newTab().setText(generateCountMsg(checkedOutCount, true)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final TabViewAdapter adapter = new TabViewAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckInActivity.class);
                getContext().startActivity(intent);
            }
        });

        //hide and show title based on whether toolbar is expanded
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isShow = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //set  title only if the toolbar is collapsed
                    collapsingToolbar.setTitle("expressPay");
                    //  toolbar.setTitleTextColor(Color.WHITE);
                    // toolbar.setTitleTextColor(0xFFFFFF);
                } else if (!isShow) {
                    //set the title to an empty string to hide it
                    //when the toolbar is expanded
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUpdateCount();

    }

    //updating the checkingIn and checkedOut count
    void getUpdateCount() {
        Realm realm = Realm.getDefaultInstance();

        //fetch checkedIn users from the database and check the size
        // the size is equal to the number items

        final int guestCheckedInDataCount = realm.where(GuestCheckedInData.class)
                .equalTo("checkedOut", false)
                .findAll().size();

        // fetch checkedUut users from the database and check the size
        // the size is equal to the number of items
        final int guestCheckedOutDataCount = realm.where(GuestCheckedInData.class)
                .equalTo("checkedOut", true)
                .findAll().size();

        realm.close();

        checkedInCount = guestCheckedInDataCount;
        checkedOutCount = guestCheckedOutDataCount;

        // log the count values
        Log.e("COUNT", "checked in guests => " + checkedInCount + "\nchecked out guests => " + checkedOutCount);

        // get the first tab and update the count
        tabLayout.getTabAt(0).setText(generateCountMsg(checkedInCount, false));

        // get the second tab and update the count
        tabLayout.getTabAt(1).setText(generateCountMsg(checkedOutCount, true));
    }

    //function to generate tab count message
    String generateCountMsg(int count, boolean checkedOut) {
        if (checkedOut) {
            return count + " " + CHECKED_OUT;
        } else {
            return count + " " + CHECKED_IN;
        }
    }




}
