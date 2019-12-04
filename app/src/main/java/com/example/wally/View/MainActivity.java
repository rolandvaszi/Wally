package com.example.wally.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.wally.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

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

    public ArrayList<String> getIncomeCategories(){
        ArrayList<String> income_categories = new ArrayList<>();
        income_categories.add("Gift");
        income_categories.add("Salary");
        return income_categories;
    }

    public ArrayList<String> getExpenseCategories(){
        ArrayList<String> expense_categories = new ArrayList<>();
        expense_categories.add("Gift");
        expense_categories.add("Medicine");
        expense_categories.add("Shopping");
        expense_categories.add("Bills");
        expense_categories.add("Groceries");
        expense_categories.add("Travel");
        expense_categories.add("Entertainment");
        return expense_categories;
    }
}