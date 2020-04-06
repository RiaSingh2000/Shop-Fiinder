package com.codepth.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView textView, textView1;
    private FirebaseAuth mauth;
    private FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth = FirebaseAuth.getInstance();
        currentuser = mauth.getCurrentUser();

        //Window window=getWindow();
        // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //getSupportActionBar().hide();

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);


        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);

        imageView.setAnimation(topAnim);
        textView.setAnimation(bottomAnim);
        textView1.setAnimation(bottomAnim);

        if (currentuser == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, Welcomepage.class);
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(imageView, "logo_image");
                    pairs[1] = new Pair<View, String>(textView, "logo_text");

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }
            }, SPLASH_SCREEN);

        }
        else
        {
            SharedPreferences sharedPreferences=getSharedPreferences("sharedPrefs",MODE_PRIVATE);
            String value = sharedPreferences.getString("role","");
            if(value.equals("0"))
            {
                Intent intent = new Intent(SplashActivity.this, SellerChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else if(value.equals("1"))
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }
    }
}
