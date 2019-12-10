package com.example.wally.Presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wally.R;
import com.example.wally.Model.Transaction;
import com.example.wally.View.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {
    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionsAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transactions_recyclerview, parent, false);
        return new TransactionsViewHolder(view);
    }

    /**
     * Populate recyclerView
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, final int position) {
        //Initializing the container
        final Map<String, Integer> images = ((MainActivity) context).getImages();
        final Transaction transaction = transactions.get(position);
        //Adding data to recyclerView item holder
        holder.tv_comment.setText(transaction.getComment());
        holder.tv_category.setText(transaction.getCategory());
        holder.tv_date.setText(transaction.getDate());
        holder.img_category.setBackgroundResource(images.get(holder.tv_category.getText().toString()));
        String amount;
        DecimalFormat df2 = new DecimalFormat("#.##");
        //Setting amount format depending on the category
        //(example: if the user ads an expense, the amount will be listed with "-").
        if(transaction.getType().equals("Expenses")) {
            amount = "-" + df2.format(transaction.getAmount()) + " RON";
            holder.tv_amount.setTextColor(Color.rgb(229,57,53));
        } else {
            amount = "+" + df2.format(transaction.getAmount()) + " RON";
            holder.tv_amount.setTextColor(Color.WHITE);
        }

        holder.tv_amount.setText(amount);
        //Adding trash button to an event listener. (delete transaction)
        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {

            /**
             * Setting click listener to the delete procedure
             * @param v
             */
            @Override
            public void onClick(View v) {
                //Dialog for interrogated delete
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("Are you sure you want to delete this transaction?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            /**
                             * If the user taps on "Yes", the transaction disappears
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Getting phone number and wallet from shared preferences
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                                String phone_number = sharedPref.getString(context.getString(R.string.phone_number),"Phone Number");
                                String wallet_name = sharedPref.getString(context.getString(R.string.wallet_name),"Wallet Name");
                                //Getting database instance
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                final DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name);
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    //Delete data from database.

                                    /**
                                     * Handling transaction delete
                                     * @param dataSnapshot
                                     */
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String balanceStr = dataSnapshot.child("Amount").getValue().toString();
                                        double balance = parseDouble(balanceStr);
                                        //Changing the amount of the balance depending on type.
                                        if(transaction.getType().equals("Expenses")){
                                            balance = balance + transaction.getAmount();
                                        } else {
                                            balance = balance - transaction.getAmount();
                                        }

                                        //Updating the new amount in database.
                                        myRef.child("Amount").setValue(balance);
                                        myRef.child(transaction.getType()).child(transaction.getId()).removeValue();
                                        //Remove transaction
                                        transactions.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, transactions.size());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            /**
                             * If the user taps on "No", the transaction remains
                             * @param dialogInterface
                             * @param i
                             */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                //Creating and showing the dialog.
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        });
    }

    /**
     * Getting the listed items count
     * @return
     */
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    //This class helps adapter with modelling a holder to add data to an item.
    class TransactionsViewHolder extends RecyclerView.ViewHolder {
        ImageView img_category;
        ImageButton imgBtnDelete;
        TextView tv_category, tv_comment, tv_amount, tv_date;

        TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category = itemView.findViewById(R.id.img_category);
            imgBtnDelete = itemView.findViewById(R.id.imgbtn_delete);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
