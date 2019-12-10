package com.example.wally.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.wally.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_overview: {
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        bottomNavigationView.getMenu().getItem(1).setChecked(false);
                        bottomNavigationView.getMenu().getItem(2).setChecked(false);

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new OverviewFragment());
                        frag_trans.commit();

                        break;
                    }
                    case R.id.action_transactions: {
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        bottomNavigationView.getMenu().getItem(2).setChecked(false);

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
                        frag_trans.commit();

                        break;
                    }
                    case R.id.action_wallets: {
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        bottomNavigationView.getMenu().getItem(1).setChecked(false);
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);

                        FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new WalletsFragment());
                        frag_trans.commit();

                        break;
                    }
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_transactions);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        //if keyboard is showing set bottomNavigationBar's visibility to GONE
        final ConstraintLayout constraintLayout=findViewById(R.id.rootView);
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new             ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                constraintLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = constraintLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    //Toast.makeText(MainActivity.this,"Keyboard is showing",Toast.LENGTH_LONG).show();
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    //Toast.makeText(MainActivity.this,"keyboard closed",Toast.LENGTH_LONG).show();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

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
        expense_categories.add("Transport");
        return expense_categories;
    }

    public Map<String, Integer> getImages() {
        Map<String, Integer> images = new HashMap<>();
        images.put("Gift", R.drawable.gift);
        images.put("Medicine", R.drawable.medicine);
        images.put("Shopping", R.drawable.shop);
        images.put("Bills", R.drawable.taxes);
        images.put("Groceries", R.drawable.grocery);
        images.put("Travel", R.drawable.flight);
        images.put("Salary", R.drawable.money);
        images.put("Entertainment", R.drawable.bowling);
        images.put("Transport", R.drawable.car);
        return images;
    }
}
