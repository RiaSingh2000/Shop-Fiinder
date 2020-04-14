package Buyer;

import Adapters.PlacesAutoCompleteAdapter;
import Common.DrawerController;
import Models.mBuyerProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

public class BuyeProfileCreation extends AppCompatActivity  {
    private static final int REQUEST_CODE = 101;
    AutoCompleteTextView locality;
    private Button Create_pofile;
    private static String type = "Curr";
    String userid, nm, pn, Street, loc, phone, House,personName;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location userLoc = null;
    double lat, lng;
    String curLat , curLng;
    Boolean existence = false;
    private EditText name, phn, street, house;
    private ProgressDialog progressDialog;
    private TextView currentLocTv,hiddenTv;
    private mBuyerProfile mBuyerProfile = null;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        DrawerController.setIdentity("SettingsActivity");

        //    Places.initialize(BuyeProfileCreation.this,"AIzaSyBe1tmgpLujgxK64FfL7n0eNJaWIijdy58");
        //  PlacesClient placesClient = Places.createClient(BuyeProfileCreation.this);

        progressDialog = new ProgressDialog(this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(BuyeProfileCreation.this);
        if (acct != null) {
             personName = acct.getDisplayName();
        }
        name = findViewById(R.id.etName);
        name.setText(personName);
        phn = findViewById(R.id.etPhone);
        street = findViewById(R.id.etStreet);
        locality = (AutoCompleteTextView) findViewById(R.id.autoLoc);
        house = findViewById(R.id.etHouse);
        Create_pofile = findViewById(R.id.btRegister);
        currentLocTv = findViewById(R.id.currentLocTv);
        hiddenTv= findViewById(R.id.HiddenTv);
        fauth = FirebaseAuth.getInstance();
        userLoc = new Location(LocationManager.GPS_PROVIDER);
        userid = fauth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();

        //BACK TO CURRENT LOCATION
        hiddenTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locality.setVisibility(View.INVISIBLE);
                currentLocTv.setVisibility(View.VISIBLE);
                hiddenTv.setVisibility(View.INVISIBLE);
                BuyeProfileCreation.type="Curr";
            }
        });


        currentLocTv.setOnClickListener(new View.OnClickListener() {
            //CHOOSING OTHER LOCATION THAN CURRENT
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyeProfileCreation.this);
                builder.setMessage(R.string.ask_for_autocomplete)
                        .setPositiveButton("Yes,change!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                locality.setVisibility(View.VISIBLE);
                                currentLocTv.setVisibility(View.INVISIBLE);
                                hiddenTv.setVisibility(View.VISIBLE);
                                BuyeProfileCreation.type="Reg";
                            }
                        })
                        .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                BuyeProfileCreation.type="Curr";
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BuyeProfileCreation.this);
        fetchLastLoc();
        locality.setAdapter(new PlacesAutoCompleteAdapter(BuyeProfileCreation.this, android.R.layout.simple_list_item_1));
        Retriveinfo();
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
                if(type=="Curr"){
                    loc=currentLocTv.getText().toString();
                    if(loc.isEmpty())
                    {
                        currentLocTv.setError("Locality is required");
                        return;
                    }
                }
                if(type =="Reg") {
                    loc = locality.getText().toString();
                    if(loc.isEmpty())
                    {
                        locality.setError("Locality is required");
                        return;
                    }
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

                DocumentReference documentReference=fstore.collection("Buyer").document(userid);
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("uid",userid);
                profilemap.put("name",nm);
                profilemap.put("phone",phone);
                profilemap.put("Street",Street);
                profilemap.put("Locality",loc);
                profilemap.put("House",House);
                profilemap.put("token", SplashActivity.token);
                while (userLoc==null);
//                profilemap.put("lat",Double.toString(userLoc.getLatitude()));
//                profilemap.put("lng",Double.toString(userLoc.getLongitude()));
                if(type=="Reg"){
                profilemap.put("lat",String.valueOf(lat));
                profilemap.put("lng",String.valueOf(lng));
                }
                else{
                    profilemap.put("lat",curLat);
                    profilemap.put("lng",curLng);
                }

                documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BuyeProfileCreation.this,"Profile set up Successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuyeProfileCreation.this, MainActivity.class);
                        //if(type=="Reg")
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
                    curLat=String.valueOf(userLoc.getLatitude());
                    curLng = String.valueOf(userLoc.getLongitude());
                    Geocoder geocoder = new Geocoder(BuyeProfileCreation.this);
                    try {
                       List<Address> list =geocoder.getFromLocation(userLoc.getLatitude(),userLoc.getLongitude(),1);
                       Address add=list.get(0);
                       currentLocTv.setText(add.getAddressLine(0).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                if(existence==true)
                {

                    Intent intent = new Intent(BuyeProfileCreation.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                finish();
                signout();}
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

    private void signout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
}

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    private void Retriveinfo() {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We Are Fetching Your Information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        fstore= FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("Buyer").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        existence=true;
                        Create_pofile.setText("Update Profile");
                        mBuyerProfile = document.toObject(Models.mBuyerProfile.class);
                        setExistingData(mBuyerProfile);
                        //setLayoutWidgets(mBuyerPrsetExistingData(mBuyerProfile);
                    } else {
                        progressDialog.dismiss();
                        //Toast.makeText(BuyeProfileCreation.this,"No data history found",Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(BuyeProfileCreation.this,"No data history found task failed",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void setExistingData(Models.mBuyerProfile mBuyerProfile) {
            name.setText(mBuyerProfile.getName());
            phn.setText(mBuyerProfile.getphone());
            street.setText(mBuyerProfile.getStreet());
            //Toast.makeText(BuyeProfileCreation.this,mBuyerProfile.getStreet(),Toast.LENGTH_LONG).show();
            locality.setText(mBuyerProfile.getLocality());
            currentLocTv.setVisibility(View.INVISIBLE);
            locality.setVisibility(View.VISIBLE);
            hiddenTv.setVisibility(View.VISIBLE);
            house.setText(mBuyerProfile.getHouse());
            progressDialog.dismiss();


        }

}



