package Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepth.maps.R;

import java.util.ArrayList;

import Chats.ChatActivity;
import Models.BuyerList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BuyerListAdapter extends RecyclerView.Adapter<BuyerListAdapter.BuyerListViewHolder>  {
    Context context;
    ArrayList<BuyerList> list;

    public BuyerListAdapter(Context context, ArrayList<BuyerList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BuyerListAdapter.BuyerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.buyer_list_layout,parent,false);
        return new BuyerListAdapter.BuyerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerListAdapter.BuyerListViewHolder holder, int position) {
        final BuyerList obj=list.get(position);
        holder.buyerName.setText(obj.getName());


        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,ChatActivity.class);
                i.putExtra("uid",obj.getBuid());
                context.startActivity(i);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + obj.getPhone())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BuyerListViewHolder extends RecyclerView.ViewHolder{
        TextView buyerName;
        ImageView call;
        ImageView chat;

        public BuyerListViewHolder(@NonNull View itemView) {
            super(itemView);
           buyerName=itemView.findViewById(R.id.name);
           call=itemView.findViewById(R.id.call);
           chat=itemView.findViewById(R.id.chat);
        }
    }
}
