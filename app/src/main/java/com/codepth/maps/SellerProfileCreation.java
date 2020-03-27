package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SellerProfileCreation extends AppCompatActivity {
    private EditText etSellerName,etShopName,etSellerPhone,etSellerLocality;
    private Button btnRegisterSeller;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;

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
                mSellerProfile.setSellerLat("20.789");
                mSellerProfile.setSellerLong("30.6789");
                profilemap.put("lt",mSellerProfile.getSellerLat());
                profilemap.put("ln",mSellerProfile.getSellerLong());
                //TODO: find latitude and longitude(string) for seller's locality and store it on firestore as in line 71 and 72
                //TODO: description of shop when added should be in a document inside a new collection pointed by each seller's document
                //TODO: The custcare and seller name can also be moved to this new document
                documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SellerProfileCreation.this,"Profile set up Successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SellerProfileCreation.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                });


            }
        });
    }
}




