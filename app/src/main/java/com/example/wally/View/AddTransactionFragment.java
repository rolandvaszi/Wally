package com.example.wally.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wally.Model.Transaction;
import com.example.wally.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Double.parseDouble;

public class AddTransactionFragment extends Fragment {
    private Context context;
    Spinner mainSpinner, categorySpinner;


    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        final ArrayList<String> income_categories = ((MainActivity) context).getIncomeCategories();
        final ArrayList<String> expense_categories = ((MainActivity) context).getExpenseCategories();
        // *** set datepicker dialog ***

        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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

                String date = startYear + "." + startMonth + "." + startDay;

                Button btn_date = v.findViewById(R.id.date_bt);
                btn_date.setText(date);

            }
        }, startYear, startMonth, startDay);

        Button btn_date = v.findViewById(R.id.date_bt);
        btn_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mainSpinner = (Spinner)v.findViewById(R.id.income_sp);
        categorySpinner = (Spinner) v.findViewById(R.id.categ_sp);
        final String mainCategories[] = {"Expense", "Income"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, mainCategories);
        mainSpinner.setAdapter(adapter);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelect = mainCategories[position];
                if(position == 1) {
                    ArrayAdapter<String> adapter1 =
                            new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, income_categories);
                    categorySpinner.setAdapter(adapter1);
                }
                if(position == 0) {
                    ArrayAdapter<String> adapter2 =
                            new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, expense_categories);
                    categorySpinner.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // *** go back to TransactionsFragment when user clicks Add ***

        Button btn_add_transaction = v.findViewById(R.id.add_bt);
        btn_add_transaction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText et_amount = v.findViewById(R.id.balance_et);
                EditText et_comment = v.findViewById(R.id.comment_et);
                Button btn_date = v.findViewById(R.id.date_bt);

                String amount = et_amount.getText().toString();
                String date = btn_date.getText().toString();

                if(amount.isEmpty()){
                    Toast.makeText(context, "Please enter the amount!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(date.equals("Select Date")){
                    Toast.makeText(context, "Please select a date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String transaction_type;
                if(mainSpinner.getSelectedItem().toString().equals("Income")){
                    transaction_type = "Incomes";
                }
                else{
                    transaction_type = "Expenses";
                }
                String category = categorySpinner.getSelectedItem().toString();
                String comment = et_comment.getText().toString();
                final double amountDouble = parseDouble(amount);

                Transaction new_transaction = new Transaction(date, category, amountDouble, comment);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
                final String wallet_name = sharedPref.getString(getString(R.string.wallet_name),"Wallet Name");

                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child(phone_number).child("Wallets").child(wallet_name).child(transaction_type).push().setValue(new_transaction);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String balanceStr = dataSnapshot.child(phone_number).child("Wallets").child(wallet_name).child("Amount").getValue().toString();
                        double balance = parseDouble(balanceStr);
                        if(transaction_type.equals("Expenses")){
                            balance = balance - amountDouble;
                        }
                        else {
                            balance = balance + amountDouble;
                        }
                        dbRef.child(phone_number).child("Wallets").child(wallet_name).child("Amount").setValue(balance);

                        FragmentTransaction frag_trans = getFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
                        frag_trans.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
