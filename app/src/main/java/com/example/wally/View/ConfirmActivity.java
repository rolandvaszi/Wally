package com.example.wally.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wally.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ConfirmActivity extends AppCompatActivity {

    private EditText confirmCode;
    private TextView userNumber;
    private Button confirmButton;
    private String phoneNumber;

    private String verificationID;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phonenumber");
        mRef = FirebaseDatabase.getInstance().getReference();
        confirmCode = findViewById(R.id.edt_password);
        userNumber = findViewById(R.id.user_number);
        confirmButton = findViewById(R.id.confirm_button);

        userNumber.setText("Your phone number: " + phoneNumber);

        sendVerifivationCode(phoneNumber);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = confirmCode.getText().toString().trim();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
                signInWithCredential(credential); //if code is ok, the we proceed to authentification
            }
        });

    }

    private  void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithCredential(credential); //if code is ok, the we proceed to authentification

    }

    /**
     * If the phone recognises the text message and the user checked the checkbox ,
     * the user will be proceded to the main screen
     * @param credential
     */
    private void signInWithCredential(PhoneAuthCredential credential) { //Sign in

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                            mRef.child(phoneNumber).child("Wallets").child("Wallet1").child("Amount").setValue("0");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/

                           FirebaseDatabase db = FirebaseDatabase.getInstance();
                           final DatabaseReference myRef = db.getReference();

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren())
                                    {
                                        if(ds.getKey().equals(phoneNumber)){
                                            //phone_number exists

                                            if(ds.child("Wallets").hasChildren()) {
                                                //already has wallet -> MainActivity
                                                String wallet_name = "";
                                                for(DataSnapshot dsWallet : ds.child("Wallets").getChildren()){
                                                    wallet_name = dsWallet.getKey(); // wallet name is the key
                                                    break; // we need only one
                                                }
                                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ConfirmActivity.this);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString(getString(R.string.wallet_name), wallet_name);
                                                editor.apply();

                                                Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                            else{
                                                //no wallet -> FisrtWalletActivity
                                                Intent intent = new Intent(ConfirmActivity.this, FirstWalletActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                            return;
                                        }
                                    }
                                    //phone number doesn't exist -> new user, no wallet
                                    Intent intent = new Intent(ConfirmActivity.this, FirstWalletActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        } else {
                            Toast.makeText(ConfirmActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    /**
     * Sends verification code to the given number
     * @param number
     */

    private void sendVerifivationCode(String number){
        //progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    /**
     * If the code was sent, it autofils the field.
     */
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;
        }

        /**
         * Completes the credential verifiaction process.
         * @param phoneAuthCredential
         */
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code!=null){
                confirmCode.setText(code);
                verifyCode(code);
            }
        }


        /**
         * Sends a message if something went wrong.
         * @param e
         */
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ConfirmActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };
}
