package com.example.wally.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.wally.R;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNum = findViewById(R.id.edt_phone);
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {

            /**
             * Setting o click listener for the button
             * @param v
             */
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
                //editor.putString(getString(R.string.date), date);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, ConfirmActivity.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);
            }
        });
    }
}