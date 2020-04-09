package Buyer;

import Adapters.BuyerListAdapter;
import Adapters.ShopListAdapter;
import Common.VerticalSpacingItemDecoration;
import Models.BuyerList;
import Models.SellerList;
import Seller.SellerChatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.codepth.maps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BuyerChatActivity extends AppCompatActivity {
    private String uid;
    FirebaseAuth sAuth;
    RecyclerView listOfSellers;
    ArrayList<String> sellerUid;
    ArrayList<SellerList> list;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_chat);
        sAuth=FirebaseAuth.getInstance();
        uid=sAuth.getUid();
        listOfSellers=findViewById(R.id.listOfShops);
        listOfSellers.setLayoutManager(new LinearLayoutManager(BuyerChatActivity.this));
        VerticalSpacingItemDecoration itemDecoration=new VerticalSpacingItemDecoration(20);
        listOfSellers.addItemDecoration(itemDecoration);
        list=new ArrayList<>();
        sellerUid=new ArrayList<>();
        auth=FirebaseAuth.getInstance();


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
                    }
                }
            }
        });

        db.collection("Seller").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                if(task.isSuccessful()){
                    list.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        if(sellerUid.contains(snapshot.getData().get("uid").toString())){
                            list.add(new SellerList(snapshot.getData().get("shopname").toString(),snapshot.getData().get("custcare").toString(),snapshot.getData().get("shopname").toString(),snapshot.getData().get("uid").toString()));
                            Toast.makeText(BuyerChatActivity.this, ""+list, Toast.LENGTH_SHORT).show();
                        }
                    }
                    listOfSellers.setAdapter(new ShopListAdapter(BuyerChatActivity.this,list));
                }
            }
        });

    }
}
