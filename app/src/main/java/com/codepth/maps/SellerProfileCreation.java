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
    private EditText name,shopname,cno,loc;
    private Button Create_pofile;
    String userid,nm,shpname,no,locality;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profileas_seller);
        name=findViewById(R.id.etSellerName);
        shopname=findViewById(R.id.etShopName);
        loc=findViewById(R.id.etSellerLocality);
        cno=findViewById(R.id.etSellerPhone);
        Create_pofile=findViewById(R.id.btRegisterSeller);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        Create_pofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nm=name.getText().toString();
                if(nm.isEmpty())
                {
                    name.setError("Name is Required");
                    return ;
                }
                shpname=shopname.getText().toString();
                if(shpname.isEmpty())
                {
                    shopname.setError("Name is Required");
                    return ;
                }

                no=cno.getText().toString();
                if(no.isEmpty())
                {
                    cno.setError("Phone number is required");
                    return;
                }
                locality=loc.getText().toString();
                if(locality.isEmpty())
                {
                    loc.setError("Locality is required");
                    return;
                }
                userid=fauth.getCurrentUser().getUid();
                DocumentReference documentReference=fstore.collection("Seller").document(userid);
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("uid",userid);
                profilemap.put("name",nm);
                profilemap.put("Shopname",shpname);
                profilemap.put("CC",no);
                profilemap.put("Locality",locality);
                profilemap.put("lt","0.0");
                profilemap.put("Ln","0.0");
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




