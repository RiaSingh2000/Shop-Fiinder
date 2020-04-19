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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
    Location userLoc = MainActivity.returnUserLoc();
    Boolean existence = false;
    private Boolean flag_alert= false;
    double lat, lng;
    private LocationCallback locationCallback;
    private EditText name, phn, street, house;
    private ProgressDialog progressDialog , progressDialog1;
    private mBuyerProfile mBuyerProfile = null;

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        DrawerController.setIdentity("SettingsActivity");
        Log.w(type,"userloc ........................................="+userLoc);
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
        fauth = FirebaseAuth.getInstance();
        userid = fauth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BuyeProfileCreation.this);
        if(userLoc==null){
            fetchLastLoc();
        }
        while (userLoc==null);
        if(userLoc.getLatitude()==0 && userLoc.getLongitude() == 0)
            fetchLastLoc();
        locality.setAdapter(new PlacesAutoCompleteAdapter(BuyeProfileCreation.this, android.R.layout.simple_list_item_1));
        Retriveinfo();
        Create_pofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nm = name.getText().toString();
                if (nm.isEmpty()) {
                    name.setError("Name is Required");
                    return;
                }
                phone = phn.getText().toString();
                if (phone.isEmpty()) {
                    phn.setError("Phone number is required");
                    return;
                }
                Street = street.getText().toString();
                if (Street.isEmpty()) {
                    street.setError("Street number is required");
                    return;
                }

                loc = locality.getText().toString();
                if (loc.isEmpty() && flag_alert == false) {
                    locality.setError("Locality is required");
                    return;
                }
                else{
                    if(flag_alert == true)
                        loc = "Last Current Location";
                }

                House = house.getText().toString();
                if (House.isEmpty()) {
                    house.setError("House detail is required");
                    return;
                }
                try {
                    if (flag_alert == false)
                        geoLocate(view);

                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    useCurrentLocationAlert();
                    Toast.makeText(BuyeProfileCreation.this, "No such location found", Toast.LENGTH_SHORT).show();
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                    useCurrentLocationAlert();
                    Toast.makeText(BuyeProfileCreation.this, "Geocoder list size 0", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void uploadOnCloud() {
        DocumentReference documentReference = fstore.collection("Buyer").document(userid);
        HashMap<String, String> profilemap = new HashMap<>();
        profilemap.put("uid", userid);
        profilemap.put("name", nm);
        profilemap.put("phone", phone);
        profilemap.put("Street", Street);
        profilemap.put("House", House);
        profilemap.put("token", SplashActivity.token);
        while (userLoc == null) ;
        if (flag_alert == true ) {
            Toast.makeText(BuyeProfileCreation.this,"USERLOCCCC"+userLoc.getLatitude() +"\n"+ userLoc.getLongitude(),Toast.LENGTH_LONG).show();
            profilemap.put("lat", String.valueOf(userLoc.getLatitude()));
            profilemap.put("lng", String.valueOf(userLoc.getLongitude()));
            profilemap.put("Locality", "Last Current Location");
        } else {
            Toast.makeText(BuyeProfileCreation.this,"AUTOCOMPLETE"+String.valueOf(lat) +"\n"+ String.valueOf(lng),Toast.LENGTH_LONG).show();

            profilemap.put("lat", String.valueOf(lat));
            profilemap.put("lng", String.valueOf(lng));
            profilemap.put("Locality", loc);
        }

        documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BuyeProfileCreation.this, "Profile set up Successfully", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Intent intent = new Intent(BuyeProfileCreation.this, MainActivity.class);
                intent.putExtra("lat", userLoc.getLatitude());
                intent.putExtra("lng", userLoc.getLongitude());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

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



    public void fetchLastLoc() {
        if(progressDialog.isShowing()==false){
            progressDialog.show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BuyeProfileCreation.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLoc = location;
                    Toast.makeText(getApplicationContext(), "AFTER FETCH LAST LOCATION"+userLoc.getLatitude() + "\n" + userLoc.getLongitude(), Toast.LENGTH_LONG).show();
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                } else {
                    //USING UPDATE LOCATION
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BuyeProfileCreation.this);
                    LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    locationCallback = new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                Toast.makeText(BuyeProfileCreation.this, "We are unable to get your current location kindly open Google maps and " +
                                        "revisit the app then", Toast.LENGTH_LONG).show();

                            } else {
                                userLoc = new Location(locationResult.getLocations().get(0));
                                Toast.makeText(getApplicationContext(), "AFTER UPDATE LOCATION"+userLoc.getLatitude() + "\n" + userLoc.getLongitude(), Toast.LENGTH_LONG).show();


                                // Log.w(TAG1,"onLocationResult=="+locationResult.getLocations().get(0).getLatitude()+locationResult.getLocations().get(0).getLongitude());
                                // Toast.makeText(MainActivity.this,"onLocationResult=="+locationResult.getLocations().get(0).toString(),Toast.LENGTH_LONG).show();
                            }
                            //stopLocationUpdates();
                            if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        }
                    };


                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

                    } else {
                        showGPSDisabledAlertToUser();
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    }
                    Toast.makeText(getApplicationContext(), "Location field invalid", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showGPSDisabledAlertToUser() {
        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void geoLocate(View v) throws IOException, IllegalAccessException { //SETS LAT LNG FROM VALUE IN AUTOCOMPLETETEXTVIEW ONSELECTED
        hideSoftKeyboard(v);
        String location=locality.getText().toString();
        Geocoder gc=new Geocoder(BuyeProfileCreation.this);
        List<Address> list=gc.getFromLocationName(location,1);
        if(list.isEmpty())
            throw new IllegalAccessException("List size 0");
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


    private void setExistingData(Models.mBuyerProfile mBuyerProfile) { //SETS RETRIEVED DATA IN THE UI
            name.setText(mBuyerProfile.getName());
            phn.setText(mBuyerProfile.getphone());
            street.setText(mBuyerProfile.getStreet());
            //Toast.makeText(BuyeProfileCreation.this,mBuyerProfile.getStreet(),Toast.LENGTH_LONG).show();
            locality.setText(mBuyerProfile.getLocality());
            //currentLocTv.setVisibility(View.INVISIBLE);
            locality.setVisibility(View.VISIBLE);
            //hiddenTv.setVisibility(View.VISIBLE);
            house.setText(mBuyerProfile.getHouse());
            progressDialog.dismiss();


        }

    private void useCurrentLocationAlert(){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(BuyeProfileCreation.this);
        alertDialogBuilder.setMessage("We cannot identify your locality . Use your current location as locality ?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                flag_alert = true;
                                calculatingCurrentLocation();
                                progressDialog.setTitle("Please Wait");
                                progressDialog.setMessage("We Are Registering you...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                uploadOnCloud();

                            }
                        });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        flag_alert = false;
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    
    private void calculatingCurrentLocation(){
                if(userLoc.getLongitude()!=0 || userLoc.getLatitude()!=0){ //VALUE RECEIVED FROM MAIN_ACT IS VALID
                    Geocoder gc=new Geocoder(BuyeProfileCreation.this);
                    List<Address> list= null;
                    try {
                        list = gc.getFromLocation(userLoc.getLatitude(),userLoc.getLongitude(),1);
                        //Address address=list.get(0);
                        //locality.setText(address.getLocality());
                        locality.setText(" ");
                        locality.setHint("Successfully Received your location");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            locality.setHintTextColor(getColor(R.color.green_600));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        locality.setText(" ");
                        locality.setHint("Successfully Received your location");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            locality.setHintTextColor(getColor(R.color.green_600));
                        }
                    }

                }
                else{
                    fetchLastLoc(); //puts correct value in userLoc
                    Geocoder gc=new Geocoder(BuyeProfileCreation.this);
                    List<Address> list= null;
                    try {
                        list = gc.getFromLocation(userLoc.getLatitude(),userLoc.getLongitude(),1);
                       // Address address=list.get(0);
                        //locality.setText(address.getLocality());
                        locality.setText("");
                        //progressDialog.dismiss();
                        locality.setHint("Successfully Received your location");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            locality.setHintTextColor(getColor(R.color.green_600));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        locality.setText("");
                        //progressDialog.dismiss();
                        locality.setHint("Successfully Received your location");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            locality.setHintTextColor(getColor(R.color.green_600));
                        }
                    }

                }

                progressDialog.dismiss();
    }

}



