package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SellerDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_display);

        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        Toast.makeText(this,selleruid,Toast.LENGTH_LONG).show();
    }
}
