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

        //Go back to WalletsFragment when user clicks "Add"
        Button btn_add_wallet = view.findViewById(R.id.add_bt);
        btn_add_wallet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText et_wallet = view.findViewById(R.id.wname_et);
                final EditText et_balance = view.findViewById(R.id.balance_et);
                //If the fields are empty, the program exits
                if(isEmpty(et_wallet.getText()) || isEmpty(et_balance.getText())){
                    Toast.makeText(context, "Empty field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String wallet_name = et_wallet.getText().toString();
                final String balance = et_balance.getText().toString();
                //Catching wrong data. Data must be a number.
                try {
                    double d = Double.parseDouble(balance);
                } catch (NumberFormatException nfe) {
                    Toast.makeText(context, "Initial balance is not a number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Saving phone number in SharedPreferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                String phone_number = sharedPref.getString(getString(R.string.phone_number),"Phone Number");
                //Initializing database.
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = db.getReference().child(phone_number).child("Wallets");
                //Adding wallet to the database.
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * Handling wallet add
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            String dsWalletName = ds.getKey();
                            assert dsWalletName != null;
                            //Gives a short message if the name exists
                            if(dsWalletName.equals(wallet_name)){
                                Toast.makeText(context, "This wallet-name already exists", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        myRef.child(wallet_name).child("Amount").setValue(balance);
                        //Replaces current fragment with WalletsFragment
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
