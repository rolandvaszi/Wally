package com.example.wally.Presenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wally.Model.OverviewItem;
import com.example.wally.R;
import com.example.wally.View.MainActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.OverviewViewHolder> {
    private ArrayList<OverviewItem> categories;
    private Context context;

    public OverviewAdapter(Context context, ArrayList<OverviewItem>categories) {
        this.context = context;
        this.categories = categories;
    }

    /**
     * Initializing adapter
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public OverviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_overview_recyclerview, parent, false);
        return new OverviewViewHolder(view);
    }

    /**
     * Populating recyclerView
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull OverviewViewHolder holder, int position) {
        //Initializing the container
        final Map<String, Integer> images = ((MainActivity) context).getImages();
        String category = categories.get(position).getCategory();
        //Adding data to recyclerView item holder
        holder.tv_category.setText(category);
        DecimalFormat df2 = new DecimalFormat("#.##");
        String amount = df2.format(categories.get(position).getAmount()) + " RON";
        holder.tv_amount.setText(amount);
        holder.img_category_icon.setBackgroundResource(images.get(holder.tv_category.getText().toString()));
        if(categories.get(position).getType().equals("Income")){
            holder.tv_amount.setTextColor(Color.WHITE);
        }
        else{
            holder.tv_amount.setTextColor(Color.rgb(229,57,53));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    //This class helps adapter with modelling a holder to add data to an item.
    class OverviewViewHolder extends RecyclerView.ViewHolder {
        ImageView img_category_icon;
        TextView tv_category, tv_amount;
        OverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category_icon = itemView.findViewById(R.id.img_category_icon);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_amount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
