package Buyer;

import Adapters.PlacesAutoCompleteAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class BuyeProfileCreation extends AppCompatActivity {
    private EditText name,phn,street,house;
    AutoCompleteTextView locality;
    private Button Create_pofile;
    String userid,nm,pn,Street,loc,phone,House;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    Location userLoc=null;
    double lat,lng;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        name=findViewById(R.id.etName);
        phn=findViewById(R.id.etPhone);
        street=findViewById(R.id.etStreet);
        locality=findViewById(R.id.etLocality);
        house=findViewById(R.id.etHouse);
        Create_pofile=findViewById(R.id.btRegister);
        fauth=FirebaseAuth.getInstance();
        userLoc = new Location(LocationManager.GPS_PROVIDER);
        fstore=FirebaseFirestore.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(BuyeProfileCreation.this);
        fetchLastLoc();
        locality.setAdapter(new PlacesAutoCompleteAdapter(BuyeProfileCreation.this,android.R.layout.simple_list_item_1));


        Create_pofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nm=name.getText().toString();
                if(nm.isEmpty())
                {
                    name.setError("Name is Required");
                    return ;
                }
                phone=phn.getText().toString();
                if(phone.isEmpty())
                {
                    phn.setError("Phone number is required");
                    return;
                }
                Street=street.getText().toString();
                if(Street.isEmpty())
                {
                    street.setError("Street number is required");
                    return;
                }
                loc=locality.getText().toString();
                if(loc.isEmpty())
                {
                    locality.setError("Locality is required");
                    return;
                }
                House=house.getText().toString();
                if(House.isEmpty())
                {
                    house.setError("House detail is required");
                    return;
                }
                try {
                    geoLocate(view);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(BuyeProfileCreation.this, "No such location found", Toast.LENGTH_SHORT).show();
                }

                userid=fauth.getCurrentUser().getUid();
                DocumentReference documentReference=fstore.collection("Buyer").document(userid);
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("uid",userid);
                profilemap.put("name",nm);
                profilemap.put("Phone number",phone);
                profilemap.put("Street",Street);
                profilemap.put("Locality",loc);
                profilemap.put("House",House);
                while (userLoc==null);
//                profilemap.put("lat",Double.toString(userLoc.getLatitude()));
//                profilemap.put("lng",Double.toString(userLoc.getLongitude()));
                profilemap.put("lat",String.valueOf(lat));
                profilemap.put("lng",String.valueOf(lng));

                documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BuyeProfileCreation.this,"Profile set up Successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuyeProfileCreation.this, MainActivity.class);
                        intent.putExtra("lat",userLoc.getLatitude());
                        intent.putExtra("lng",userLoc.getLongitude());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                });


            }
        });
    }

    public void fetchLastLoc(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(BuyeProfileCreation.this,new String[]
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
        String location=locality.getText().toString();
        Geocoder gc=new Geocoder(BuyeProfileCreation.this);
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
                finish();
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



}



