package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SellerChatActivity extends AppCompatActivity {
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_chat);
        uid = receiveUid();
    }

    /*@Override
    public void onBackPressed() {
        startActivity(new Intent(this, Welcomepage.class));
    }*/

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }
}
