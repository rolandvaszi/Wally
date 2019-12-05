package com.example.wally.Presenter;

import com.example.wally.R;
import com.example.wally.View.LoginActivity;
import com.example.wally.View.MainActivity;
import com.example.wally.View.TransactionsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WalletsAdapter extends RecyclerView.Adapter<WalletsAdapter.WalletsViewHolder> {
    private ArrayList<String> wallets, amounts;
    private Context context;

    public WalletsAdapter(Context context, ArrayList<String> wallets, ArrayList<String> amounts) {
        this.context = context;
        this.wallets = wallets;
        this.amounts = amounts;
    }

    @NonNull
    @Override
    public WalletsAdapter.WalletsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallets_recyclerview, parent, false);
        return new WalletsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletsViewHolder holder, final int position) {
        final String wallet_name = wallets.get(position);
        holder.tv_wallet_name.setText(wallet_name);

        final String amount = amounts.get(position) + " RON";
        holder.tv_amount.setText(amount);

        holder.layout_select_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(context.getString(R.string.wallet_name), wallet_name);
                editor.apply();

                TextView tv_selected_wallet = ((MainActivity) context).findViewById(R.id.tv_selected_wallet);
                tv_selected_wallet.setText(wallet_name);

                String phone_number = sharedPref.getString(context.getString(R.string.phone_number),"Phone Number");

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name).child("Amount");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                FragmentTransaction frag_trans = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                frag_trans.replace(R.id.fragment_container,new TransactionsFragment());
                frag_trans.commit();
            }
        });

        holder.imgBtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("Are you sure you want to delete this wallet?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                                String phone_number = sharedPref.getString(context.getString(R.string.phone_number),"Phone Number");

                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets").child(wallet_name);
                                myRef.removeValue();

                                wallets.remove(position);
                                amounts.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, wallets.size());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public class WalletsViewHolder extends RecyclerView.ViewHolder {
        ImageView img_wallet_icon;
        TextView tv_wallet_name, tv_amount;
        ImageButton imgBtn_delete;
        ConstraintLayout layout_select_wallet;

        public WalletsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_wallet_icon = itemView.findViewById(R.id.img_wallet_icon);
            tv_wallet_name = itemView.findViewById(R.id.tv_wallet_name);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            imgBtn_delete = itemView.findViewById(R.id.imgBtn_delete);
            layout_select_wallet = itemView.findViewById(R.id.layout_select_wallet);
        }
    }
}
