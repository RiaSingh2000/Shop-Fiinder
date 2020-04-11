package Chats;

import Common.VerticalSpacingItemDecoration;
import Models.Messages;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Base64;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codepth.maps.MyFirebaseMessagingService;
import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Seller.SellerPhoneAuth.Shared_pref;

public class ChatActivity extends AppCompatActivity {
    String uid;
    RecyclerView chatsRv;
    ImageView cam, send;
    EditText msg;
    ArrayList<Messages> messages;
    Uri imageUri, fileUri;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Bitmap image;
    private RequestQueue requestQueue;
    String tok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        requestQueue = Volley.newRequestQueue(ChatActivity.this);
        messages = new ArrayList<>();
        uid = getIntent().getStringExtra("uid");
        cam = findViewById(R.id.cam);
        send = findViewById(R.id.send);
        chatsRv = findViewById(R.id.chatsRv);
        msg = findViewById(R.id.msg);
        chatsRv.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(20);
        chatsRv.addItemDecoration(itemDecoration);
        receiveMessage();
        getToken();

        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    100);
        }

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msg.getText().toString().trim() != null || imageUri != null) {
                    sendMessage();
                    msg.setText("");
                }

                receiveMessage();


            }
        });
    }

    public  void  getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "-1");
        if(role.equals("1"))
        firestore.collection("Seller").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.getData().get("uid").toString().equals(uid))
                                    tok = snapshot.getData().get("token").toString();
                                Toast.makeText(ChatActivity.this, "Token:"+tok, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        else
            firestore.collection("Buyer").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    if (snapshot.getData().get("uid").toString().equals(uid))
                                        tok = snapshot.getData().get("token").toString();
                                    Toast.makeText(ChatActivity.this, ""+tok, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                image = (Bitmap) data.getExtras().get("data");
                cam.setImageBitmap(image);
            }
        }
    }



    public void sendMessage() {
        String message = msg.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("msg", msg.getText().toString());
        map.put("sender", auth.getUid());
        map.put("receiver", uid);
        map.put("img", "");
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));


        firestore.collection("Chats").add(map);
        sendNotification(message);
    }

    public void receiveMessage() {
        firestore.collection("Chats")
                .orderBy("timestamp")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                messages.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if ((snapshot.getData().get("sender").toString().equals(auth.getUid()) && snapshot.getData().get("receiver").toString().equals(uid)) ||
                                (snapshot.getData().get("receiver").toString().equals(auth.getUid()) && snapshot.getData().get("sender").toString().equals(uid))) {
                            messages.add(new Messages(
                                    snapshot.getData().get("msg").toString(),
                                    snapshot.getData().get("img").toString(),
                                    snapshot.getData().get("sender").toString(),
                                    snapshot.getData().get("receiver").toString()
                            ));
                        }
                    }
                    chatsRv.setAdapter(new ChatAdapter(ChatActivity.this, messages, auth.getUid()));
                }
            }
        });
        // Toast.makeText(this, "Received", Toast.LENGTH_SHORT).show();
    }


    public void sendNotification(String message) {

        String url="https://fcm.googleapis.com/fcm/send";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/"+ tok);
            JSONObject notify = new JSONObject();
            notify.put("title", "New message");
            notify.put("body", message);
            jsonObject.put("notification",notify);

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ChatActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header=new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAdabEAMs:APA91bG-iB9fcLKgHmUxfgrVVFMyKk5qZQb6aeLNvxgnRbNLByCw4LBngdAhOn-_h4U9t7je-jxdY_L6hNX3j6ol0RA3Uzpvp0SwKSS7bagm4uHR7ET72Hrm1DsNJr5rc8vNzjQ4Nh8j");
                    return  header;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
