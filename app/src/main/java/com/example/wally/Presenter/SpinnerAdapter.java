package com.example.wally.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wally.Model.DropDownItem;
import com.example.wally.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<DropDownItem> {
    private int groupId;
    private ArrayList<DropDownItem> list;
    private LayoutInflater inflater;

    public SpinnerAdapter(Context context, int groupId, int id, ArrayList<DropDownItem>
            list){
        super(context,id,list);
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupId=groupId;
    }

    /**
     * Making view for the spinner.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent ){
        @SuppressLint("ViewHolder") View itemView=inflater.inflate(groupId,parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.img);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView=(TextView)itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    /**
     * Initializing drop down for spinner
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);
    }
}