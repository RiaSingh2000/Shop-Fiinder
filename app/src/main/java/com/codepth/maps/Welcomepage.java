package com.codepth.maps;

import Buyer.MainActivity;
import androidx.appcompat.app.AppCompatActivity;
import  Seller.SellerChatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcomepage extends AppCompatActivity {

    private Button Login;
    private Button Signup;
    private FirebaseAuth mauth;
    private FirebaseUser currentuser;
   // private TextView CustomerRegisterLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcomepage);
        Login=findViewById(R.id.loginbtn);
        Signup=findViewById(R.id.signupbtn);
      //  CustomerRegisterLink=findViewById(R.id.register_customer_link);
        mauth = FirebaseAuth.getInstance();
        currentuser = mauth.getCurrentUser();

        //Signup.setVisibility(View.INVISIBLE);
       // Signup.setEnabled(false);



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Welcomepage.this,ChoiceofLoginRole.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_right);
                finish();

            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Welcomepage.this, ChoiceofRegRole.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_right);
                finish();
            }
        });







    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String value = sharedPreferences.getString("role", "");
            if (value.equals("0")) {
                Toast.makeText(Welcomepage.this, "Welcome Back!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Welcomepage.this, SellerChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (value.equals("1")) {
                Toast.makeText(Welcomepage.this, "Welcome Back!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Welcomepage.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(Welcomepage.this,"LOGIN FIRST",Toast.LENGTH_LONG).show();
            }

        }
    }



}
