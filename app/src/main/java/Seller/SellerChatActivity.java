package Seller;

import Adapters.BuyerListAdapter;
import Common.VerticalSpacingItemDecoration;
import Models.BuyerList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.codepth.maps.R;
import com.codepth.maps.Welcomepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerChatActivity extends AppCompatActivity {
    private String uid;
    FirebaseAuth sAuth;
    RecyclerView listOfBuyers;
    ArrayList<String> buyerUid;
    ArrayList<BuyerList> list;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_chat);
        sAuth=FirebaseAuth.getInstance();
        uid=sAuth.getUid();
        listOfBuyers=findViewById(R.id.listOfBuyers);
        listOfBuyers.setLayoutManager(new LinearLayoutManager(SellerChatActivity.this));
        VerticalSpacingItemDecoration itemDecoration=new VerticalSpacingItemDecoration(20);
        listOfBuyers.addItemDecoration(itemDecoration);
        list=new ArrayList<>();
        buyerUid=new ArrayList<>();
        auth=FirebaseAuth.getInstance();


        db=FirebaseFirestore.getInstance();
        db.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                buyerUid.clear();
                if(task.isSuccessful()){
                    buyerUid.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        if(snapshot.getData().get("receiver").toString().equals(auth.getUid())
                        &&
                        !buyerUid.contains(snapshot.getData().get("sender").toString())
                        )
                            buyerUid.add(snapshot.getData().get("sender").toString());
                        //Toast.makeText(SellerChatActivity.this, ""+buyerUid, Toast.LENGTH_SHORT).show();
                         }
                }
            }
        });

        db.collection("Buyer").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                if(task.isSuccessful()){
                    list.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                       if(buyerUid.contains(snapshot.getData().get("uid").toString())){
                           list.add(new BuyerList(snapshot.getData().get("name").toString(),snapshot.getData().get("uid").toString()));
                           Toast.makeText(SellerChatActivity.this, ""+list, Toast.LENGTH_SHORT).show();
                    }
                    }
                    listOfBuyers.setAdapter(new BuyerListAdapter(SellerChatActivity.this,list));
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Welcomepage.class));
    }

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }


}