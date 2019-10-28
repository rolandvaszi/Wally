package com.example.wally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText phone;
    private EditText password;
    private Button login_button;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone=findViewById(R.id.phone);
        password=findViewById(R.id.password);
        login_button=findViewById(R.id.login_button);
        register_button=findViewById(R.id.register_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goManageActivity(v);
            }
        });

    }

    public void goManageActivity(View view) {
        Intent manageintent=new Intent(this,ManageActivity.class);
        startActivity(manageintent);
    }
}
