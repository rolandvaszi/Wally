package com.example.wally;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class AddTransactionFragment extends Fragment {
    private Context context;


    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_transaction, container, false);

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
}
