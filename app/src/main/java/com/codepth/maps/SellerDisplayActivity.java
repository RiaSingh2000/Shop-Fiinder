package com.codepth.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    LinearLayout linearLayout = null;

    //FIREBASE
    FirebaseFirestore fstore;

    //GLOBAL VARIABLES
    private  String uid = null;
    private mSellerProfile mSellerProfile=null;
    private static String TAG = "Seller Display Activity =>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_display);
        uid = receiveUid();
        linearLayout=findViewById(R.id.lL_SellerDisplay);
        fetchSellerInfoFromFirebase();
    }

    private void fetchSellerInfoFromFirebase() {
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
                        setLayoutWidgets(mSellerProfile);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setLayoutWidgets(com.codepth.maps.mSellerProfile mSellerProfile) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setText(mSellerProfile.getShopname());
        textView.setId((int)19);
        linearLayout.addView(textView,params);
    }


    private void addTextView(String text , int id) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setText(text);
        textView.setId(id);
        linearLayout.addView(textView,params);
    }

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }


}
