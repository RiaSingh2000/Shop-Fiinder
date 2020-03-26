package com.codepth.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.location.Location.*;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

     private FirebaseFirestore db=FirebaseFirestore.getInstance();
     private CollectionReference sellerRef = db.collection("Seller");
    Location userLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    int sel=0,findShop=0; //flag variables

    private static final String[] options=new String[]{
            "Near Current Location","Near Registered Location"
    };
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fetchLastLoc();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = null;
        LatLng shopLatLng =null;

        if(sel ==0 && findShop==0 ) //initial case
        {
            Log.d(TAG,"sel==-------------------------------------"+sel);
            latLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
        }
        else if( sel ==0 && findShop==1 ){ //select shops nearby current location
            if(latLng==null){
                latLng=new LatLng(userLoc.getLatitude(),userLoc.getLongitude());
                //TODO: fetch shops latLong one by one from firestore and run
                Log.d(TAG,"BEGINNING -------------------------------------");
                //fetchShopsMapDetails(); //TODO fix firestore as per structure required by this function
                Log.d(TAG,"BEGINNING =====================================");
                float[] result = new float[3];
                Location.distanceBetween(userLoc.getLatitude(), userLoc.getLongitude(), 20.353270, 85.826740, result );
                if(result!=null && result[0]<=7000){
                    Toast.makeText(this,"Nearby shop found",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this,"XX NO Nearby shop found XX"+result[0],Toast.LENGTH_LONG).show();
                }
            }

        }
        else {
            latLng = new LatLng(20.2960587, 85.8223511); //user's registered location
            //shopLatLng = new LatLng(20.353270,85.826740); shop's registered location
            float[] result = new float[3];
            Location.distanceBetween(20.2960587, 85.8223511, 20.353270, 85.826740, result );
            if(result!=null && result.length>0 && result[0]<=7000){
                Toast.makeText(this,"Nearby atmaram",Toast.LENGTH_LONG).show();
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(20.353270, 85.826740))
                        .title("Atma Ram")
                        .snippet("A NEARBY SHOP"));
            }
            else{
                Toast.makeText(this,"XX atmaram not nearby XX"+ result[0],Toast.LENGTH_LONG).show();
            }
        }
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("I am here"); //icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_black_24dp));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(markerOptions);
    }
    public void fetchLastLoc(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]
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
                    SupportMapFragment supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0] ==PackageManager.PERMISSION_GRANTED);
                fetchLastLoc();
                break;
        }
    }

    int pos=0;

    public void openDialog(View view)
    {
        int id=view.getId();
        if (id==R.id.find_shop)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("How Do You Want to Find Shops");
            builder.setSingleChoiceItems(options, pos, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int index)
                {
                    sel=index;
                    pos=index;
                    findShop=1;
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int index)
                {
                    fetchLastLoc();
                }
            });
            builder.setNegativeButton("CANCEL",null);
            builder.show();
        }
    }

    void fetchShopsMapDetails(){ //gets the name of shops and its longitude and latitude values from firestore
         final ArrayList<mShops> mShopsArrayList = new ArrayList<mShops>();
        db.collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mShops mshops = (mShops) document.getData();
                                mShopsArrayList.add(mshops);
                                Log.d(TAG, String.valueOf(mShopsArrayList));

                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}
