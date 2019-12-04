package com.example.wally.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wally.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        View v = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        final ArrayList<String> income_categories = ((MainActivity) context).getIncomeCategories();
        final ArrayList<String> expense_categories = ((MainActivity) context).getExpenseCategories();
        // *** set datepicker dialog ***

        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, startYear, startMonth, startDay);

        Button btn_date = v.findViewById(R.id.date_bt);
        btn_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mainSpinner = (Spinner)v.findViewById(R.id.income_sp);
        categorySpinner = (Spinner) v.findViewById(R.id.categ_sp);
        final String mainCategories[] = {"Income", "Expense"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, mainCategories);
        mainSpinner.setAdapter(adapter);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelect = mainCategories[position];
                if(position == 0) {
                    ArrayAdapter<String> adapter1 =
                            new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, income_categories);
                    categorySpinner.setAdapter(adapter1);
                }
                if(position == 1) {
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
            public void onClick(View v) {
                FragmentTransaction frag_trans = getFragmentManager().beginTransaction();
                frag_trans.replace(R.id.fragment_container,new AddTransactionFragment());
                frag_trans.commit();
            }
        });

        return v;
    }

    // *** on date set -> set date as the button's text ***

    DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    View v = getView();
                    Button btn_date = v.findViewById(R.id.date_bt);
                    String date = year + "-" + (month + 1) + "-" + day;
                    btn_date.setText(date);
                }
            };

    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
