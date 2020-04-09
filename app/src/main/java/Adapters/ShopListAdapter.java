package Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepth.maps.R;

import java.util.ArrayList;

import Chats.ChatActivity;
import Models.SellerList;
import Seller.SellerDisplayActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder> {
    Context context;
    ArrayList<SellerList> list;

    public ShopListAdapter(Context context, ArrayList<SellerList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ShopListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.seller_list_layout,parent,false);
        return new ShopListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListViewHolder holder, int position) {
        final SellerList obj=list.get(position);
        holder.shopName.setText(obj.getName());
        holder.desc.setText(obj.getDesc());
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + obj.getPh())));
            }
        });
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, ChatActivity.class);
                i.putExtra("uid", obj.getUid());
                context.startActivity(i);
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.startActivity(new Intent(context, SellerDisplayActivity.class));
                Toast.makeText(context, "Open SellerDisplayActivity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ShopListViewHolder extends RecyclerView.ViewHolder{
        TextView shopName,desc;
        ImageView chat,call;
        LinearLayout linearLayout;
        public ShopListViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName=itemView.findViewById(R.id.shopName);
            desc=itemView.findViewById(R.id.desc);
            chat=itemView.findViewById(R.id.chat);
            call=itemView.findViewById(R.id.call);
            linearLayout=itemView.findViewById(R.id.linear);
        }
    }
}
