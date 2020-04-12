package Seller;

import Adapters.PlacesAutoCompleteAdapter;

import Buyer.BuyeProfileCreation;
import Buyer.MainActivity;
import Models.mBuyerProfile;
import Models.mSellerProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SellerProfileCreation extends AppCompatActivity {
    private EditText etSellerName,etShopName,etSellerPhone;//,etSellerLocality;
    private Button btnRegisterSeller;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    Location userLoc;
    double lat,lng;
    private Models.mSellerProfile mSellerProfile=null;
    AutoCompleteTextView autoCompleteTextView;
    private ProgressDialog progressDialog;
    private Boolean existence=false;
//    AutocompleteSupportFragment autocompleteFragment;
//    private  static String TAG="PlacesActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profileas_seller);
        progressDialog=new ProgressDialog(this);
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

        autoCompleteTextView=findViewById(R.id.autoLoc);
        autoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(SellerProfileCreation.this,android.R.layout.simple_list_item_1));
        etSellerName=findViewById(R.id.etSellerName);
        etShopName=findViewById(R.id.etShopName);
       // etSellerLocality=findViewById(R.id.etSellerLocality);
        etSellerPhone=findViewById(R.id.etSellerPhone);
        btnRegisterSeller=findViewById(R.id.btnRegisterSeller);
        userLoc = new Location(LocationManager.GPS_PROVIDER);

        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(SellerProfileCreation.this);
        fetchLastLoc();
        Retriveinfo();

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
                mSellerProfile.setLoc(autoCompleteTextView.getText().toString());
                if(mSellerProfile.getLoc().isEmpty())
                {
                    autoCompleteTextView.setError("Locality is required");
                    return;
                }
                try {
                    geoLocate(view);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SellerProfileCreation.this, "No such location found", Toast.LENGTH_SHORT).show();
                }

               mSellerProfile.setUid(fauth.getCurrentUser().getUid());
               while (Double.toString(lat)==null && Double.toString(lng)==null && mSellerProfile.getUid()!=null);
                DocumentReference documentReference=fstore.collection("Seller").document(mSellerProfile.getUid());
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("selname",mSellerProfile.getSelname());
                profilemap.put("shopname",mSellerProfile.getShopname());
                profilemap.put("custcare",mSellerProfile.getCustcare());
                profilemap.put("loc",mSellerProfile.getLoc());
//                mSellerProfile.setLat(Double.toString(userLoc.getLatitude()));
//                mSellerProfile.setLng(Double.toString(userLoc.getLongitude()));
                profilemap.put("lat",Double.toString(lat));
                profilemap.put("lng",Double.toString(lng));
                profilemap.put("uid",mSellerProfile.getUid());
                profilemap.put("token", SplashActivity.token);

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
        String location=autoCompleteTextView.getText().toString();
        Geocoder gc=new Geocoder(SellerProfileCreation.this);
        List<Address> list=gc.getFromLocationName(location,1);
        Address address=list.get(0);
        String locality=address.getLocality();
        lat=address.getLatitude();
        lng=address.getLongitude();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                if (existence == true) {

                    Intent intent = new Intent(SellerProfileCreation.this, SellerChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
}

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }
    private void Retriveinfo() {
        String curruserid=  curruserid = fauth.getCurrentUser().getUid();
        //Toast.makeText(SellerProfileCreation.this,curruserid,Toast.LENGTH_LONG).show();
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We Are Fetching Your Information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        fstore= FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("Seller").document(curruserid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        existence=true;
                        btnRegisterSeller.setText("Update Profile");
                        mSellerProfile = document.toObject(Models.mSellerProfile.class);
                        setExistingData(mSellerProfile);
                        //setLayoutWidgets(mBuyerPrsetExistingData(mBuyerProfile);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SellerProfileCreation.this,"No data history found",Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SellerProfileCreation.this,"No data history found task failed",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void setExistingData(Models.mSellerProfile mSellerProfile) {
        etSellerName.setText(mSellerProfile.getSelname());
        etSellerPhone.setText(mSellerProfile.getCustcare());
        etShopName.setText(mSellerProfile.getShopname());
        autoCompleteTextView.setText(mSellerProfile.getLoc());
        progressDialog.dismiss();


    }
}




