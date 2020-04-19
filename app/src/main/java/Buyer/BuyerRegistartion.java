package Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import Buyer.BuyeProfileCreation;
import com.codepth.maps.R;
import com.codepth.maps.Welcomepage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static Seller.SellerPhoneAuth.Shared_pref;

//import static com.codepth.maps.SellerPhoneAuth.Shared_pref;

public class BuyerRegistartion extends AppCompatActivity {
    private final static int RC_SIGN_IN = 2;
    FirebaseAuth mauth;
    GoogleSignInClient mGoogleSignInClient;
    private Button googlereg;
    private Button Fb;
    private Button phn;
    private FirebaseAuth.AuthStateListener mauthlistner;
    private ProgressDialog progressDialog;
   /* @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mauthlistner);

    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_registration);
        progressDialog=new ProgressDialog(this);


        googlereg=findViewById(R.id.btn_google_sigin);
        mauth = FirebaseAuth.getInstance();

     /*   mauthlistner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    //Toast.makeText(RegistrationChoice.this,"User already exists with this account",Toast.LENGTH_LONG).show();
                }
            }
        };*/
        googlereg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //Toast.makeText(BuyerRegistartion.this, "Signed up successfully", Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(BuyerRegistartion.this, "Sign up issues"+e, Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mauth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(BuyerRegistartion.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("This will only take few seconds !!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Toast.makeText(getContext(), "No such user exists", Toast.LENGTH_LONG).show();
        FirebaseFirestore.getInstance().collection("Buyer").document(currentuserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    signOut();
                    progressDialog.dismiss();
                    Toast.makeText(BuyerRegistartion.this, "User  already exists..Login instead", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BuyerRegistartion.this, Welcomepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "1");
                    editor.apply();
                    Intent intent = new Intent(BuyerRegistartion.this, BuyeProfileCreation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(BuyerRegistartion.this, "Welcome", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(BuyerRegistartion.this, "Failed with:", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BuyerRegistartion.this, Welcomepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                startActivity(intent);
                finish();
            }
        });
    }
           /* FirebaseFirestore.getInstance().collection("Buyer").document(currentuserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        signOut();
                        Toast.makeText(BuyerRegistartion.this,"User  already exists..Login instead",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuyerRegistartion.this, Welcomepage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        SharedPreferences sharedPreferences=getSharedPreferences(Shared_pref,MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("role","1");
                        editor.apply();
                        Intent intent=new Intent(BuyerRegistartion.this, BuyeProfileCreation.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(BuyerRegistartion.this, "Welcome", Toast.LENGTH_LONG).show();
                        finish();


                    }
                }
            });
        }
*/
    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

    }


}
