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

public class BuyeProfileCreation extends AppCompatActivity {
    private EditText name,phn,street,locality,house;
    private Button Create_pofile;
    String userid,nm,pn,Street,loc,phone,House;
    private FirebaseFirestore fstore;
    private FirebaseAuth fauth;

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

                userid=fauth.getCurrentUser().getUid();
                DocumentReference documentReference=fstore.collection("Buyer").document(userid);
                HashMap<String,String> profilemap=new HashMap<>();
                profilemap.put("uid",userid);
                profilemap.put("name",nm);
                profilemap.put("Phone number",phone);
                profilemap.put("Street",Street);
                profilemap.put("Locality",loc);
                profilemap.put("House",House);
                documentReference.set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BuyeProfileCreation.this,"Profile set up Successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuyeProfileCreation.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                });


            }
        });
    }
}



