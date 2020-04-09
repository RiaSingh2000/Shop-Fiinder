package Buyer;

import Adapters.ShopListAdapter;
import Common.VerticalSpacingItemDecoration;
import Models.SellerList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.codepth.maps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerListActivity extends AppCompatActivity {
    RecyclerView listOFShops;
    ArrayList<SellerList> list;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listOFShops=findViewById(R.id.listOfShops);
        listOFShops.setLayoutManager(new LinearLayoutManager(SellerListActivity.this));
        VerticalSpacingItemDecoration itemDecoration=new VerticalSpacingItemDecoration(20);
        listOFShops.addItemDecoration(itemDecoration);
        list=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        db.collection("Seller").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        });

    }
}
