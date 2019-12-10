package com.example.wally.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import com.example.wally.Presenter.TransactionsAdapter;
import com.example.wally.R;
import com.example.wally.Model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class TransactionsFragment extends Fragment {
    private Context context;
    private FirebaseDatabase db;
    private String phone_number, wallet_name, date;
    private FloatingActionButton fab_main, fab_add1, fab_add3;
    private Animation FabOpen, FabClose, FabClockWise, FabAntiClock;
    private boolean isOpen = false;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_transactions, container, false);
        //Getting database reference
        db = FirebaseDatabase.getInstance();
        //Getting phone number and wallet
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);;
        phone_number = sharedPref.getString(context.getString(R.string.phone_number),"Phone Number");
        wallet_name = sharedPref.getString(context.getString(R.string.wallet_name),"Wallet Name");
        date = sharedPref.getString(context.getString(R.string.date),"Date");

        //Display wallet_name and balance for selected wallet
        TextView tv_selected_wallet = ((MainActivity) context).findViewById(R.id.tv_selected_wallet);
        tv_selected_wallet.setText(wallet_name);
        DatabaseReference amountRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name).child("Amount");
        amountRef.addValueEventListener(new ValueEventListener() {
            /**
             * Getting balance
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView tv_balance = ((MainActivity) context).findViewById(R.id.tv_balance);
                String balance = dataSnapshot.getValue().toString() + " RON";
                tv_balance.setText(balance);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Set date picker dialog
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String startYear = year + "";
                String startMonth = (monthOfYear + 1) + "";
                String startDay = dayOfMonth + "";
                if(startDay.length() == 1) {
                    startDay = "0" + startDay;
                }
                if(startMonth.length() == 1) {
                    startMonth = "0" + startMonth;
                }
                date = startYear + "." + startMonth + "." + startDay;
                //Saving date to shared preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.date), date);
                editor.apply();
                setTransactionsRecyclerView(v);
            }
        }, getYear(), getMonth() - 1, getDay());
        setTransactionsRecyclerView(v);
        //Setting the floating action buttons and the animations
        fab_main = (FloatingActionButton) v.findViewById(R.id.fab_main);
        fab_add1 = (FloatingActionButton) v.findViewById(R.id.fab_add1);
        fab_add3 = (FloatingActionButton) v.findViewById(R.id.fab_add3);
        FabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        FabClockWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        FabAntiClock = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlock);
        //Setting click listener for the main Fab to open/close
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen) {
                    fab_add3.startAnimation(FabClose);
                    fab_add1.startAnimation(FabClose);
                    fab_main.startAnimation(FabAntiClock);
                    fab_add1.setClickable(false);
                    fab_add3.setClickable(false);
                    isOpen = false;
                }
                else {
                    fab_add3.startAnimation(FabOpen);
                    fab_add1.startAnimation(FabOpen);
                    fab_main.startAnimation(FabClockWise);
                    fab_add1.setClickable(true);
                    fab_add3.setClickable(true);
                    isOpen = true;
                    fab_add1.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            FragmentTransaction frag_trans = getFragmentManager().beginTransaction();
                            frag_trans.replace(R.id.fragment_container,new AddTransactionFragment());
                            frag_trans.commit();
                        }
                    });
                    fab_add3.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            datePickerDialog.show();
                        }
                    });
                }
            }
        });
        return v;
    }

    /**
     * Populate recyclerView with transactions
     * @param v
     */
    private void setTransactionsRecyclerView(final View v){
        final RecyclerView recyclerView = v.findViewById(R.id.trans_rv);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        //Getting database reference
        DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");
        final ArrayList<Transaction> transactions = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            /**
             * Adding transactions to the recyclerView
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactions.clear();
                double balance = 0;
                //Going through expenses
                for (DataSnapshot ds : dataSnapshot.child(wallet_name).child("Expenses").getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    if(transaction.getDate().compareTo(date) <= 0){
                        transaction.setType("Expenses");
                        transaction.setId(ds.getKey());
                        transactions.add(transaction);
                        balance -= transaction.getAmount();
                    }
                }
                //Going through incomes
                for (DataSnapshot ds : dataSnapshot.child(wallet_name).child("Incomes").getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    if(transaction.getDate().compareTo(date) <= 0) {
                        transaction.setType("Incomes");
                        transaction.setId(ds.getKey());
                        transactions.add(transaction);
                        balance += transaction.getAmount();
                    }
                }
                TextView tv_no_transactions = v.findViewById(R.id.tv_no_transactions);
                if(transactions.size() == 0){
                    //if there is no transaction to show, set tv_no_transactions visible
                    tv_no_transactions.setVisibility(View.VISIBLE);
                }
                else{
                    //if there are transactions, remove tv_no_transactions
                    tv_no_transactions.setVisibility(View.GONE);
                }
                //Sorting data by the got date
                Collections.sort(transactions, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                        Date date1 = null;
                        try {
                            date1 = format.parse(o1.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = null;
                        try {
                            date2 = format.parse(o2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return date2.compareTo(date1);
                    }
                });
                //Setting adapter
                RecyclerView.Adapter adapter = new TransactionsAdapter(context, transactions);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
