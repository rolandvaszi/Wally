package com.example.wally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_overview:
                        Toast.makeText(MainActivity.this, "Overview", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_transactions:
                        Toast.makeText(MainActivity.this, "Transactions", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_wallets:
                        Toast.makeText(MainActivity.this, "Wallets", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
        frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
        frag_trans.commit();
    }
}
