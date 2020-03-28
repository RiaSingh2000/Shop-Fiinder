package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SellerProfileCreation extends AppCompatActivity {
    private EditText etSellerName,etShopName,etSellerPhone,etSellerLocality;
    private Button btnRegisterSeller;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    Location userLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profileas_seller);
        etSellerName=findViewById(R.id.etSellerName);
        etShopName=findViewById(R.id.etShopName);
        etSellerLocality=findViewById(R.id.etSellerLocality);
        etSellerPhone=findViewById(R.id.etSellerPhone);
        btnRegisterSeller=findViewById(R.id.btnRegisterSeller);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(SellerProfileCreation.this);
        fetchLastLoc();


        final mSellerProfile mSellerProfile = new mSellerProfile();
        btnRegisterSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSellerProfile.setSellerName(etSellerName.getText().toString());
                if(mSellerProfile.getSellerName().isEmpty())
                {
                    etSellerName.setError("Name is Required");
                    return ;
                }
                mSellerProfile.setShopName(etShopName.getText().toString());
                if(mSellerProfile.getShopName().isEmpty())
                {
                    etShopName.setError("ShopName is Required");
                    return ;
                }

                mSellerProfile.setSellerPhone(etSellerPhone.getText().toString());
                if(mSellerProfile.getSellerPhone().isEmpty())
                {
                    etSellerPhone.setError("Phone number is required");
                    return;
                }
                mSellerProfile.setSellerLocality(etSellerLocality.getText().toString());
                if(mSellerProfile.getSellerLocality().isEmpty())
                {
                    etSellerLocality.setError("Locality is required");
                    return;
                }
               mSellerProfile.setSellerId(fauth.getCurrentUser().getUid());
                DocumentReference documentReference=fstore.collection("Seller").document(mSellerProfile.getSellerId());
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("selname",mSellerProfile.getSellerName());
                profilemap.put("shopname",mSellerProfile.getShopName());
                profilemap.put("custcare",mSellerProfile.getSellerPhone());
                profilemap.put("loc",mSellerProfile.getSellerLocality());
                mSellerProfile.setSellerLat(Double.toString(userLoc.getLatitude()));
                mSellerProfile.setSellerLong(Double.toString(userLoc.getLongitude()));
//                profilemap.put("lt",mSellerProfile.getSellerLat());
//                profilemap.put("ln",mSellerProfile.getSellerLong());
                profilemap.put("lat",Double.toString(userLoc.getLatitude()));
                profilemap.put("lng",Double.toString(userLoc.getLongitude()));

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
}




