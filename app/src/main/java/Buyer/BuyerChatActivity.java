package Buyer;

import Adapters.BuyerListAdapter;
import Adapters.ShopListAdapter;
import Common.DrawerController;
import Common.VerticalSpacingItemDecoration;
import Models.mSellerProfile;
import Seller.SellerChatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.codepth.maps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BuyerChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private String uid;
    FirebaseAuth sAuth;
    RecyclerView listOfSellers;
    ArrayList<String> sellerUid;
    ArrayList<mSellerProfile> list;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_chat);
        DrawerController.setIdentity("BuyerChatActivity");

        progressBar = findViewById(R.id.spinKit);
        navView = findViewById(R.id.nv);
        navView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.activity_main_drawerlayout2);
        toolbar=findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sAuth=FirebaseAuth.getInstance();
        uid=sAuth.getUid();
        listOfSellers=findViewById(R.id.listOfShops);
        listOfSellers.setLayoutManager(new LinearLayoutManager(BuyerChatActivity.this));
        VerticalSpacingItemDecoration itemDecoration=new VerticalSpacingItemDecoration(20);
        listOfSellers.addItemDecoration(itemDecoration);
        list=new ArrayList<>();
        sellerUid=new ArrayList<>();
        auth=FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count",0);
        editor.apply();


        db=FirebaseFirestore.getInstance();
        db.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                sellerUid.clear();
                if(task.isSuccessful()){
                    sellerUid.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        if(snapshot.getData().get("sender").toString().equals(auth.getUid())
                                &&
                                !sellerUid.contains(snapshot.getData().get("receiver").toString())
                        )
                            sellerUid.add(snapshot.getData().get("receiver").toString());
                        //Toast.makeText(SellerChatActivity.this, ""+buyerUid, Toast.LENGTH_SHORT).show();
                        db.collection("Seller").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                list.clear();
                                if(task.isSuccessful()){
                                    list.clear();
                                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                                        if(sellerUid.contains(snapshot.getData().get("uid").toString())){
                                            mSellerProfile mSellerProfile = snapshot.toObject(Models.mSellerProfile.class);
                                            // list.add(new SellerList(snapshot.getData().get("shopname").toString(),snapshot.getData().get("custcare").toString(),snapshot.getData().get("shopname").toString(),snapshot.getData().get("uid").toString()));
                                            list.add(mSellerProfile);
                                            Toast.makeText(BuyerChatActivity.this, ""+list, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    listOfSellers.setAdapter(new ShopListAdapter(BuyerChatActivity.this,list));
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    Toast.makeText(BuyerChatActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        listOfSellers.setAdapter(new ShopListAdapter(BuyerChatActivity.this,list));

                    }
                }
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mapDrawableItem :{
                if(DrawerController.toMainActivity(getApplicationContext())){
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.shopListDrawableItem :{
                if(DrawerController.toShopList(getApplicationContext())){
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.chatListDrawableItem :{
                if(DrawerController.toChatList(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.aboutUsDrawableList :{
                Toast.makeText(this,"TO BE DONE",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.rateUsDrawableList :{
                Toast.makeText(this,"TO BE DONE",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.settingsDrawableItem :{
                if(DrawerController.sendUserToSettingActivity(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
            case R.id.logoutDrawableItem :{
                FirebaseAuth.getInstance().signOut();
                if(DrawerController.sendUsertologinactivity(getApplicationContext())) {
                    this.overridePendingTransition(0,0);
                    finish();
                    this.overridePendingTransition(0,0);
                }
                break;
            }
        }
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}

