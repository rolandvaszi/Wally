package com.example.wally;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, final int position) {
        final Transaction transaction = transactions.get(position);
        holder.tv_comment.setText(transaction.getComment());
        holder.tv_category.setText(transaction.getCategory());
        holder.tv_date.setText(transaction.getDate());
        String amount;
        DecimalFormat df2 = new DecimalFormat("#.##");
        if(transaction.getType().equals("Expenses")) {
            amount = "-" + df2.format(transaction.getAmount()) + " RON";
            holder.tv_amount.setTextColor(Color.RED);
        }
        else{
            amount = "+" + df2.format(transaction.getAmount()) + " RON";
            holder.tv_amount.setTextColor(Color.BLUE);
        }
        holder.tv_amount.setText(amount);

        holder.imgbtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                String phone_number = sharedPref.getString(context.getString(R.string.phone_number),"Phone Number");

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference myRef = db.getReference().child(phone_number).child(transaction.getType()).child(transaction.getId());
                myRef.removeValue();

                transactions.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, transactions.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class TransactionsViewHolder extends RecyclerView.ViewHolder {
        ImageView img_category;
        ImageButton imgbtn_delete;
        TextView tv_category, tv_comment, tv_amount, tv_date;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category = itemView.findViewById(R.id.img_category);
            imgbtn_delete = itemView.findViewById(R.id.imgbtn_delete);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
