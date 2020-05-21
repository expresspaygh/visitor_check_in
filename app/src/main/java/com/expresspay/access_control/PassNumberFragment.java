package com.expresspay.access_control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.expresspay.access_control.models.CheckInData;
import com.expresspay.access_control.models.GuestCheckedInData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class PassNumberFragment extends Fragment {

    private ImageButton backButton;
    private Button checkInButton;
    private TextInputEditText passNumber;
    private ProgressBar spinner;

    private String  passNum ;
    private CheckInData checkInData;
    GuestCheckedInData guestCheckedInData = new GuestCheckedInData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!= null){
            checkInData = (CheckInData) getArguments().getSerializable("staff_data");
        }
    }

    @Nullable
    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passnumber,container,false);
        backButton= view.findViewById(R.id.back_imageButton);
        checkInButton = view.findViewById(R.id.checkedIn_btn);
        passNumber = view.findViewById(R.id.pass_Edt);
        spinner = view.findViewById(R.id.spinner);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    getActivity().onBackPressed();
                }
            }
        });

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){



                    String currentTime = String.valueOf(System.currentTimeMillis());
                    Log.e("Timestamp", currentTime);


                    guestCheckedInData.setVisitorName(checkInData.getVisitorName());
                    guestCheckedInData.setVisitorPhone(checkInData.getVisitorPhone());
                    guestCheckedInData.setStaffName(checkInData.getStaffName());
                    guestCheckedInData.setPurpose(checkInData.getPurpose());
                    guestCheckedInData.setPassNumber(passNum);
                    guestCheckedInData.setCheckedInTime(currentTime);
                    guestCheckedInData.setCheckedOutTime("");
                    guestCheckedInData.setCheckedOut(false);

                  spinner.setVisibility(View.VISIBLE);
                      postDataToApi();

                }
                else {
                    Toast.makeText(v.getContext(), "Pass Number  Not Filled ", Toast.LENGTH_SHORT).show();

                }
            }
        });
  }

        private boolean validate(){
        passNum = passNumber.getText().toString();

        return !passNum.isEmpty();
        }

        private void checkInGuestAlertDialog(String dialogMessage){
        if(dialogMessage == null){
            dialogMessage = "Checking In a guest failed ";
        }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(dialogMessage);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    postDataToApi();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            //creating dialog box
           AlertDialog alert = builder.create();
           alert.setTitle("Error");
           alert.show();

        }



        private void saveToDataBase(){
        spinner.setVisibility(View.GONE);

        checkInData.setPassNumber( passNum );
            Log.e("anything", "saveToDataBase: " + checkInData.getVisitorName());
            Log.e("anything", "saveToDataBase: " + checkInData.getVisitorPhone());
            Log.e("anything", "saveToDataBase: " + checkInData.getStaffName());
            Log.e("anything", "saveToDataBase: " + checkInData.getPurpose());
            Log.e("anything", "saveToDataBase: " + checkInData.getPassNumber());

            // insert into db
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {


                    realm.insert(guestCheckedInData);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_LONG).show();
                }


            });

        }

        private void postDataToApi() {
        //GET parameters
            HashMap<String,String> params = new HashMap<String, String>();

            params.put("visitor_name", guestCheckedInData.getVisitorName());
            params.put("visitor_phone",guestCheckedInData.getVisitorPhone());
            params.put("staff_name",guestCheckedInData.getStaffName());
            params.put("purpose",guestCheckedInData.getPurpose());
            params.put("pass_number",guestCheckedInData.getPassNumber());
            params.put("check_in_time",guestCheckedInData.getCheckedInTime());
            params.put("check_out_time", guestCheckedInData.getCheckedOutTime());

            if(guestCheckedInData.isCheckedOut() == true){
                params.put("is_checked_in","true" );
            }else{
                params.put("is_checked_in","false");
            }

            Log.e("params", "params"+ params);
//create a new json object
            JSONObject parameters = new JSONObject(params);


            String server_url = getString(R.string.check_in_url);
            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, parameters,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Response", "ResponseBody" + response);
                            try {
                               String status = response.getString("status");
                               String message = response.getString("message");
                               if(status.equals("0")){
                                   saveToDataBase();
                               }else {
                                   checkInGuestAlertDialog(message);
                                   Log.e("Message","message"+ " " + message);
                               }


                       } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener(

            ) {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error message", "Something is wrong" + error.getMessage());
                    checkInGuestAlertDialog(null);
                }
            }
            ){
                @Override
                protected Map<String, String> getParams()  {

                    HashMap<String,String> params = new HashMap<String, String>();

                  params.put("visitor_name", guestCheckedInData.getVisitorName());
                    params.put("visitor_phone",guestCheckedInData.getVisitorPhone());
                    params.put("staff_name",guestCheckedInData.getStaffName());
                    params.put("purpose",guestCheckedInData.getPurpose());
                    params.put("check_in_time",guestCheckedInData.getCheckedInTime());
                    params.put("check_out_time", guestCheckedInData.getCheckedOutTime());
                    params.put("pass_number",guestCheckedInData.getPassNumber());

                    if(guestCheckedInData.isCheckedOut() == true){
                        params.put("is_checked_in","true" );
                    }else{
                        params.put("is_checked_in","false");
                    }

        Log.e("params", "params"+ params);
                    return params;
                }
            };

            requestQueue.add(jsonObjectRequest);

        }



        }
