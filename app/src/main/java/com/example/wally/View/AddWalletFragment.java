package com.example.wally.View;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wally.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class AddWalletFragment extends Fragment {
    private Context context;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_wallet, container, false);

        // *** go back to WalletsFragment when user clicks Add ***

        Button btn_add_wallet = view.findViewById(R.id.add_bt);
        btn_add_wallet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText et_walletname = view.findViewById(R.id.wname_et);
                final EditText et_balance = view.findViewById(R.id.balance_et);

                if(isEmpty(et_walletname.getText()) || isEmpty(et_balance.getText())){
                    Toast.makeText(context, "Empty field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            String walletname = ds.getKey();
                            if(walletname.equals(et_walletname.getText().toString())){
                                Toast.makeText(context, "This wallet-name already exists", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        myRef.child(et_walletname.getText().toString()).child("Amount").setValue(et_balance.getText().toString());

                        FragmentTransaction frag_trans = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                        frag_trans.replace(R.id.fragment_container,new WalletsFragment());
                        frag_trans.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        return view;
    }


}
