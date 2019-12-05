package com.example.wally.View;

import android.annotation.SuppressLint;
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
import android.widget.Button;

import com.example.wally.Model.Transaction;
import com.example.wally.Presenter.TransactionsAdapter;
import com.example.wally.Presenter.WalletsAdapter;
import com.example.wally.R;
import com.example.wally.View.AddWalletFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class WalletsFragment extends Fragment {
    private Context context;


    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallets, container, false);

        // *** open AddWalletFragment when user clicks + ***

        FloatingActionButton btn_add_wallet = v.findViewById(R.id.add_wallet_fab);
        btn_add_wallet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction frag_trans = getFragmentManager().beginTransaction();
                frag_trans.replace(R.id.fragment_container,new AddWalletFragment());
                frag_trans.commit();
            }
        });

        // *** set wallet-recyclerview ***

        final RecyclerView recyclerView = v.findViewById(R.id.wallets_rv);
        recyclerView.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");
        final ArrayList<String> wallets = new ArrayList<>();
        final ArrayList<String> amounts = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallets.clear();
                amounts.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String wallet_name = ds.getKey(); //the wallet_name is the key
                    String amount = ds.child("Amount").getValue().toString();
                    wallets.add(wallet_name);
                    amounts.add(amount);
                }

                //Collections.sort(wallets);
                RecyclerView.Adapter adapter = new WalletsAdapter(context, wallets, amounts);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}
