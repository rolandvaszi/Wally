package com.example.wally.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wally.Model.OverviewItem;
import com.example.wally.Presenter.OverviewAdapter;
import com.example.wally.Presenter.WalletsAdapter;
import com.example.wally.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class OverviewFragment extends Fragment {
    private Context context;


    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);

        final ArrayList<String> income_categories = ((MainActivity) context).getIncomeCategories();
        final ArrayList<String> expense_categories = ((MainActivity) context).getExpenseCategories();

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
        final String wallet_name = sharedPref.getString(getString(R.string.wallet_name),"Wallet Name");

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        final RecyclerView incomes_recyclerView = v.findViewById(R.id.income_rv);
        incomes_recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager incomes_layoutManager = new LinearLayoutManager(context);

        DatabaseReference incomesRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name).child("Incomes");
        final ArrayList<OverviewItem> incomes = new ArrayList<>();

        incomesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incomes.clear();
                for(String category : income_categories){
                    double amount = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String transaction_category = ds.child("category").getValue().toString();
                        if(transaction_category.equals(category)){
                            String transaction_amount = ds.child("amount").getValue().toString();
                            amount += parseDouble(transaction_amount);
                        }
                    }
                    if(amount != 0){
                        incomes.add(new OverviewItem(category, amount, "Income"));
                    }
                }

                RecyclerView.Adapter adapter = new OverviewAdapter(context, incomes);
                incomes_recyclerView.setAdapter(adapter);
                incomes_recyclerView.setLayoutManager(incomes_layoutManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        final RecyclerView expenses_recyclerView = v.findViewById(R.id.expense_rv);
        expenses_recyclerView.setHasFixedSize(true);
       final RecyclerView.LayoutManager expenses_layoutManager = new LinearLayoutManager(context);

        DatabaseReference expenseRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name).child("Expenses");
        final ArrayList<OverviewItem> expenses = new ArrayList<>();

        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenses.clear();
                for(String category : expense_categories){
                    double amount = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.child("category").getValue().toString().equals(category)){
                            amount += parseDouble(ds.child("amount").getValue().toString());
                        }
                    }
                    if(amount != 0){
                        expenses.add(new OverviewItem(category, amount, "Expense"));
                    }
                }

                RecyclerView.Adapter adapter = new OverviewAdapter(context, expenses);
                expenses_recyclerView.setAdapter(adapter);
                expenses_recyclerView.setLayoutManager(expenses_layoutManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}
