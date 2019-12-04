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
import android.widget.Button;
import android.widget.DatePicker;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class TransactionsFragment extends Fragment {
    private Context context;
    FloatingActionButton fab_main, fab_add1, fab_add2, fab_add3;
    Animation FabOpen, FabClose, FabClockWise, FabAntiClock;
    boolean isOpen = false;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_transactions, container, false);

        // *** set datepicker dialog ***

        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, startYear, startMonth, startDay);

        // *** open AddTransactionFragment when user clicks Add ***

        // *** set transacton-recyclerview ***

        final RecyclerView recyclerView = v.findViewById(R.id.trans_rv);

        // must set because ..
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
        final String wallet_name = sharedPref.getString(getString(R.string.wallet_name),"Wallet Name");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");
        final ArrayList<Transaction> transactions = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactions.clear();
                double balance = 0;
                for (DataSnapshot ds : dataSnapshot.child(wallet_name).child("Expenses").getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    transaction.setType("Expenses");
                    transaction.setId(ds.getKey());
                    transactions.add(transaction);
                    balance -= transaction.getAmount();
                }
                for (DataSnapshot ds : dataSnapshot.child(wallet_name).child("Incomes").getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    transaction.setType("Incomes");
                    transaction.setId(ds.getKey());
                    transactions.add(transaction);
                    balance += transaction.getAmount();
                }
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
                RecyclerView.Adapter adapter = new TransactionsAdapter(context, transactions);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab_main = (FloatingActionButton) v.findViewById(R.id.fab_main);
        fab_add1 = (FloatingActionButton) v.findViewById(R.id.fab_add1);
        fab_add2 = (FloatingActionButton) v.findViewById(R.id.fab_add2);
        fab_add3 = (FloatingActionButton) v.findViewById(R.id.fab_add3);
        FabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        FabClockWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        FabAntiClock = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlock);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen) {
                    fab_add3.startAnimation(FabClose);
                    fab_add2.startAnimation(FabClose);
                    fab_add1.startAnimation(FabClose);
                    fab_main.startAnimation(FabAntiClock);
                    fab_add1.setClickable(false);
                    fab_add2.setClickable(false);
                    isOpen = false;
                }
                else {
                    fab_add3.startAnimation(FabOpen);
                    fab_add2.startAnimation(FabOpen);
                    fab_add1.startAnimation(FabOpen);
                    fab_main.startAnimation(FabClockWise);
                    fab_add1.setClickable(true);
                    fab_add2.setClickable(true);
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


    // *** on date set -> set date as the button's text ***

    DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    View v = getView();
                    Button btn_date = v.findViewById(R.id.fab_add3);
                    String date = year + "-" + (month + 1) + "-" + day;
                    btn_date.setText(date);
                }
            };

}
