package com.codepth.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SellerProfileCreation extends AppCompatActivity {
    private EditText etSellerName,etShopName,etSellerPhone,etSellerLocality;
    private Button btnRegisterSeller;//,picker;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    Location userLoc;
    double lat,lng;
   // int PLACE_PICKER_REQUEST=1;
//    AutocompleteSupportFragment autocompleteFragment;
//    private  static String TAG="PlacesActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profileas_seller);
//Places
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), "AIzaSyC4oSY9sO_ta8qGwLO1oVj-0q6D3vZXMhE");
//        }
//        PlacesClient placesClient = Places.createClient(this);
//
//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//
//            @Override
//            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
//                Toast.makeText(SellerProfileCreation.this, place.getLatLng()+"", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });



        etSellerName=findViewById(R.id.etSellerName);
        etShopName=findViewById(R.id.etShopName);
        etSellerLocality=findViewById(R.id.etSellerLocality);
        etSellerPhone=findViewById(R.id.etSellerPhone);
        btnRegisterSeller=findViewById(R.id.btnRegisterSeller);
       // picker=findViewById(R.id.picker);

//        picker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(SellerProfileCreation.this),PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(SellerProfileCreation.this);
        fetchLastLoc();


        final mSellerProfile mSellerProfile = new mSellerProfile();
        btnRegisterSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSellerProfile.setSelname(etSellerName.getText().toString());
                if(mSellerProfile.getSelname().isEmpty())
                {
                    etSellerName.setError("Name is Required");
                    return ;
                }
                mSellerProfile.setShopname(etShopName.getText().toString());
                if(mSellerProfile.getShopname().isEmpty())
                {
                    etShopName.setError("ShopName is Required");
                    return ;
                }

                mSellerProfile.setCustcare(etSellerPhone.getText().toString());
                if(mSellerProfile.getCustcare().isEmpty())
                {
                    etSellerPhone.setError("Phone number is required");
                    return;
                }
                mSellerProfile.setLoc(etSellerLocality.getText().toString());
                if(mSellerProfile.getLoc().isEmpty())
                {
                    etSellerLocality.setError("Locality is required");
                    return;
                }
                try {
                    geoLocate(view);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SellerProfileCreation.this, "No such location found", Toast.LENGTH_SHORT).show();
                }

                mSellerProfile.setUid(fauth.getCurrentUser().getUid());
                while (Double.toString(lat)==null&&Double.toString(lng)==null);
                DocumentReference documentReference=fstore.collection("Seller").document(mSellerProfile.getUid());
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("selname",mSellerProfile.getSelname());
                profilemap.put("shopname",mSellerProfile.getShopname());
                profilemap.put("custcare",mSellerProfile.getCustcare());
                profilemap.put("loc",mSellerProfile.getLoc());
                mSellerProfile.setLat(Double.toString(userLoc.getLatitude()));
                mSellerProfile.setLng(Double.toString(userLoc.getLongitude()));
//                profilemap.put("lt",mSellerProfile.getSellerLat());
//                profilemap.put("ln",mSellerProfile.getSellerLong());
                profilemap.put("lat",Double.toString(lat));
                profilemap.put("lng",Double.toString(lng));

                //TODO: find latitude and longitude(string) for seller's locality and store it on firestore as in line 71 and 72
                //TODO: description of shop when added should be in a document inside a new collection pointed by each seller's document
                //TODO: The custcare and seller name can also be moved to this new document
                documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SellerProfileCreation.this,"Profile set up Successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SellerProfileCreation.this, SellerChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void fetchLastLoc() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SellerProfileCreation.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    userLoc=location;
                    Toast.makeText(getApplicationContext(),userLoc.getLatitude()+"\n"+userLoc.getLongitude(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        String location=etSellerLocality.getText().toString();
        Geocoder gc=new Geocoder(SellerProfileCreation.this);
        List<Address> list=gc.getFromLocationName(location,1);
        Address address=list.get(0);
        String locality=address.getLocality();
        lat=address.getLatitude();
        lng=address.getLongitude();
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place= PlacePicker.getPlace(data,SellerProfileCreation.this);
//                StringBuilder stringBuilder=new StringBuilder();
//                String lat=String.valueOf(place.getLatLng().latitude);
//                String lng=String.valueOf(place.getLatLng().longitude);
//                stringBuilder.append("LATITUDE:");
//                stringBuilder.append(lat);
//                stringBuilder.append("LONGITUDE:");
//                stringBuilder.append(lng);
//                etSellerLocality.setText(stringBuilder);
//
//            }
//        }
//    }
}




