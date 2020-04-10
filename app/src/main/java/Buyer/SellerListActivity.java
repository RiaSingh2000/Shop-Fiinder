package Buyer;

import Adapters.ShopListAdapter;
import Common.VerticalSpacingItemDecoration;
import Models.SellerList;
import Models.mSellerProfile;
import Models.mShops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepth.maps.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerListActivity extends AppCompatActivity {
    RecyclerView listOFShops;
   // ArrayList<SellerList> list;
    ArrayList<mSellerProfile> mSellerProfileArrayList;
    FirebaseFirestore db;
    private static LatLng myLatLng = MainActivity.getLatLng();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
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
                            }

                        } else
                            Toast.makeText(SellerListActivity.this, "No shops registered", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
