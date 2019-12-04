package com.example.wally.View;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;

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
import static java.lang.Integer.parseInt;

public class OverviewFragment extends Fragment {
    private Context context;
    private String phone_number, wallet_name, date;
    private FirebaseDatabase db;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_overview, container, false);

        db = FirebaseDatabase.getInstance();

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
        wallet_name = sharedPref.getString(getString(R.string.wallet_name),"Wallet Name");
        date = sharedPref.getString(getString(R.string.date),"Date");

        final String[] MONTH = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final TextView tv_date = v.findViewById(R.id.tv_date);
        String month = date.substring(0,5) + " " + MONTH[parseInt(date.substring(5,7)) - 1];
        tv_date.setText(month);

        setIncomeRecyclerView(v);
        setExpenseRecyclerView(v);

        TextView tv_change_date = v.findViewById(R.id.tv_change_date);
        tv_change_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String startYear = year + "";
                        String startMonth = (monthOfYear + 1) + "";
                        String startDay = dayOfMonth + "";
                        if(startDay.length() == 1){
                            startDay = "0" + startDay;
                        }
                        if(startMonth.length() == 1){
                            startMonth = "0" + startMonth;
                        }

                        date = startYear + "." + startMonth + "." + startDay;

                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.date), date);
                        editor.apply();

                        setIncomeRecyclerView(v);
                        setExpenseRecyclerView(v);

                        String month = date.substring(0,5) + " " + MONTH[parseInt(date.substring(5,7)) - 1];
                        tv_date.setText(month);
                    }
                }, getYear(), getMonth() - 1, getDay());

                datePickerDialog.show();
            }
        });

        return v;
    }

    private int getYear(){
        return parseInt(date.substring(0,4));
    }

    private int getMonth(){
        return parseInt(date.substring(5,7));
    }

    private int getDay(){
        return parseInt(date.substring(8,10));
    }

    private void setIncomeRecyclerView(View v){
        final ArrayList<String> income_categories = ((MainActivity) context).getIncomeCategories();

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
                    String month = date.substring(0,7);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String transaction_category = ds.child("category").getValue().toString();
                        String transaction_month = ds.child("date").getValue().toString().substring(0,7);
                        if(transaction_category.equals(category) && transaction_month.compareTo(month) == 0){
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
    }

    private void setExpenseRecyclerView(View v){
        final ArrayList<String> expense_categories = ((MainActivity) context).getExpenseCategories();

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
                    String month = date.substring(0,7);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String transaction_category = ds.child("category").getValue().toString();
                        String transaction_month = ds.child("date").getValue().toString().substring(0,7);
                        if(transaction_category.equals(category) && transaction_month.compareTo(month) == 0){
                            String transaction_amount = ds.child("amount").getValue().toString();
                            amount += parseDouble(transaction_amount);
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
    }
}
