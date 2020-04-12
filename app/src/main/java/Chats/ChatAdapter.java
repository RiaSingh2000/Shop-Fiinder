package Chats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepth.maps.R;

import java.util.ArrayList;

import Models.Messages;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

 public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    Context context;
    ArrayList<Messages>messages;
    String fuserId;
    ImageView imageView;


    int MSG_LEFT=1;
    int MSG_RIGHT=2;

    public ChatAdapter(Context context, ArrayList<Messages> messages, String fuserId) {
        this.context = context;
        this.messages = messages;
        this.fuserId = fuserId;

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.msg_left,parent,false);
            return new ChatAdapter.ChatViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.msg_right,parent,false);
            return new ChatAdapter.ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Messages obj=messages.get(position);
        holder.msg.setText(obj.getMsg());
        if(!obj.getImg().equals("")) {
            Glide.with(context).load(obj.getImg()).into(holder.img);
            holder.img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView msg;
        ImageView img;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.msg);
            img=itemView.findViewById(R.id.img);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent=new Intent(v.getContext(),Zoom.class);
                    intent.putExtra("resId", (Parcelable) img);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSender().equals(fuserId))
            return MSG_RIGHT;
        else
            return MSG_LEFT;

    }



}
