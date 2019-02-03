package com.bitirme.gitbusters.borkinroads;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

class DoggoBar extends RecyclerView.ViewHolder implements View.OnClickListener {

    public DoggoBar(View itemView) {
        super(itemView);
        Context context = itemView.getContext();
        ImageButton imageButtonView = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);
        imageButtonView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(v.getContext(), "Item Pressed = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
    }
}