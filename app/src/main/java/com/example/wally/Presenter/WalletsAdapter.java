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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WalletsAdapter extends RecyclerView.Adapter<WalletsAdapter.WalletsViewHolder> {
    private ArrayList<String> wallets;
    private Context context;

    public WalletsAdapter(Context context, ArrayList<String> wallets) {
        this.context = context;
        this.wallets = wallets;
    }

    @NonNull
    @Override
    public WalletsAdapter.WalletsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallets_recyclerview, parent, false);
        return new WalletsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletsViewHolder holder, int position) {
        final String wallet_name = wallets.get(position);
        holder.tv_wallet_name.setText(wallet_name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public class WalletsViewHolder extends RecyclerView.ViewHolder {
        ImageView img_wallet_icon;
        TextView tv_wallet_name;
        public WalletsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_wallet_icon = itemView.findViewById(R.id.img_wallet_icon);
            tv_wallet_name = itemView.findViewById(R.id.tv_wallet_name);
        }
    }
}
