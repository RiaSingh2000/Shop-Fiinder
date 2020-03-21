package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateProfileActivity extends AppCompatActivity {
    private EditText name,phn,street,locality,house;
    private Button Create_pofile;

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
    }
}
