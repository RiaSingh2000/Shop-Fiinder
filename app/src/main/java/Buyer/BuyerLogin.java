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

import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static Seller.SellerPhoneAuth.Shared_pref;

//import static com.codepth.maps.SellerPhoneAuth.Shared_pref;

public class BuyerLogin extends AppCompatActivity {
    private final static int RC_SIGN_IN = 2;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    private Button googleSignInBtn;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore fstore;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser user) {
        if(user==null){ //ie user not logged in
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleSignInBtn = (Button) findViewById(R.id.btn_google_sigin);
        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                /*if(firebaseAuth.getCurrentUser()!=null)
                {

                    startActivity(new Intent(BuyerLogin.this, MainActivity.class));
                    Toast.makeText(BuyerLogin.this,"Welcome back",Toast.LENGTH_LONG).show();
                    finish();
                }*/
            }
        };
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
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
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(BuyerLogin.this, "Log in issues", Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            verifyexistence();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(BuyerLogin.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void verifyexistence() {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We are Fetching your Information !!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String currentuserid = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fstore.collection("Buyer").document(currentuserid);
        documentReference.update("token",SplashActivity.token);

        fstore.collection("Buyer").document(currentuserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    progressDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "1");
                    editor.apply();
                    Toast.makeText(BuyerLogin.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BuyerLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    signOut();
                    Toast.makeText(BuyerLogin.this, "No such user exists", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(BuyerLogin.this, Welcomepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(BuyerLogin.this, "On Failure No such user exists", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BuyerLogin.this, Welcomepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                startActivity(intent);
                finish();

            }
        });
    }
           /* public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "1");
                    editor.apply();
                    Toast.makeText(BuyerLogin.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BuyerLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    signOut();
                    Toast.makeText(BuyerLogin.this, "No such user exists", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(BuyerLogin.this, Welcomepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BuyerLogin.this, "On Failure No such user exists", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BuyerLogin.this, Welcomepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                startActivity(intent);
                finish();
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
