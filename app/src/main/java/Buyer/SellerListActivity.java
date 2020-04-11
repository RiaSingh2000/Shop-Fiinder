package Buyer;

import Adapters.ShopListAdapter;
import Common.DrawerController;
import Common.VerticalSpacingItemDecoration;
import Models.mSellerProfile;
import Models.mShops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepth.maps.R;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView listOFShops;
    // ArrayList<SellerList> list;
    ArrayList<mSellerProfile> mSellerProfileArrayList;
    FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ProgressBar progressBar;
    private static LatLng myLatLng = MainActivity.getLatLng();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        DrawerController.setIdentity("SellerListActivity");
        navView = findViewById(R.id.nv);
        navView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.activity_main_drawerlayout);
        progressBar = findViewById(R.id.spinKit);
        Toolbar toolbar = findViewById(R.id.toolbar_list);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        listOFShops = findViewById(R.id.listOfShops);
        listOFShops.setLayoutManager(new LinearLayoutManager(SellerListActivity.this));
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(20);
        listOFShops.addItemDecoration(itemDecoration);
        //list = new ArrayList<>();
        mSellerProfileArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
       /* db.collection("Seller").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                if(task.isSuccessful()){
                    list.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        list.add(new SellerList(snapshot.getData().get("shopname").toString(),snapshot.getData().get("custcare").toString(),snapshot.getData().get("shopname").toString(),snapshot.getData().get("uid").toString()));
                    }
                    listOFShops.setAdapter(new ShopListAdapter(SellerListActivity.this,list));
                }
            }
        });*/

        FoldingCube foldingCube = new FoldingCube();
        //foldingCube.setColor(R.color.appThemeYellow);
        progressBar.setIndeterminateDrawable(foldingCube);
        db.collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mSellerProfileArrayList.clear();
                        if (task.isSuccessful()) {
                            mSellerProfileArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mSellerProfile mSellerProfile = document.toObject(Models.mSellerProfile.class);
                                float[] result = new float[3];
                                Location.distanceBetween((float) myLatLng.latitude, (float) myLatLng.longitude, Float.parseFloat(mSellerProfile.getLat()),
                                        Float.parseFloat(mSellerProfile.getLng()), result);
                                if (result != null && result[0] <= 5000) {
                                    mSellerProfileArrayList.add(mSellerProfile);
                                }
                                listOFShops.setAdapter(new ShopListAdapter(SellerListActivity.this, mSellerProfileArrayList));
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SellerListActivity.this, "No shops registered", Toast.LENGTH_SHORT).show();
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
