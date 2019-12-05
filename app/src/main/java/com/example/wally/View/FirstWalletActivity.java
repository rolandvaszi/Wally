package com.example.wally.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wally.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.text.TextUtils.isEmpty;

public class FirstWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_wallet);

        Button btn_add_wallet = findViewById(R.id.btn_add);
        btn_add_wallet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText et_walletname = findViewById(R.id.et_wallet_name);
                EditText et_balance = findViewById(R.id.et_initial_balance);

                String wallet_name = et_walletname.getText().toString();
                String balance = et_balance.getText().toString();

                if(isEmpty(wallet_name) || isEmpty(balance)){
                    Toast.makeText(FirstWalletActivity.this, "Empty field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FirstWalletActivity.this);
                String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.wallet_name), wallet_name);
                editor.apply();

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");
                myRef.child(wallet_name).child("Amount").setValue(balance);

                Intent intent = new Intent(FirstWalletActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
