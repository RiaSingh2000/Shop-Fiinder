package Buyer;

import Common.DrawerController;
import Models.mSellerProfile;
import Models.mShops;
import Seller.SellerDisplayActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.codepth.maps.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity  extends FragmentActivity  implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //FIREBASE
     private FirebaseFirestore db=FirebaseFirestore.getInstance();
     private CollectionReference sellerRef = db.collection("Seller");
    FusedLocationProviderClient fusedLocationProviderClient;

    //WIDGETS AND LAYOUTS
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

     Location userLoc = null;
     int sel=0,findShop=0; //flag variables
     FirebaseFirestore fstore;
     FirebaseAuth fauth;
     private static LatLng latLng = null;
     ArrayList<mShops> mShopsArrayList = new ArrayList<>();

    private static final int REQUEST_CODE=101;
    private static final String[] options=new String[]{
            "Near Current Location","Near Registered Location"
    };
    private static final String TAG = "MainActivity";
    private GoogleMap googleMap;
    MarkerOptions markerOptions = null;
    private MarkerOptions markerOptions3= null;

    public  static  LatLng getLatLng(){
        return latLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerController.setIdentity("MainActivity");
        navView = findViewById(R.id.nv);
        navView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.activity_main_drawerlayout);
        Toolbar toolbar=findViewById(R.id.toolbar);
        progressDialog=new ProgressDialog(this);
        //setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        fstore=FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fetchLastLoc();

        DocumentReference documentReference=fstore.collection("Buyer").document(fauth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    double lat,lon;
                    DocumentSnapshot doc = task.getResult();
                    lat=Double.parseDouble(doc.get("lat").toString());
                    lon=Double.parseDouble(doc.get("lng").toString());
                    latLng = new LatLng(lat,lon);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        while(userLoc==null);
        LatLng shopLatLng =null;
        if(sel ==0 && findShop==0 ) //initial case
        {

            latLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
            markerOptions=new MarkerOptions().position(latLng).title("I am here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            googleMap.addMarker(markerOptions);
            markerOptions.visible(true);
        }
        else if( sel ==0 && findShop==1 ){ //select shops nearby current location
           // if(latLng==null){
                googleMap.clear();
                double lat,lon;
                lat=userLoc.getLatitude();
                lon=userLoc.getLongitude();
                latLng = new LatLng(lat,lon);
                markerOptions=new MarkerOptions().position(latLng).title("I am here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                googleMap.addMarker(markerOptions);
                markerOptions.visible(true);
                fetchShopsMapDetails();
                    Log.w(TAG, "In main" + "\n");

          //  }

        }
        else { //select shops nearby registered location
            DocumentReference documentReference=fstore.collection("Buyer").document(fauth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        double lat,lon;
                        DocumentSnapshot doc = task.getResult();
                        lat=Double.parseDouble(doc.get("lat").toString());
                        lon=Double.parseDouble(doc.get("lng").toString());
                        latLng = new LatLng(lat,lon);
                        while (latLng==null);
                        addRegLocationMarker();
                        fetchShopsMapDetails();
                    }
                }
            });


        }
    }

    private void addRegLocationMarker() {
        googleMap.clear();
         markerOptions3=new MarkerOptions().position(latLng).title("My Registered location"); //icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_black_24dp));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(markerOptions3);
        markerOptions3.visible(true);
    }

    private void calculateAndPlotNearbyShops(@NonNull LatLng ll , ArrayList<mShops> mShopsArrayList) {
       int avail =0;
        for(int i=0; i< mShopsArrayList.size() ; i++ ){
           // Log.w(TAG,"Inside calculate" +mShopsArrayList.get(i).getLatitude()+"\n");
            float[] result = new float[3];
            Location.distanceBetween((float)ll.latitude,(float)ll.longitude, Float.parseFloat( mShopsArrayList.get(i).getLatitude()),
                    Float.parseFloat( mShopsArrayList.get(i).getLongitude()), result);
            if(result!=null && result[0]>=0 ){
                avail=1;
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Float.parseFloat( mShopsArrayList.get(i).getLatitude()), Float.parseFloat( mShopsArrayList.get(i).getLongitude())))
                        .title(mShopsArrayList.get(i).getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                                snippet("A NEARBY SHOP"));
                marker.setTag(mShopsArrayList.get(i).getuId());

                googleMap.setOnMarkerClickListener(this);

            }
        }

    if(avail==0)
        Toast.makeText(this,"You have no nearby shops.Invite shops to register with us!",Toast.LENGTH_LONG).show();
    else
        Toast.makeText(this,"We have found nearby shops for you!",Toast.LENGTH_LONG).show();
    progressDialog.dismiss();
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
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We Are Fetching Nearby Shops...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        db.collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mSellerProfile mSellerProfile = document.toObject(Models.mSellerProfile.class);
                                mShops mShop = slice(mSellerProfile);
                                Log.d(TAG, String.valueOf(mShopsArrayList));
                                mShopsArrayList.add(mShop);
                            }

                            calculateAndPlotNearbyShops(latLng,mShopsArrayList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Error Getting Shops",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    return ; }

    private mShops slice(mSellerProfile mSellerProfile) {
        mShops mShop = new mShops();
        mShop.setLatitude(mSellerProfile.getLat());
        mShop.setLongitude(mSellerProfile.getLng());
        mShop.setName(mSellerProfile.getShopname());
        mShop.setuId(mSellerProfile.getUid());
     return mShop;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       // if(marker.equals(markerOptions) || marker.equals(markerOptions3)){
         if (marker.getTitle().equals("My Registered location") || marker.getTitle().equals("I am here")){
             marker.showInfoWindow();
            return  true;
        }
        else {
            if(marker.isVisible()) {
                //Log.w(TAG, "..........................................................................." + marker.getTag().toString());
                Intent intent = new Intent(this, SellerDisplayActivity.class);
                intent.putExtra("SellerUid", marker.getTag().toString());
                startActivity(intent);
                return false;
            }
            else
                return true;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mapDrawableItem :{
                if(DrawerController.toMainActivity(getApplicationContext())){
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.shopListDrawableItem :{
                if(DrawerController.toShopList(getApplicationContext())){
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.chatListDrawableItem :{
                if(DrawerController.toChatList(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.aboutUsDrawableList :{
                Toast.makeText(this,"TO BE DONE",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.rateUsDrawableList :{
                Toast.makeText(this,"TO BE DONE",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.settingsDrawableItem :{
                if(DrawerController.sendUserToSettingActivity(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.logoutDrawableItem :{
                FirebaseAuth.getInstance().signOut();
                if(DrawerController.sendUsertologinactivity(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
        }
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
