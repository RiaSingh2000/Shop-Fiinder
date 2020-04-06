package com.codepth.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SellerDisplayActivity extends AppCompatActivity {

    //ACTIVITY WIDGETS
    TextView tvShopName , tvSellerName , tvLocation , tvCall;
    Button  btnChat ;
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
                        mSellerProfile = document.toObject(com.codepth.maps.mSellerProfile.class);
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

    private void editWidgets(com.codepth.maps.mSellerProfile mSellerProfile) {
        tvShopName.setText(mSellerProfile.getShopname());
        tvSellerName.setText(mSellerProfile.getSelname());
        tvLocation.setText(mSellerProfile.getLoc());
        tvCall.setText(mSellerProfile.getCustcare());
        progressDialog.dismiss();
    }

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }


    public void goToChatActivity(View view) {
        Intent intent = new Intent(this,SellerChatActivity.class);
        intent.putExtra("uid",mSellerProfile.getUid());
        startActivity(intent);
    }

}
