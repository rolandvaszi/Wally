package com.example.wally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNum;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNum = findViewById(R.id.edt_phone);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "4";

                String number = phoneNum.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    phoneNum.setError("Number is required");
                    phoneNum.requestFocus();
                    return;
                }

                String phonenumber = "+" + code + number; //Creating number

                // *** save active user ***
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.phone_number), phonenumber);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, ConfirmActivity.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);
            }
        });
    }

    public void onSkip(View v){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.phone_number), "+40746071211");
        editor.apply();

        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}