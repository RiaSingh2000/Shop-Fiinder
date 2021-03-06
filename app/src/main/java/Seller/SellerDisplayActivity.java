package Seller;

import Chats.ChatActivity;
import Models.mSellerProfile;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepth.maps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerDisplayActivity extends AppCompatActivity {

    //ACTIVITY WIDGETS
    TextView tvShopName , tvSellerName , tvLocation;
    ImageView btnChat ,tvCall,nav;
    private ProgressDialog progressDialog;


    //FIREBASE
    FirebaseFirestore fstore;

    //GLOBAL VARIABLES
    private  String uid = null;
    private mSellerProfile mSellerProfile=null;
    private static String TAG = "Seller Display Activity =>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seller_display);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }

        uid = receiveUid();
        initializeWidgets();
        fetchSellerInfoFromFirebase();
    }

    private void initializeWidgets() {
        btnChat = findViewById(R.id.btnChat);
        tvCall = findViewById(R.id.tvCall);
        tvShopName = findViewById(R.id.tvShopname);
        tvSellerName = findViewById(R.id.tvSellerName);
        tvLocation = findViewById(R.id.tvShopLocation);
        progressDialog=new ProgressDialog(this);
        nav=findViewById(R.id.nav);
    }

    private void fetchSellerInfoFromFirebase() {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We Are Fetching Shop Information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        fstore= FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("Seller").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mSellerProfile = document.toObject(Models.mSellerProfile.class);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //setLayoutWidgets(mSellerProfile);
                        editWidgets(mSellerProfile);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void editWidgets(final Models.mSellerProfile mSellerProfile) {
        tvShopName.setText(mSellerProfile.getShopname());
        tvSellerName.setText(mSellerProfile.getSelname());
        tvLocation.setText(mSellerProfile.getLoc());
        //tvCall.setText(mSellerProfile.getCustcare());
        progressDialog.dismiss();

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "google.navigation:q=" + Double.parseDouble(mSellerProfile.getLat()) + "," + Double.parseDouble(mSellerProfile.getLng());
                Uri navigationIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(SellerDisplayActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }


    public void goToChatActivity(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("uid",mSellerProfile.getUid());
        startActivity(intent);
    }

    public void call(View view) {
       startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mSellerProfile.getCustcare())));
    }

    private void showGPSDisabledAlertToUser(){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

}
