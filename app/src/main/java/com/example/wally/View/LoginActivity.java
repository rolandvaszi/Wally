package com.example.wally.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wally.R;

import java.util.Calendar;

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

                Calendar c = Calendar.getInstance();
                String startYear = c.get(Calendar.YEAR) + "";
                String startMonth = (c.get(Calendar.MONTH) + 1) + "";
                String startDay = c.get(Calendar.DAY_OF_MONTH) + "";
                if(startDay.length() == 1){
                    startDay = "0" + startDay;
                }
                if(startMonth.length() == 1){
                    startMonth = "0" + startMonth;
                }
                String date = startYear + "." + startMonth + "." + startDay;

                // *** save active user ***
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.phone_number), phonenumber);
                editor.putString(getString(R.string.date), date);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, ConfirmActivity.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);
            }
        });
    }

    public void onSkip(View v){
        Calendar c = Calendar.getInstance();
        String startYear = c.get(Calendar.YEAR) + "";
        String startMonth = (c.get(Calendar.MONTH) + 1) + "";
        String startDay = c.get(Calendar.DAY_OF_MONTH) + "";
        if(startDay.length() == 1){
            startDay = "0" + startDay;
        }
        if(startMonth.length() == 1){
            startMonth = "0" + startMonth;
        }
        String date = startYear + "." + startMonth + "." + startDay;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.phone_number), "+40746071211");
        editor.putString(getString(R.string.wallet_name), "Wallet1");
        editor.putString(getString(R.string.date), date);
        editor.apply();

        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}