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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;
import com.example.wally.Model.DropDownItem;
import com.example.wally.Model.Transaction;
import com.example.wally.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class AddTransactionFragment extends Fragment {
    private Context context;
    private Spinner categorySpinner;
    private Switch mySwitch;

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
        final Map<String, Integer> imagesMap = ((MainActivity) context).getImages();
        final Map.Entry<String,Integer> images = imagesMap.entrySet().iterator().next();
        final ArrayList<DropDownItem> income_cat = new ArrayList<>();
        final ArrayList<DropDownItem> expense_cat = new ArrayList<>();

        for(String cat_name: income_categories){
            income_cat.add(new DropDownItem(cat_name,imagesMap.get(cat_name)));
        }

        for(String cat_name: expense_categories){
            expense_cat.add(new DropDownItem(cat_name,imagesMap.get(cat_name)));
        }

        //Set date picker dialog
        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);
        //Creating date picker
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            /**
             * Getting date from user interaction
             * @param view
             * @param year
             * @param monthOfYear
             * @param dayOfMonth
             */
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

            /**
             * Showing date picker dialog
             * @param v
             */
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mySwitch = (Switch)v.findViewById(R.id.switch1);
        categorySpinner = (Spinner) v.findViewById(R.id.categ_sp);
        //Initializing adapters for spinner
        final String[] mainCategories = {"Expense", "Income"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, mainCategories);
        SpinnerAdapter adapter1 = new com.example.wally.Presenter.SpinnerAdapter(getContext(),R.layout.spinner_layout,R.id.txt,expense_cat);
        categorySpinner.setAdapter(adapter1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            /**
             * Checking switch status, then adding the right adapter for the spinner
             * @param compoundButton
             * @param isChecked
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    SpinnerAdapter adapter1 = new com.example.wally.Presenter.SpinnerAdapter(getContext(),R.layout.spinner_layout,R.id.txt,income_cat);
                    categorySpinner.setAdapter(adapter1);
                }else {
                    SpinnerAdapter adapter1 = new com.example.wally.Presenter.SpinnerAdapter(getContext(),R.layout.spinner_layout,R.id.txt,expense_cat);
                    categorySpinner.setAdapter(adapter1);
                }
            }
        });

        Button btn_add_transaction = v.findViewById(R.id.add_bt);
        btn_add_transaction.setOnClickListener(new View.OnClickListener(){

            /**
             * Go back to TransactionsFragment when user clicks "Add"
             * @param view
             */
            @Override
            public void onClick(View view) {
                EditText et_amount = v.findViewById(R.id.balance_et);
                EditText et_comment = v.findViewById(R.id.comment_et);
                Button btn_date = v.findViewById(R.id.date_bt);

                String amount = et_amount.getText().toString();
                String date = btn_date.getText().toString();
                //Requesting input from the user
                if(amount.isEmpty()){
                    Toast.makeText(context, "Please enter the amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(date.equals("Select Date")){
                    Toast.makeText(context, "Please select a date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Checking for the right input value,
                try {
                    double d = Double.parseDouble(amount);
                } catch (NumberFormatException nfe) {
                    Toast.makeText(context, "Amount is not a number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DropDownItem selectedCategory;

                final String transaction_type;
                //Getting the selected items from the spinner
                if(mySwitch.isChecked()){
                    transaction_type = "Incomes";
                    selectedCategory = income_cat.get(categorySpinner.getSelectedItemPosition());
                }
                else{
                    transaction_type = "Expenses";
                    selectedCategory = expense_cat.get(categorySpinner.getSelectedItemPosition());
                }
                String category = selectedCategory.getText();
                String comment = et_comment.getText().toString();
                final double amountDouble = parseDouble(amount);
                Transaction new_transaction = new Transaction(date, category, amountDouble, comment);
                //Getting phone number and wallet from shared preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
                final String wallet_name = sharedPref.getString(getString(R.string.wallet_name),"Wallet Name");

                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child(phone_number).child("Wallets").child(wallet_name).child(transaction_type).push().setValue(new_transaction);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * Updating balance amount by the user input
                     * @param dataSnapshot
                     */
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
                        //Replace current fragment with TransactionFragment
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
}
