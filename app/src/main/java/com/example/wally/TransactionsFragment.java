package com.example.wally;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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

        Button btn_date = v.findViewById(R.id.dp_bt);
        btn_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // *** open AddTransactionFragment when user clicks Add ***

        Button btn_add_transaction = v.findViewById(R.id.add_bt);
        btn_add_transaction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction frag_trans = getFragmentManager().beginTransaction();
                frag_trans.replace(R.id.fragment_container,new AddTransactionFragment());
                frag_trans.commit();
            }
        });

        // *** set transacton-recyclerview ***

        final RecyclerView recyclerView = v.findViewById(R.id.trans_rv);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child(phone_number);
        final ArrayList<Transaction> transactions = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactions.clear();
                double balance = 0;
                for (DataSnapshot ds : dataSnapshot.child("Expenses").getChildren()) {
                    Transaction transaction = ds.getValue(Transaction.class);
                    transaction.setType("Expenses");
                    transaction.setId(ds.getKey());
                    transactions.add(transaction);
                    balance -= transaction.getAmount();
                }
                for (DataSnapshot ds : dataSnapshot.child("Incomes").getChildren()) {
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

        return v;
    }


    // *** on date set -> set date as the button's text ***

    DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    View v = getView();
                    Button btn_date = v.findViewById(R.id.dp_bt);
                    String date = year + "-" + (month + 1) + "-" + day;
                    btn_date.setText(date);
                }
            };

}
