package com.example.wally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String phoneNumber = getIntent().getStringExtra("phonenumber");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_overview: {
                        //Toast.makeText(MainActivity.this, "Overview", Toast.LENGTH_SHORT).show();

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new OverviewFragment());
                        frag_trans.commit();

                        break;
                    }
                    case R.id.action_transactions: {
                        //Toast.makeText(MainActivity.this, "Transactions", Toast.LENGTH_SHORT).show();

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
                        frag_trans.commit();

                        break;
                    }
                    case R.id.action_wallets: {
                        //Toast.makeText(MainActivity.this, "Wallets", Toast.LENGTH_SHORT).show();

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new WalletsFragment());
                        frag_trans.commit();

                        break;
                    }
                }
                return false;
            }
        });

        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
        frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
        frag_trans.commit();
    }

    @Override
    public void onBackPressed() {
        //log_out();
//        finish();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Are you sure you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
}
