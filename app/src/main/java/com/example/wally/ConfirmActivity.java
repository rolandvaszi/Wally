package com.example.wally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ConfirmActivity extends AppCompatActivity {

    private EditText confirmCode;
    private TextView userNumber;
    private Button confirmButton;


    private String verificationID;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mAuth = FirebaseAuth.getInstance();

        String phoneNumber = getIntent().getStringExtra("phonenumber");

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
     * If the phone recognises the text message and the user checked the checkbox , the user will be proceded to the main screen
     * @param credential
     */
    private void signInWithCredential(PhoneAuthCredential credential) { //Sign in

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ConfirmActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
