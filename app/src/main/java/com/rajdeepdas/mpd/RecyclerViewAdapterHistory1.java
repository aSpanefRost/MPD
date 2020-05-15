package com.rajdeepdas.mpd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rajdeepdas on 30/04/20.
 */

public class RecyclerViewAdapterHistory1 extends RecyclerView.Adapter<RecyclerViewAdapterHistory1.History1ViewHolder> {

    private ArrayList<History1> histories1;
    private Context context;

    public RecyclerViewAdapterHistory1(Context context, ArrayList<History1> histories1)
    {
        this.histories1=histories1;
        this.context=context;

    }

    public class History1ViewHolder extends RecyclerView.ViewHolder
    {
        TextView enWord;


        public History1ViewHolder(View v)
        {
            super(v);
            enWord=(TextView)v.findViewById(R.id.en_word1);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String text = histories1.get(position).get_en_word();

                    Intent intent =new Intent(context,WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public History1ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history1_item_layout,parent,false);
        return new History1ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(History1ViewHolder holder, final int position)
    {
        holder.enWord.setText(histories1.get(position).get_en_word());



    }
    @Override
    public int getItemCount()
    {
        return histories1.size();
    }
}
