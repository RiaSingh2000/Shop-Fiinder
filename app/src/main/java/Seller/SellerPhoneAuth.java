package Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepth.maps.R;
import Seller.SellerProfileCreation;
import com.codepth.maps.Welcomepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;
// This activity is responsible for phone number verification for during seller auth
public class SellerPhoneAuth extends AppCompatActivity {
    private Button getotp,signup;
    private EditText num,etotp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    public static  final String Shared_pref="sharedPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_phone_auth);
        getotp=findViewById(R.id.btn_get_otp);
        signup=findViewById(R.id.btn_singup);
        num=findViewById(R.id.et_phone_num);
        etotp=findViewById(R.id.et_otp);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phnno=num.getText().toString();
                StringBuilder s=new StringBuilder("+91");
                s.append(phnno);
                Toast.makeText(SellerPhoneAuth.this,String.valueOf(s),Toast.LENGTH_LONG).show();
                loadingbar.setTitle("Phone Verification");
                loadingbar.setMessage("Please wait,while we authenticate your phone");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        String.valueOf(s),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        SellerPhoneAuth.this,               // Activity (for callback binding)
                        callbacks);        // OnVerificationStateChangedCallbacks


            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingbar.dismiss();

                Toast.makeText(SellerPhoneAuth.this, "Invalid please enter correct phone number with your country code", Toast.LENGTH_LONG).show();

            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();
                Toast.makeText(SellerPhoneAuth.this, "Code sent", Toast.LENGTH_LONG).show();
                num.setVisibility(View.INVISIBLE);


            }

        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etotp.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(SellerPhoneAuth.this, "Please enter the code", Toast.LENGTH_LONG).show();
                } else {

                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("Please wait,while we are verifying");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            sendtoprofilecreation();


                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(SellerPhoneAuth.this, msg, Toast.LENGTH_LONG).show();


                        }

                    }
                });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void  sendtoprofilecreation() {
        String currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Toast.makeText(getContext(), "No such user exists", Toast.LENGTH_LONG).show();
        FirebaseFirestore.getInstance().collection("Seller").document(currentuserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Toast.makeText(SellerPhoneAuth.this, "User  already exists..Login instead", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SellerPhoneAuth.this, Welcomepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "0");
                    editor.apply();
                    Intent intent = new Intent(SellerPhoneAuth.this, SellerProfileCreation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SellerPhoneAuth.this, "Welcome", Toast.LENGTH_LONG).show();
                    finish();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerPhoneAuth.this, "Failed with Exception:" + e, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SellerPhoneAuth.this, Welcomepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

    }
}
